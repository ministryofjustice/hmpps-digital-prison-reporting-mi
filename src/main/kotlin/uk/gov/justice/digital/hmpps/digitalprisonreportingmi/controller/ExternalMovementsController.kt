package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.EstablishmentsService
import java.util.Collections.singletonMap

@RestController
class ExternalMovementsController(val establishmentsService: EstablishmentsService) {

  @GetMapping("/external-movements/count")
  fun stubbedCount(): Map<String, Int> {
    return singletonMap("count", 501)
  }
  @GetMapping("/establishments/count")
  fun establishmentsCount(): Map<String, Long> {
    return singletonMap("count", establishmentsService.establishmentsCount())
  }
}
