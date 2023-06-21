package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.security.HmppsAuthExtension

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(HmppsAuthExtension::class)
@ActiveProfiles("test")
abstract class IntegrationTestBase {

  @Value("\${spring.security.user.roles}")
  lateinit var authorisedRole: String

//  @Suppress("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  lateinit var webTestClient: WebTestClient

  @Autowired
  lateinit var jwtAuthHelper: JwtAuthHelper
  internal fun setAuthorisation(
    user: String = "AUTH_ADM",
    roles: List<String> = listOf(),
    scopes: List<String> = listOf(),
  ): (HttpHeaders) -> Unit = jwtAuthHelper.setAuthorisation(user, roles, scopes)
}
