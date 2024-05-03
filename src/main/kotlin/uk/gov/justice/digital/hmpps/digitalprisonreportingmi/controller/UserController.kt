package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.DprAuthAwareAuthenticationToken

@Validated
@RestController
@Tag(name = "User API")
class UserController() {

  @GetMapping("/user/caseload/active")
  @Operation(
    description = "Gets a user's active caseloads",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun definitions(authentication: Authentication): List<String> {
    val authToken = authentication as DprAuthAwareAuthenticationToken
    return authToken.getCaseLoads()
  }

  @Hidden
  @GetMapping("/test/aws/connectivity")
  @Operation(
    description = "Debug Endpoint",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun awsConnectivity(authentication: Authentication): String {
    val secretsClient = SecretsManagerClient.builder()
      .region(Region.EU_WEST_2)
      .build()
    val valueRequest: GetSecretValueRequest = GetSecretValueRequest.builder()
      .secretId("arn:aws:secretsmanager:eu-west-2:771283872747:secret:dpr-redshift-secret-development-rLHcQZ")
      .build()
    val valueResponse: GetSecretValueResponse = secretsClient.getSecretValue(valueRequest)
    val secret = valueResponse.secretString()
    return "Success"
  }
}
