package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import io.sentry.Hint
import io.sentry.SentryEvent
import io.sentry.SentryOptions
import io.sentry.protocol.SentryException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class SentryConfig {
  @Bean
  fun ignoreHealthRequests() = SentryOptions.BeforeSendTransactionCallback { transaction, _ ->
    transaction.transaction?.let { if (it.startsWith("GET /health") or it.startsWith("GET /info")) null else transaction }
  }

  @Bean
  fun transactionSampling() = SentryOptions.TracesSamplerCallback { context ->
    context.customSamplingContext?.let {
      val request = it["request"] as HttpServletRequest

      if (request.method == "GET") 0.001 else 0.02
    }
  }
}

@Component
class CustomBeforeSendCallback : SentryOptions.BeforeSendCallback {

  private val regexOptions = setOf(
    RegexOption.DOT_MATCHES_ALL,
    RegexOption.MULTILINE,
    RegexOption.IGNORE_CASE,
  )

  private val ignoredExceptions = mapOf(
    $$"WebClientResponseException$NotFound" to emptyList(),
    "AsyncRequestNotUsableException" to listOf(Regex(".*flushBuffer.*", regexOptions), Regex(".*Broken pipe.*", regexOptions)),
    "TableExpiredException" to emptyList(),
    "ValidationException" to listOf(
      Regex("Invalid report id provided.*", regexOptions),
      Regex("Could not retrieve the query result as it has expired after 24 hours", regexOptions),
    ),
    "IllegalArgumentException" to listOf(
      Regex("Invalid report ID.*", regexOptions),
    ),
    "ExecutionStatementNotFound" to listOf(
      Regex("QueryExecution.*was not found", regexOptions),
    ),
    "Exception" to listOf(
      Regex("Cannot pipe to a closed or destroyed stream", regexOptions),
    ),
    "UserAuthorisationException" to listOf(
      Regex("User does not have correct authorisation", regexOptions),
    ),
    "WebClientRequestException" to listOf(
      Regex("Connection reset by peer", regexOptions),
      Regex("Connection prematurely closed.*", regexOptions),
    ),
    "NoDataAvailableException" to listOf(
      Regex(".*active caseload", regexOptions),
    ),
    $$"FluxOnAssembly$OnAssemblyException" to listOf(
      Regex(".*Error.*/users/.*", regexOptions),
      Regex(".*Error.*/caseloads.*", regexOptions),
      Regex(".*404.*/users/.*", regexOptions),
    ),
  )
  override fun execute(event: SentryEvent, hint: Hint): SentryEvent? = event.filterSentryExceptions().enhanceSentryExceptions()

  fun SentryEvent?.enhanceSentryExceptions(): SentryEvent? {
    if (this == null) return this
    val exceptions = exceptions
    if (exceptions.isNullOrEmpty()) return this

    exceptions.forEach { it.enhanceIfNeeded() }
    return this
  }

  private fun SentryEvent?.filterSentryExceptions(): SentryEvent? {
    if (this == null) return this

    val numExceptionsOriginal = exceptions?.size

    val matchedExceptions =
      exceptions
        ?.filter { it.isIgnored() }
        ?.toMutableSet()
        ?: mutableSetOf()

    val queue = ArrayDeque(matchedExceptions)

    while (queue.isNotEmpty()) {
      val current = queue.removeFirst()

      val related = exceptions
        ?.filterNot { it in matchedExceptions }
        ?.filter {
          it.mechanism?.exceptionId == current.mechanism?.parentId ||
            it.mechanism?.parentId == current.mechanism?.exceptionId
        }
        .orEmpty()

      related.forEach {
        matchedExceptions += it
        queue += it
      }
    }

    exceptions?.removeAll(matchedExceptions)

    if (exceptions.isNullOrEmpty()) {
      exceptions = null
    }

    if (exceptions?.isEmpty() == true && numExceptionsOriginal != null && numExceptionsOriginal > 0) {
      return null
    }
    return this
  }

  data class ExceptionToEnhance(
    val exceptionName: String,
    val valueRegexes: List<Regex>,
    val valueEnhancement: String,
  )

  private val exceptionsToEnhance = listOf(
    ExceptionToEnhance(
      "UncategorizedSQLException",
      listOf(Regex(".*EntityNotFoundException.*glue.*", regexOptions)),
      "Potential contract violation - glue catalog or source table missing!\n"
    ),
    ExceptionToEnhance(
      "UncategorizedSQLException",
      listOf(Regex(".*WLM abort.*rule_query_execution.*", regexOptions)),
      "Query timed out!\n"
    ),
    ExceptionToEnhance(
      "UncategorizedSQLException",
      listOf(Regex(".*DeltaManifest.*NoSuchKey.*", regexOptions)),
      "DeltaLake table manifest is missing -- likely means ingestion missed it!\n"
    ),
    ExceptionToEnhance(
      "BadSqlGrammarException",
      listOf(Regex(".*Invalid operation.*", regexOptions)),
      "Likely invalid SQL in query!\n"
    ),
  )

  private fun SentryException.enhanceIfNeeded() {
    val exception = exceptionsToEnhance.find {
        it.exceptionName == type
          && value != null
          && it.valueRegexes.any { regex -> regex.matches(value!!) }
      }

    if (exception == null) return

    value = """
      === LIKELY CAUSE ===
      ${exception.valueEnhancement}
      === ORIGINAL ERROR ===
      $value
    """.trimIndent()
  }

  private fun SentryException.isIgnored(): Boolean {
    val patterns = ignoredExceptions[type] ?: return false

    if (patterns.isEmpty()) {
      return true
    }

    return value?.let { message ->
      patterns.any { it.matches(message) }
    } ?: false
  }
}
