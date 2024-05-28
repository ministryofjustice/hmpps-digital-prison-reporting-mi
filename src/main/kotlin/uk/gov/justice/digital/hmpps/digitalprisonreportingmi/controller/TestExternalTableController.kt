package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient
import software.amazon.awssdk.services.redshiftdata.model.DescribeStatementRequest
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementRequest
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementResponse
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Validated
@RestController
@Hidden
class TestExternalTableController(
  val redshiftDataClient: RedshiftDataClient,
  val executeStatementRequestBuilder: ExecuteStatementRequest.Builder,
) {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  val cache = ConcurrentHashMap<String, String>()

  @Hidden
  @GetMapping("/test/async/execute")
  @Operation(
    description = "Test external table creation.",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun createExternalTable(authentication: Authentication): ResponseEntity<Response> {
    val tableId = "_" + UUID.randomUUID().toString().replace("-", "_")

    val requestBuilder = executeStatementRequestBuilder
      .sql(
        "CREATE EXTERNAL TABLE reports.$tableId " +
          "STORED AS parquet " +
          "LOCATION 's3://dpr-working-development/reports/$tableId/' " +
          "AS (SELECT * FROM datamart.domain.movement_movement)",
      )
    val statementRequest: ExecuteStatementRequest = requestBuilder.build()

    val response: ExecuteStatementResponse = redshiftDataClient.executeStatement(statementRequest)
    cache[tableId] = response.id()
    log.info("Execution ID: {}", response.id())
    log.info("External table ID: {}", tableId)

    return ResponseEntity
      .status(HttpStatus.OK)
      .body(
        Response(tableId, response.id()),
      )
  }

  @Hidden
  @GetMapping("/test/async/{statementId}/status")
  @Operation(
    description = "Test external table creation status.",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun getTestStatementStatus(
    @PathVariable("statementId") statementId: String,
    authentication: Authentication,
  ): ResponseEntity<StatusResponse> {
    val statementRequest = DescribeStatementRequest.builder()
      .id(statementId)
      .build()
    val describeStatementResponse = redshiftDataClient.describeStatement(statementRequest)
    return ResponseEntity
      .status(HttpStatus.OK)
      .body(
        StatusResponse(
          status = describeStatementResponse.statusAsString(),
          duration = describeStatementResponse.duration(),
          queryString = describeStatementResponse.queryString(),
          resultRows = describeStatementResponse.resultRows(),
          resultSize = describeStatementResponse.resultSize(),
          dbUser = describeStatementResponse.dbUser(),
          error = describeStatementResponse.error(),
        ),
      )
  }

  data class Response(val tableId: String, val statementId: String)
  data class StatusResponse(
    val status: String,
    val duration: Long,
    val queryString: String,
    val resultRows: Long,
    val resultSize: Long?,
    val dbUser: String,
    val error: String? = null,
  )
}
