package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.Count
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.EstablishmentService

@RestController
@Tag(name = "Establishments API")
class EstablishmentController(val establishmentService: EstablishmentService) {
  @Operation(description = "Gets a count of establishments", security = [SecurityRequirement(name = "bearer-jwt") ])
  @GetMapping("/establishments/count")
  fun establishmentsCount(): Count {
    return Count(establishmentService.establishmentsCount())
  }
}
