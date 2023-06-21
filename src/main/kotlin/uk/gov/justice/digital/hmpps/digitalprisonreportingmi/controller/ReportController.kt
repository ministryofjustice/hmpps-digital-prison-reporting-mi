package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.intellij.lang.annotations.Pattern
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.Count
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ReportService

@RestController
@Tag(name = "Reporting API")
class ReportController(val reportService: ReportService) {
  @Operation(description = "Gets a count of rows in a table", security = [SecurityRequirement(name = "bearer-jwt") ])
  @GetMapping("/{domain}/{table}/count")
  fun count(
    @PathVariable @Pattern("^[a-zA-Z\\d]+$") domain: String,
    @PathVariable @Pattern("^[a-zA-Z\\d]+$") table: String,
  ): Count {
    return Count(reportService.count(domain, table))
  }
}
