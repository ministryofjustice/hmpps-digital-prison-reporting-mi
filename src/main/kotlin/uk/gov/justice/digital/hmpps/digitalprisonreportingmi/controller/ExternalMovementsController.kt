package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ExternalMovementsController() {

  @GetMapping("/external-movements/count")
  fun stubbedCount(): Map<String, Int> {
    return mapOf("count" to 501)
  }
}
