package uk.gov.justice.digital.hmpps.digitalprisonreportingmi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// @ConfigurationPropertiesScan
// @ComponentScan("uk.gov.justice.digital.hmpps.digitalprisonreportingmi", "uk.gov.justice.digital.hmpps.digitalprisonreportinglib")
@SpringBootApplication
class DigitalPrisonReportingMi

fun main(args: Array<String>) {
  runApplication<DigitalPrisonReportingMi>(*args)
}
