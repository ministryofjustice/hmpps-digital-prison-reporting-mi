package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.EstablishmentService
import java.util.*
@RestController
class EstablishmentController(val establishmentService: EstablishmentService) {
  @GetMapping("/establishments/count")
  fun establishmentsCount(): Map<String, Long> {
    return mapOf("count" to establishmentService.establishmentsCount())
  }
}
