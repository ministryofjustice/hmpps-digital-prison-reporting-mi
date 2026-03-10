package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.hmpps.kotlin.auth.authorisedWebClient
import java.time.Duration

@TestConfiguration
class TestWebClientConfiguration(
  @Value("\${dpr.lib.manage-users.host}")
  private val manageUsersApiUri: String,
  @Value("\${api.timeout:20s}")
  private val healthTimeout: Duration,
) {

  @Bean
  fun authorizedClientManager() = mock<OAuth2AuthorizedClientManager>()

  @Bean
  fun manageUsersWebClient(
    authorizedClientManager: OAuth2AuthorizedClientManager,
    builder: WebClient.Builder,
  ): WebClient = builder.authorisedWebClient(
    authorizedClientManager,
    registrationId = "DPR_LIB_API",
    url = manageUsersApiUri,
    healthTimeout,
  )
}
