package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@Tag(name = "Test Endpoint")
@ConditionalOnProperty("dpr.lib.aws.sts.enabled", havingValue = "true")
class ATestController {
  @GetMapping("/test/endpoint")
  @Operation(
    description = "Testing",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun testEndpoint(authentication: Authentication): String {
    return "Test"
  }
}