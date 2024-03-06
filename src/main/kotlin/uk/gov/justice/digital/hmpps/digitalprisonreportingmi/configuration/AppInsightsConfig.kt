package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

// import com.microsoft.applicationinsights.web.internal.internalThreadContext

import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.opentelemetry.api.trace.Span
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.DprAuthAwareAuthenticationToken
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.DprAuthAwareTokenConverter
import java.text.ParseException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Configuration
class AppInsightsConfig(private val clientTrackingInterceptor: ClientTrackingInterceptor) : WebMvcConfigurer {
  override fun addInterceptors(registry: InterceptorRegistry) {
    log.info("Adding application insights client tracking interceptor")
    registry.addInterceptor(clientTrackingInterceptor).addPathPatterns("/**")
  }
  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}

@Configuration
class ClientTrackingInterceptor(val tokenConverter: DprAuthAwareTokenConverter) : HandlerInterceptor {
  override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
    val token = request.getHeader(HttpHeaders.AUTHORIZATION)
    val bearer = "Bearer "
    if (StringUtils.startsWithIgnoreCase(token, bearer)) {
      try {
        val parsed: JWT = SignedJWT.parse(token.replace("Bearer ", ""))
        val headers = LinkedHashMap(parsed.header.toJSONObject())
        val claims = parsed.jwtClaimsSet.claims
        val issuedAt: Instant = Instant.now().truncatedTo(ChronoUnit.SECONDS)
        val expiration = issuedAt.plus(3, ChronoUnit.MINUTES)
        val parsedToken1 = Jwt.withTokenValue(token)
          .headers { h -> h.putAll(headers) }
          .claims { c -> c.putAll(claims) }
          .issuedAt(issuedAt)
          .expiresAt(expiration)
          .build()
        val parsedToken = tokenConverter.convert(parsedToken1) as DprAuthAwareAuthenticationToken
//        val jwtBody = getClaimsFromJWT(token)
//        val user = Optional.ofNullable(jwtBody.subject)
        val user = Optional.ofNullable(parsedToken1.subject)
        user.map { it.toString() }.ifPresent {
          Span.current().setAttribute("username", it) // username in customDimensions
        }
        Span.current().setAttribute("activeCaseLoadId", parsedToken.getCaseLoads().first().toString())
      } catch (e: ParseException) {
        log.warn("problem decoding jwt public key for application insights", e)
      }
    }
    return true
  }

  @Throws(ParseException::class)
  private fun getClaimsFromJWT(token: String): JWTClaimsSet =
    SignedJWT.parse(token.replace("Bearer ", "")).jwtClaimsSet

  companion object {
    private val log = LoggerFactory.getLogger(ClientTrackingInterceptor::class.java)
  }
}
