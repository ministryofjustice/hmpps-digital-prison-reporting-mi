package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.AuthenticationHelper
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.DprAuthAwareAuthenticationToken

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
abstract class IntegrationTestBase {

  companion object {

    lateinit var wireMockServer: WireMockServer

    @BeforeAll
    @JvmStatic
    fun setupClass() {
      wireMockServer = WireMockServer(
        WireMockConfiguration.wireMockConfig().port(9999),
      )
      wireMockServer.start()
    }

    @AfterAll
    @JvmStatic
    fun teardownClass() {
      wireMockServer.stop()
    }

    const val TEST_TOKEN = "TestToken"
  }

  @Value("\${dpr.lib.user.role}")
  lateinit var authorisedRole: String

  @Autowired
  lateinit var webTestClient: WebTestClient

  @Autowired
  lateinit var jwtAuthHelper: JwtAuthHelper

  @MockBean
  lateinit var redshiftDataClient: RedshiftDataClient

  @MockBean
  lateinit var authenticationHelper: AuthenticationHelper

  internal fun setAuthorisation(
    user: String = "AUTH_ADM",
    roles: List<String> = listOf(),
    scopes: List<String> = listOf(),
  ): (HttpHeaders) -> Unit = jwtAuthHelper.setAuthorisation(user, roles, scopes)

  protected fun stubDefinitionsResponse() {
    val externalMovementsDefinitionJson = this::class.java.classLoader.getResource("external-movements.json")?.readText()
    val courtDefinitionJson = this::class.java.classLoader.getResource("dpd001-court-hospital-movements.json")?.readText()

    val jwt = Mockito.mock<Jwt>()
    val authentication = Mockito.mock<DprAuthAwareAuthenticationToken>()
    whenever(jwt.tokenValue).then { TEST_TOKEN }
    whenever(authentication.jwt).then { jwt }
    whenever(authenticationHelper.getCurrentAuthentication()).then { authentication }

    wireMockServer.stubFor(
      WireMock.get("/definitions/prisons/orphanage")
        .withHeader(HttpHeaders.AUTHORIZATION, WireMock.equalTo("Bearer $TEST_TOKEN"))
        .willReturn(
          WireMock.aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody("""[$externalMovementsDefinitionJson, $courtDefinitionJson]"""),
        ),
    )
  }

  @BeforeEach
  fun setup() {
    wireMockServer.resetAll()
    stubDefinitionsResponse()
  }
}
