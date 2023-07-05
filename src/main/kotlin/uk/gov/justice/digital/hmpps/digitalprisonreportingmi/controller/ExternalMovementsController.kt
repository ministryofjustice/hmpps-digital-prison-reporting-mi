package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.Count
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ExternalMovementService

private const val FILTER_PARAM_PREFIX = "filter."

@Validated
@RestController
@Tag(name = "External Movements API")
class ExternalMovementsController(val externalMovementService: ExternalMovementService) {

  @GetMapping("/external-movements/count")
  @Operation(
    description = "Gets a count of external movements (mocked)",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun stubbedCount(
    @RequestParam allParams: Map<String, String>,
  ): Count {
    return externalMovementService.count(extractFilters(allParams))
  }

  @GetMapping("/external-movements")
  @Operation(
    description = "Gets a list of external movements (mocked)",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun stubbedExternalMovements(
    @RequestParam("selectedPage")
    @Min(1)
    selectedPage: Long?,
    @RequestParam("pageSize")
    @Min(1)
    pageSize: Long?,
    @RequestParam("sortColumn") sortColumn: String?,
    @RequestParam("sortedAsc") sortedAsc: Boolean?,
    @RequestParam allParams: Map<String, String>,
  ): List<ExternalMovement> {
    return externalMovementService.list(
      selectedPage = selectedPage ?: 1,
      pageSize = pageSize ?: 10,
      sortColumn = sortColumn ?: "date",
      sortedAsc = sortedAsc ?: false,
      filters = extractFilters(allParams)
    )
  }

  private fun extractFilters(allParams: Map<String, String>): Map<ExternalMovementFilter, String> {
    return allParams
      .filterKeys { it.startsWith(FILTER_PARAM_PREFIX) }
      .filterValues { it.isNotBlank() }
      .mapKeys { it.key.replace(FILTER_PARAM_PREFIX, "").uppercase() }
      .filterKeys { key -> ExternalMovementFilter.values().map { it.toString() }.contains(key) }
      .mapKeys { ExternalMovementFilter.valueOf(it.key) }
  }
}
