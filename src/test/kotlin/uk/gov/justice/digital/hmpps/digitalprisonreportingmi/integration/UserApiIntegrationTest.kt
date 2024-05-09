package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.web.util.UriBuilder
@Disabled
class UserApiIntegrationTest : IntegrationTestBase() {

  @Test
  fun `User's active caseloads are returned successfully`() {
    webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/user/caseload/active")
          .build()
      }
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody()
      .json("[\"LWSTMC\"]")
  }
}
