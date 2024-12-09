package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import io.opentelemetry.api.trace.Span
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.controller.ReportDefinitionController
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.DprAuthAwareAuthenticationToken
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.service.ReportDefinitionService
import kotlinx.coroutines.runBlocking

@Configuration
class AppInsightsConfig(private val clientTrackingInterceptor: ClientTrackingInterceptor) : WebMvcConfigurer {
  override fun addInterceptors(registry: InterceptorRegistry) {
    log.info("Adding application insights client tracking interceptor")
    registry.addInterceptor(clientTrackingInterceptor)
      .addPathPatterns("/**")
      .excludePathPatterns("/swagger-ui/**")
      .excludePathPatterns("/health/**")
  }
  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}

@Configuration
class ClientTrackingInterceptor(val reportDefinitionService: ReportDefinitionService) : HandlerInterceptor {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
    if (SecurityContextHolder.getContext().authentication is DprAuthAwareAuthenticationToken) {
      val token = SecurityContextHolder.getContext().authentication as DprAuthAwareAuthenticationToken
      val user = token.jwt.subject
      Span.current().setAttribute("username", user) // username in customDimensions
      Span.current().setAttribute("activeCaseLoadId", token.getCaseLoads().first().toString())
      captureDpdAndPageDetails(request, token)
    }
    return true
  }

  private fun captureDpdAndPageDetails(
    request: HttpServletRequest,
    token: DprAuthAwareAuthenticationToken,
  ): Unit = runBlocking {
    try {
      val regex = Regex("""/reports/([^/]+)/(?!metrics(/|$))([^/]+)""")
      val resultMatched = regex.find(request.requestURI)
      val productId = resultMatched?.let { resultMatched.groupValues[1] }
      val reportVariantId = resultMatched?.let { resultMatched.groupValues[3] }
      if (matchExists(productId, reportVariantId)) {
        val dataProductDefinitionsPath = request.parameterMap["dataProductDefinitionsPath"]?.get(0)
          ?: ReportDefinitionController.DATA_PRODUCT_DEFINITIONS_PATH_EXAMPLE
        val pageNumber = request.parameterMap["selectedPage"]?.get(0)
        val definition = reportDefinitionService.getDefinition(productId!!, reportVariantId!!, token, dataProductDefinitionsPath)
        Span.current().setAttribute("product", definition.name) // product name in customDimensions
        Span.current().setAttribute("reportName", definition.variant.name) // variant name in customDimensions
        pageNumber?.let { Span.current().setAttribute("page", pageNumber) } // page number in customDimensions
      }
    } catch (e: Exception) {
      log.error("Failed to log product name, variant name or selected page to App Insights: {}", e.message)
    }
  }

  private fun matchExists(productId: String?, reportVariantId: String?) =
    !productId.isNullOrBlank() && !reportVariantId.isNullOrBlank()
}
