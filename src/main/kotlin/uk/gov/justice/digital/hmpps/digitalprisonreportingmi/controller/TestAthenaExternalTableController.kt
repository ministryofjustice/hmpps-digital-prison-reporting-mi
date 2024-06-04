package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.services.athena.AthenaClient
import software.amazon.awssdk.services.athena.model.AthenaException
import software.amazon.awssdk.services.athena.model.GetQueryExecutionRequest
import software.amazon.awssdk.services.athena.model.QueryExecutionContext
import software.amazon.awssdk.services.athena.model.ResultConfiguration
import software.amazon.awssdk.services.athena.model.StartQueryExecutionRequest
import java.util.UUID

@Validated
@RestController
@Hidden
class TestAthenaExternalTableController(
  val athenaClient: AthenaClient,
) {

  @Autowired
  lateinit var jdbcTemplate: JdbcTemplate

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  @Hidden
  @GetMapping("/test/athena/async/execute")
  @Operation(
    description = "Test external table creation.",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun createExternalTable(authentication: Authentication): ResponseEntity<Response> {
    val tableId = "_" + UUID.randomUUID().toString().replace("-", "_")
    val queryString =
      """
          CREATE TABLE AwsDataCatalog.reports.$tableId 
          WITH (
            format = 'PARQUET'
          )
          AS (
            SELECT * FROM TABLE(system.query(query => 'SELECT * FROM OMS_OWNER.AGENCY_INTERNAL_LOCATIONS WHERE ROWNUM <= 5'))
          ) 
      """.trimIndent()
    val queryExecutionId = submitAthenaQuery(tableId, queryString)
    log.info("Athena Execution ID: {}", queryExecutionId)
    log.info("Athena External table ID: {}", tableId)

    return ResponseEntity
      .status(HttpStatus.OK)
      .body(
        Response(tableId, queryExecutionId),
      )
  }

  @Hidden
  @GetMapping("/test/athena/async/{statementId}/status")
  @Operation(
    description = "Test external table creation.",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun getTestAthenaStatementStatus(@PathVariable("statementId") statementId: String, authentication: Authentication): ResponseEntity<TestStatus> {
    val getQueryExecutionRequest = GetQueryExecutionRequest.builder()
      .queryExecutionId(statementId)
      .build()

    val getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest)
    val queryState = getQueryExecutionResponse.queryExecution().status().state()
    val stateChangeReason = getQueryExecutionResponse
      .queryExecution().status().stateChangeReason() ?: ""
    val error = getQueryExecutionResponse
      .queryExecution().status().athenaError()
    val errorMessage = if (error != null) error.errorMessage() else ""
    return ResponseEntity
      .status(HttpStatus.OK)
      .body(
        TestStatus(queryState.toString(), stateChangeReason, errorMessage),
      )
  }

  fun submitAthenaQuery(tableId: String, queryString: String): String {
    try {
      // The QueryExecutionContext allows us to set the database.
      val queryExecutionContext = QueryExecutionContext.builder()
        .database("DIGITAL_PRISON_REPORTING")
        .catalog("nomis")
        .build()

      // The result configuration specifies where the results of the query should go.
      val resultConfiguration = ResultConfiguration.builder()
        .outputLocation("s3://dpr-working-development/reports/$tableId/")
        .build()
      val startQueryExecutionRequest = StartQueryExecutionRequest.builder()
        .queryString(queryString)
        .queryExecutionContext(queryExecutionContext)
        .resultConfiguration(resultConfiguration)
        .build()
      val startQueryExecutionResponse = athenaClient
        .startQueryExecution(startQueryExecutionRequest)
      return startQueryExecutionResponse.queryExecutionId()
    } catch (e: AthenaException) {
      e.printStackTrace()
    }
    return ""
  }
  data class Response(val tableId: String, val statementId: String)
  data class TestStatus(val status: String, val stateChangeReason: String, val error: String)
}
