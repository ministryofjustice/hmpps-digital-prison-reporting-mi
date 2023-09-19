package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.ConfiguredApiController.FiltersPrefix.FILTERS_PREFIX
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ConfiguredApiService

@Validated
@RestController
@Tag(name = "Configured Data API")
class ConfiguredApiController(val configuredApiService: ConfiguredApiService) {
  object FiltersPrefix {
    const val FILTERS_PREFIX = "filters."
  }

  @GetMapping("/{reportId}/{reportVariantId}")
  @Operation(
    description = "Returns the dataset for the given report ID and report variant ID filtered by the filters provided in the query.",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun configuredApiDataset(
    @RequestParam(defaultValue = "1")
    @Min(1)
    selectedPage: Long,
    @RequestParam(defaultValue = "10")
    @Min(1)
    pageSize: Long,
    @RequestParam sortColumn: String?,
    @RequestParam(defaultValue = "false") sortedAsc: Boolean,
    @Parameter(
      description = "The filter query parameters have to start with the prefix \"$FILTERS_PREFIX\" followed by the name of the filter.",
      example = "filters.date.start=2023-04-25",
    )
    @RequestParam
    allQueryParams: Map<String, String>,
    @PathVariable("reportId") reportId: String,
    @PathVariable("reportVariantId") reportVariantId: String,
  ): List<Map<String, Any>> {
    return configuredApiService.validateAndFetchData(reportId, reportVariantId, filtersOnly(allQueryParams), selectedPage, pageSize, sortColumn, sortedAsc)
  }

  private fun filtersOnly(filters: Map<String, String>): Map<String, String> {
    return filters.entries
      .filter { it.key.startsWith(FILTERS_PREFIX) }
      .filter { it.value.isNotBlank() }
      .associate { (k, v) -> k.removePrefix(FILTERS_PREFIX) to v }
  }
}
