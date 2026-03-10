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
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.DprUserAuthAwareAuthenticationToken
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration.PostgresContainer
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration.TestFlywayConfig
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.PrisonerRepository
import uk.gov.justice.hmpps.kotlin.auth.HmppsAuthenticationHolder
import uk.gov.justice.hmpps.test.kotlin.auth.JwtAuthorisationHelper

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = ["spring.main.allow-bean-definition-overriding=true"])
@ActiveProfiles("test")
@Import(value = [TestFlywayConfig::class, TestWebClientConfiguration::class])
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

    const val TEST_TOKEN = "eyJraWQiOiJobXBwcy1hdXRoLWRldi0yMDIzMDMwNiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJURVNUX1VTRVIiLCJuYmYiOjE3NzMxNDIxODMsImdyYW50X3R5cGUiOiJjbGllbnRfY3JlZGVudGlhbHMiLCJ1c2VyX25hbWUiOiJURVNUX1VTRVIiLCJhdXRoX3NvdXJjZSI6Im5vbmUiLCJzY29wZSI6WyJyZWFkIl0sImlzcyI6Imh0dHBzOi8vc2lnbi1pbi1kZXYuaG1wcHMuc2VydmljZS5qdXN0aWNlLmdvdi51ay9hdXRoL2lzc3VlciIsImV4cCI6MTc3MzE0NTc4MywiaWF0IjoxNzczMTQyMTgzLCJqdGkiOiJaZEl2TVplWEhuV2N1VFdVSWNCeWtVTGFJMjgiLCJhdXRob3JpdGllcyI6WyJST0xFX1JPTEVTX0FETUlOIiwiUk9MRV9VU0VSX1BFUk1JU1NJT05TX19STyIsIlJPTEVfUFJJU09OU19SRVBPUlRJTkdfVE9PTFNfVVNFUiJdLCJjbGllbnRfaWQiOiJobXBwcy1kcHItdG9vbHMtYXBpLTIifQ.iyg3GL9LpK0bQbz-KGh9-lQC7oJ_6JpiEtiBwe9NmdeS0EUxuaWYjGf1WbZLrxBfLQqldwxjCgyNOZm0s4ub6HzuM5OJhJkxltOcxwfkqwo5f3xHchi9HdINcbMatr6yIIVWdcsy6i_7l1U9Bd1AHlJTpTaB0qteyTlcO4iyRYNf9zPFb8SuPZCEiqc0ldKL4JjFHENb2ROim16fohf5idnBdjYrHQXjAju8gbCup4XqPAUA3xvgq9hKPKsEmVHt_3JKMfM8SttNEKYIzfqfWz90ES-CwY2x29OsZSFREJSJ2ERyajkVM8dj0r6nwt-_Vj4IX4dV5106UGiVgCWosg"
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
    val authentication = Mockito.mock<DprUserAuthAwareAuthenticationToken>()
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
            .withBody("""
              {
                "access_token": "$TEST_TOKEN",
                 "expires_in": 3599,
                 "token_type": "Bearer",
                 "scope": "read",
                 "sub": "request-user",
                 "user_name": "request-user",
                 "auth_source": "none"
               }
              """.trimIndent()),
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
    stubUserRolesResponse()
  }
}
