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
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.DprAuthAwareAuthenticationToken

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
class ClientTrackingInterceptor : HandlerInterceptor {
  override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
    if (SecurityContextHolder.getContext().authentication is DprAuthAwareAuthenticationToken) {
      val token = SecurityContextHolder.getContext().authentication as DprAuthAwareAuthenticationToken
      val user = token.jwt.subject
      Span.current().setAttribute("username", user) // username in customDimensions
      Span.current().setAttribute("activeCaseLoadId", token.getCaseLoads().first().toString())
    }
    return true
  }
}
