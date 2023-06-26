package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.Count
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ExternalMovementService

@RestController
@Tag(name = "External Movements API")
class ExternalMovementsController(val externalMovementService: ExternalMovementService) {

  @GetMapping("/external-movements/count")
  @Operation(
    description = "Gets a count of external movements (mocked)",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun stubbedCount(): Count {
    return Count(501)
  }

  @GetMapping("/external-movements")
  @Operation(
    description = "Gets a count of external movements (mocked)",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun stubbedExternalMovements(@RequestParam("selectedPage") selectedPage: Long,
                               @RequestParam("pageSize") pageSize: Long,
                               @RequestParam("sortColumn") sortColumn: String,
                               @RequestParam("sortedAsc") sortedAsc: Boolean,
                               ): List<ExternalMovement> {
    return externalMovementService.externalMovements(selectedPage, pageSize, sortColumn, sortedAsc)
  }
}
