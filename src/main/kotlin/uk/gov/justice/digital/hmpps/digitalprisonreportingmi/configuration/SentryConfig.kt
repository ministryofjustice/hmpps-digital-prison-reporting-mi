package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import io.sentry.SentryOptions
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnClass(name = arrayOf("org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration"))
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
