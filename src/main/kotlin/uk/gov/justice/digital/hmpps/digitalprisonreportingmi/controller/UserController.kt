package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.DprAuthAwareAuthenticationToken

@Validated
@RestController
@Tag(name = "User API")
class UserController {

  @GetMapping("/user/caseload/active")
  @Operation(
    description = "Gets a user's active caseloads",
    security = [ SecurityRequirement(name = "bearer-jwt") ],
  )
  fun definitions(authentication: Authentication): List<String> {
    val authToken = authentication as DprAuthAwareAuthenticationToken
    return authToken.getCaseLoads()
  }
}
