package uk.gov.justice.digital.hmpps.digitalprisonreportingmi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
  exclude = [
    io.sentry.spring.boot.jakarta.SentryAutoConfiguration::class,
  ],
)
class DigitalPrisonReportingMi

fun main(args: Array<String>) {
  runApplication<DigitalPrisonReportingMi>(*args)
}
