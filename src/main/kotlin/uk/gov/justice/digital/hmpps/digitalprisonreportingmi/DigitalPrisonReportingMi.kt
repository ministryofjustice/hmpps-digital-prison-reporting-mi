package uk.gov.justice.digital.hmpps.digitalprisonreportingmi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.config.JpaRepositoryConfiguration

@SpringBootApplication
@Import(JpaRepositoryConfiguration::class)
class DigitalPrisonReportingMi

fun main(args: Array<String>) {
  runApplication<DigitalPrisonReportingMi>(*args)
}
