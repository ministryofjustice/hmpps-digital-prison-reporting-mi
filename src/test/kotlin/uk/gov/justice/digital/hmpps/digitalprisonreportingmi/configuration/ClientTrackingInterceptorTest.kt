package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import io.opentelemetry.api.trace.Span
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.controller.ReportDefinitionController.Companion.DATA_PRODUCT_DEFINITIONS_PATH_EXAMPLE
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.controller.model.SingleVariantReportDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.controller.model.VariantDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.DprAuthAwareAuthenticationToken
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.service.ReportDefinitionService
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.security.FakeCaseloadProvider
import java.time.Instant

class ClientTrackingInterceptorTest {
  companion object {
    val token = DprAuthAwareAuthenticationToken(
      jwt = Jwt(
        "abc",
        Instant.now(),
        Instant.now().plusMillis(100),
        mapOf("Authentication" to "Bearer token"),
        mapOf("sub" to "userA"),
      ),
      clientId = "clientId",
      userName = "userA",
      authorities = emptyList(),
      caseloadProvider = FakeCaseloadProvider(),
    )
    private val staticSecurityContextHolderMockUtil: MockedStatic<SecurityContextHolder> = Mockito.mockStatic(SecurityContextHolder::class.java)
    val span: Span = mock()

    @JvmStatic
    @BeforeAll
    fun setUp() {
      val securityContext = mock<SecurityContext>()
      staticSecurityContextHolderMockUtil.`when`<SecurityContext> { SecurityContextHolder.getContext() }.thenReturn(securityContext)
      whenever(securityContext.authentication).thenReturn(token)
      val staticSpanMockUtil: MockedStatic<Span> = Mockito.mockStatic(Span::class.java)
      staticSpanMockUtil.`when`<Span> { Span.current() }.thenReturn(span)
    }

    @JvmStatic
    @AfterAll
    fun tearDown() {
      staticSecurityContextHolderMockUtil.close()
    }
  }

  @BeforeEach
  fun setUpEach() {
    clearInvocations(span)
  }

  @ParameterizedTest
  @ValueSource(
    strings = [
      "/reports/external-movements/last-month",
      "/reports/external-movements/last-month/fieldid",
      "/reports/external-movements/last-month/count",
      "/async/reports/external-movements/last-month",
      "/reports/external-movements/last-month/tables/tableId/result",
      "/reports/external-movements/last-month/tables/tableId/result/summary/summaryId",
      "/reports/external-movements/last-month/statements/statementId/status",
      "/reports/external-movements/last-month/statements/statementId",
    ],
  )
  fun `should call app insights with product name, variant name and selected page`(uri: String) {
    val reportDefinitionService = mock<ReportDefinitionService>()
    val clientTrackingInterceptor = ClientTrackingInterceptor(reportDefinitionService)
    val request = MockHttpServletRequest("GET", uri)
    request.setParameter("selectedPage", "1")
    val response = MockHttpServletResponse()
    val productId = "external-movements"
    val variantId = "last-month"
    val productName = "External Movements"
    val variantName = "Last Month"
    val definition = SingleVariantReportDefinition(
      id = productId,
      name = productName,
      variant = VariantDefinition(
        id = variantId,
        name = variantName,
        resourceName = "a",
      ),
    )
    whenever(
      reportDefinitionService.getDefinition(productId, variantId, token, DATA_PRODUCT_DEFINITIONS_PATH_EXAMPLE),
    ).thenReturn(definition)

    clientTrackingInterceptor.preHandle(request, response, mock())

    verify(reportDefinitionService, times(1))
      .getDefinition(productId, variantId, token, DATA_PRODUCT_DEFINITIONS_PATH_EXAMPLE)
    verify(span, times(1))
      .setAttribute("username", "userA")
    verify(span, times(1))
      .setAttribute("activeCaseLoadId", "LWSTMC")
    verify(span, times(1))
      .setAttribute("product", productName)
    verify(span, times(1))
      .setAttribute("reportName", variantName)
    verify(span, times(1))
      .setAttribute("page", "1")
  }

  @ParameterizedTest
  @ValueSource(
    strings = [
      "/reports/external-movements/metrics/metricId",
      "/report/tables/tableId/count",
      "/definitions/external-movements/last-month",
    ],
  )
  fun `should not call app insights with product name, variant name and selected page for non matching uris`(uri: String) {
    val reportDefinitionService = mock<ReportDefinitionService>()
    val clientTrackingInterceptor = ClientTrackingInterceptor(reportDefinitionService)
    val request = MockHttpServletRequest("GET", uri)
    val response = MockHttpServletResponse()

    clientTrackingInterceptor.preHandle(request, response, mock())

    verifyNoInteractions(reportDefinitionService)
    verify(span, times(1))
      .setAttribute("username", "userA")
    verify(span, times(1))
      .setAttribute("activeCaseLoadId", "LWSTMC")
    verify(span, times(0))
      .setAttribute(eq("product"), anyString())
    verify(span, times(0))
      .setAttribute(eq("reportName"), anyString())
    verify(span, times(0))
      .setAttribute(eq("page"), anyString())
  }
}
