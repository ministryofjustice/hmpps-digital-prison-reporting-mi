package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.constraints.Min
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
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

  @Autowired
  lateinit var jdbcTemplate: JdbcTemplate

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
        """
          CREATE EXTERNAL TABLE reports.$tableId 
          "STORED AS parquet 
          "LOCATION 's3://dpr-working-development/reports/$tableId/' 
          "AS (SELECT * FROM datamart.domain.movement_movement);
        """.trimIndent(),
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
  @GetMapping("/test/async/result/{tableId}/")
  @Operation(
    description = "Test external table results.",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun getTestStatementStatus(
    @PathVariable("tableId") tableId: String,
    @RequestParam(defaultValue = "1")
    @Min(1)
    selectedPage: Long,
    @RequestParam(defaultValue = "10")
    @Min(1)
    pageSize: Long,
    authentication: Authentication,
  ): List<Map<String, Any?>> {
    return jdbcTemplate.queryForList("SELECT * FROM $tableId limit $pageSize OFFSET ($selectedPage - 1) * $pageSize;")
  }

  data class Response(val tableId: String, val statementId: String)
}
