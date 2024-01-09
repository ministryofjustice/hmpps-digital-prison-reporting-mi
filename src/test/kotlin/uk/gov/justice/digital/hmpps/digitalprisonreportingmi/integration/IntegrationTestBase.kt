package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

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
  }

  @Value("\${dpr.lib.user.role}")
  lateinit var authorisedRole: String

  @Autowired
  lateinit var webTestClient: WebTestClient

  @Autowired
  lateinit var jwtAuthHelper: JwtAuthHelper

  internal fun setAuthorisation(
    user: String = "AUTH_ADM",
    roles: List<String> = listOf(),
    scopes: List<String> = listOf(),
  ): (HttpHeaders) -> Unit = jwtAuthHelper.setAuthorisation(user, roles, scopes)

  protected fun stubDefinitionsResponse() {
    val externalMovementsDefinitionJson = this::class.java.classLoader.getResource("external-movements.json")?.readText()
    val courtDefinitionJson = this::class.java.classLoader.getResource("dpd001-court-hospital-movements.json")?.readText()
    wireMockServer.stubFor(
      WireMock.get("/definitions/prisons/orphanage").willReturn(
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
