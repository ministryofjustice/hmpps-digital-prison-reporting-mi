package uk.gov.justice.digital.hmpps.digitalprisonreportingmi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ConfigurationPropertiesScan
@ComponentScan("uk.gov.justice.digital.hmpps.digitalprisonreportingmi", "uk.gov.justice.digital.hmpps.digitalprisonreportinglib")
class DigitalPrisonReportingMi

fun main(args: Array<String>) {
  runApplication<DigitalPrisonReportingMi>(*args)
}
