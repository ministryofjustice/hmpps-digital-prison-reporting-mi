package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient
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
    val tableId = UUID.randomUUID().toString()
    val requestBuilder = executeStatementRequestBuilder
      .sql(
        "CREATE EXTERNAL SCHEMA IF NOT EXISTS reports from data catalog " +
          "database 'reports' " +
          "iam_role 'arn:aws:iam::771283872747:role/dpr-redshift-spectrum-role' " +
          "create external database if not exists; " +
          "CREATE EXTERNAL TABLE \"reports.$tableId\" " +
          "STORED AS parquet " +
          "LOCATION 'S3://dpr-report-spill-dev/$tableId/' " +
          "AS (SELECT * FROM datamart.domain.movement_movement limit 300000)",
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

  data class Response(val tableId: String, val statementId: String)
}
