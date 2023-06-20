package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.security

import com.github.tomakehurst.wiremock.WireMockServer
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class HmppsAuthExtension : BeforeAllCallback, AfterAllCallback, BeforeEachCallback {

  companion object {
    @JvmField
    val hmppsAuthApi = HmppsAuthMockServer()
  }

  override fun beforeAll(context: ExtensionContext) {
    hmppsAuthApi.start()
//    hmppsAuthApi.stubGrantToken()
//    hmppsAuthApi.stubGetUserDetails("created-user")
//    hmppsAuthApi.stubGetUserDetails("updated-user")
//    hmppsAuthApi.stubGetUserDetails("cancelled-user")
  }

  override fun beforeEach(context: ExtensionContext) {
    hmppsAuthApi.resetRequests()
  }

  override fun afterAll(context: ExtensionContext) {
    hmppsAuthApi.stop()
  }
}

class HmppsAuthMockServer : WireMockServer(WIREMOCK_PORT) {
  companion object {
    private const val WIREMOCK_PORT = 8090
  }

//  fun stubGrantToken() {
//    stubFor(
//      post(WireMock.urlEqualTo("/auth/oauth/token"))
//        .willReturn(
//          aResponse()
//            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//            .withBody(
//              """
//              {
//                "token_type": "bearer",
//                "access_token": "atoken"
//              }
//              """.trimIndent(),
//            ),
//        ),
//    )
//  }

//  fun stubGetUserDetails(userId: String, fullName: String ? = "$userId-name") {
//    val responseBuilder = aResponse()
//      .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//
//    stubFor(
//      get("/auth/api/user/$userId")
//        .willReturn(
//          responseBuilder
//            .withStatus(HttpStatus.OK.value())
//            .withBody(
//              """
//              {
//                 "username": "$userId",
//                 "name": "$fullName"
//                }
//              """.trimIndent(),
//            ),
//        ),
//    )
//  }
}