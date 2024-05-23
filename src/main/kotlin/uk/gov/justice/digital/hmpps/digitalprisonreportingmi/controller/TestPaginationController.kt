package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
import software.amazon.awssdk.core.interceptor.ExecutionAttribute
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient
import software.amazon.awssdk.services.redshiftdata.model.ColumnMetadata
import software.amazon.awssdk.services.redshiftdata.model.Field
import software.amazon.awssdk.services.redshiftdata.model.GetStatementResultRequest
import software.amazon.awssdk.services.redshiftdata.model.GetStatementResultResponse
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.DprAuthAwareAuthenticationToken
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Validated
@RestController
@Hidden
class TestPaginationController(val redshiftDataClient: RedshiftDataClient) {

  @Hidden
  @GetMapping("/test/pagination/statements/{statementId}")
  @Operation(
    description = "Test pagination.",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun paginate(
    @PathVariable("statementId") statementId: String,
    @RequestParam("nextToken", required = false)
    nextToken: String?,
    authentication: Authentication,
  ): ResponseEntity<Response> {
    val authToken = authentication as DprAuthAwareAuthenticationToken
    val requestBuilder = GetStatementResultRequest.builder()
    requestBuilder.id(statementId)
    nextToken?.let { requestBuilder.nextToken(it) }
    val overrideConfig: AwsRequestOverrideConfiguration.Builder = AwsRequestOverrideConfiguration.builder()
    overrideConfig.putExecutionAttribute(ExecutionAttribute("maxResults"), 2)
    requestBuilder.overrideConfiguration(overrideConfig.build())
    val statementRequest: GetStatementResultRequest = requestBuilder
      .build()
    val resultStatementResponse: GetStatementResultResponse = redshiftDataClient
      .getStatementResult(statementRequest)

    return ResponseEntity
      .status(HttpStatus.OK)
      .body(
        Response(resultStatementResponse.totalNumRows(), resultStatementResponse.hasRecords(), resultStatementResponse.nextToken()),
      )
  }

  data class Response(val totalNumRows: Long, val hasRecords: Boolean, val nextToken: String)

  private fun extractRecords(resultStatementResponse: GetStatementResultResponse) =
    resultStatementResponse.records().map { record ->
      record.mapIndexed { index, field -> extractFieldValue(field, resultStatementResponse.columnMetadata()[index]) }
        .toMap()
    }

  private fun extractFieldValue(field: Field, columnMetadata: ColumnMetadata): Pair<String, Any?> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val value = when (columnMetadata.typeName()) {
      "varchar" -> field.stringValue()
      "int8" -> field.longValue()
      "timestamp" -> LocalDateTime.parse(field.stringValue(), formatter)
      // This will need to be extended to support more date types when required in the future.
      else -> field.stringValue()
    }
    return columnMetadata.name() to value
  }
}
