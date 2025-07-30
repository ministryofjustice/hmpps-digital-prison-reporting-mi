package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RequestLoggerConfig: Filter {
  private val logger = LoggerFactory.getLogger(RequestLoggerConfig::class.java)
  override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
    val httpRequest = request as HttpServletRequest
    logger.info("Http Request: ${httpRequest.method} ${httpRequest.requestURI}")
    chain?.doFilter(request, response)
  }

}