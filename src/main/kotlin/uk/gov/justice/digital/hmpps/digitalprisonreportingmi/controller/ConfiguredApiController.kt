package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.DataSet
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ConfiguredApiService
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ReportDefinitionService

@Validated
@RestController
@Tag(name = "Configured Data API")
class ConfiguredApiController(val configuredApiService: ConfiguredApiService) {

  @GetMapping("/{reportId}/{dataSetId}")
  @Operation(
    description = "Returns the dataset for the given report ID and dataSetId ID filtered by the filters provided in the query.",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun configuredApiDataset(
    @PathVariable("reportId") reportId: String,
    @PathVariable("dataSetId") dataSetId: String,
    @RequestParam ("reportVariantId") reportVariantId: String,
    @RequestParam filters: Map<String, String>
    ): List<Map<String,String>> {
    return configuredApiService.validateAndFetchData(reportId, dataSetId, reportVariantId, filters)
  }
}