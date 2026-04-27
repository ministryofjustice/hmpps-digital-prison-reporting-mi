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
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.productCollection.ProductCollectionRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.DprSystemAuthAwareAuthenticationToken
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration.PostgresContainer
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration.TestFlywayConfig
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.PrisonerRepository
import uk.gov.justice.hmpps.kotlin.auth.AuthSource
import uk.gov.justice.hmpps.kotlin.auth.HmppsAuthenticationHolder
import uk.gov.justice.hmpps.test.kotlin.auth.JwtAuthorisationHelper

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = ["spring.main.allow-bean-definition-overriding=true"])
@ActiveProfiles("test")
@Import(value = [TestFlywayConfig::class])
@AutoConfigureWebTestClient
abstract class IntegrationTestBase {

  @Autowired
  lateinit var externalMovementRepository: ExternalMovementRepository

  @Autowired
  lateinit var prisonerRepository: PrisonerRepository

  @Autowired
  lateinit var productCollectionRepository: ProductCollectionRepository

  companion object {

    lateinit var wireMockServer: WireMockServer
    val pgContainer = PostgresContainer.instance

    @BeforeAll
    @JvmStatic
    fun setupClass() {
      wireMockServer = WireMockServer(
        WireMockConfiguration.wireMockConfig().port(9999),
      )
      wireMockServer.start()
    }

    @JvmStatic
    @DynamicPropertySource
    fun setupClass(registry: DynamicPropertyRegistry) {
      registry.add("dpr.lib.definition.locations") { "external-movements.json,dpd001-court-hospital-movements.json" }
      pgContainer?.run {
        registry.add("spring.datasource.url", pgContainer::getJdbcUrl)
        registry.add("spring.datasource.username", pgContainer::getUsername)
        registry.add("spring.datasource.password", pgContainer::getPassword)
        registry.add("spring.datasource.missingreport.url", pgContainer::getJdbcUrl)
        registry.add("spring.datasource.missingreport.username", pgContainer::getUsername)
        registry.add("spring.datasource.missingreport.password", pgContainer::getPassword)
      }
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
  protected lateinit var jwtAuthorisationHelper: JwtAuthorisationHelper

  @MockitoBean
  lateinit var authenticationHelper: HmppsAuthenticationHolder

  protected fun setAuthorisation(
    user: String = "request-user",
    roles: List<String> = emptyList(),
    scopes: List<String> = emptyList(),
  ): (HttpHeaders) -> Unit = jwtAuthorisationHelper.setAuthorisationHeader(
    clientId = "hmpps-digital-prison-reporting-api",
    username = user,
    scope = scopes,
    roles = roles,
  )

  protected fun stubDefinitionsResponse() {
    val externalMovementsDefinitionJson = this::class.java.classLoader.getResource("external-movements.json")?.readText()
    val courtDefinitionJson = this::class.java.classLoader.getResource("dpd001-court-hospital-movements.json")?.readText()

    val jwt = Mockito.mock<Jwt>()
    val authentication = Mockito.mock<DprSystemAuthAwareAuthenticationToken>()
    whenever(jwt.tokenValue).then { TEST_TOKEN }
    whenever(authentication.jwt).then { jwt }
    whenever(authenticationHelper.authentication).then { authentication }

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

  protected fun stubAccessTokenResponse() {
    wireMockServer.stubFor(
      WireMock.post("/auth/oauth/token")
        .willReturn(
          WireMock.aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(
              """
              {
                "access_token": "${setAuthorisation(roles = listOf(authorisedRole))}",
                 "expires_in": 3599,
                 "token_type": "Bearer",
                 "scope": "read",
                 "sub": "request-user",
                 "user_name": "request-user",
                 "auth_source": "none"
               }
              """.trimIndent(),
            ),
        ),
    )
  }

  protected fun stubPrisonerCaseloadResponse() {
    wireMockServer.stubFor(
      WireMock.get("/prisonusers/request-user/caseloads")
        .willReturn(
          WireMock.aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(
              """
              {
                "username":"request-user",
                "active":"true",
                "accountType":"GENERAL",
                "activeCaseload": {"id":"LWSTMC","name":"Lowestoft (North East Suffolk) Magistrat"},
                "caseloads":[
                  {"id":"LWSTMC","name":"Lowestoft (North East Suffolk) Magistrat"}
                ]
              }
              """.trimIndent(),
            ),
        ),
    )
  }

  protected fun stubPrisonerInfoResponse() {
    wireMockServer.stubFor(
      WireMock.get("/users/request-user")
        .willReturn(
          WireMock.aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(
              """
              {
                "username":"request-user",
                "active":true,
                "name":"request-user",
                "authSource":"${AuthSource.NOMIS.name}",
                "userId":"abc123",
                "uuid":"989q-2f3f-2g3-g34",
              }
              """.trimIndent(),
            ),
        ),
    )
  }

  protected fun stubUserRolesResponse() {
    wireMockServer.stubFor(
      WireMock.get("/users/request-user/roles")
        .willReturn(
          WireMock.aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(
              """
               [
                {"roleCode":"$authorisedRole"}
               ]
              """.trimIndent(),
            ),
        ),
    )
  }

  @BeforeEach
  fun setup() {
    wireMockServer.resetAll()
    stubDefinitionsResponse()
    stubAccessTokenResponse()
    stubPrisonerCaseloadResponse()
    stubPrisonerInfoResponse()
    stubUserRolesResponse()
  }
}
