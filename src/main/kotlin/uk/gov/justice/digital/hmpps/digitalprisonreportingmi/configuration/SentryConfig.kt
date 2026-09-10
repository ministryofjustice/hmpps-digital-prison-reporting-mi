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
  override fun execute(event: SentryEvent, hint: Hint): SentryEvent? {
    val filteredExceptionsEvent = filterSentryExceptions(event)
    return filteredExceptionsEvent
  }

  private fun filterSentryExceptions(event: SentryEvent): SentryEvent{
    val matchedExceptions =
      event.exceptions
        ?.filter { it.isIgnored() }
        ?.toMutableSet()
        ?: mutableSetOf()

    val queue = ArrayDeque(matchedExceptions)

    while (queue.isNotEmpty()) {
      val current = queue.removeFirst()

      val related = event.exceptions
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

    event.exceptions?.removeAll(matchedExceptions)

    if (event.exceptions.isNullOrEmpty()) {
      event.exceptions = null
    }
    return event
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