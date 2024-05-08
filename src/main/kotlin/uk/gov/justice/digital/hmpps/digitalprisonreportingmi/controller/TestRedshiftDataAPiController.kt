package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementRequest
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementResponse
import software.amazon.awssdk.services.redshiftdata.model.SqlParameter

@Validated
@RestController
@Hidden
class TestRedshiftDataAPiController(
  val redshiftDataClient: RedshiftDataClient,
  val executeStatementRequestBuilder: ExecuteStatementRequest.Builder,
) {
  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  @Hidden
  @GetMapping("/test/aws/connectivity")
  @Operation(
    description = "Debug Endpoint",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun awsConnectivity(authentication: Authentication): String {
//    val secretsClient = SecretsManagerClient.builder()
//      .region(Region.EU_WEST_2)
//      .build()
//    val valueRequest: GetSecretValueRequest = GetSecretValueRequest.builder()
//      .secretId("arn:aws:secretsmanager:eu-west-2:771283872747:secret:dpr-redshift-secret-development-rLHcQZ")
//      .build()
//    val valueResponse: GetSecretValueResponse = secretsClient.getSecretValue(valueRequest)
    val statementRequest: ExecuteStatementRequest = executeStatementRequestBuilder
      .database("datamart")
      .clusterIdentifier("dpr-redshift-development")
      .secretArn("arn:aws:secretsmanager:eu-west-2:771283872747:secret:dpr-redshift-secret-development-rLHcQZ")
      .sql(
        "SELECT * FROM datamart.domain.movement_movement where id=:id",
      )
      .parameters(SqlParameter.builder().name("id").value("405713.94").build())
      .build()

    val response: ExecuteStatementResponse = redshiftDataClient.executeStatement(statementRequest)
    log.info("Execution ID: {}", response.id())
    return "Response ID is: ${response.id()}"
  }
}
