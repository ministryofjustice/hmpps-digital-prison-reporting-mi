package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.Count
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter.DIRECTION
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter.END_DATE
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter.START_DATE
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementModel
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ReportDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ExternalMovementService
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ReportDefinitionService
import java.time.LocalDate

@Validated
@RestController
@Tag(name = "Report Definition API")
class ReportDefinitionController(val reportDefinitionService: ReportDefinitionService) {

  @GetMapping("/definition")
  @Operation(
    description = "Gets all report definitions",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun definitions(): List<ReportDefinition> {
    return reportDefinitionService.getListForUser()
  }

}
