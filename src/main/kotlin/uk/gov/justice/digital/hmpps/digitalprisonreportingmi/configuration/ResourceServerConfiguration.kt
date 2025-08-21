package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.CaseloadProvider
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.DefaultDprAuthAwareTokenConverter
import uk.gov.justice.hmpps.kotlin.auth.HmppsResourceServerConfiguration
import uk.gov.justice.hmpps.kotlin.auth.dsl.ResourceServerConfigurationCustomizer
import java.net.InetAddress
import javax.sql.DataSource

@Configuration
@ConditionalOnProperty(name = ["dpr.lib.user.role", "spring.security.oauth2.resourceserver.jwt.jwk-set-uri"])
class ResourceServerConfiguration(
  private val caseloadProvider: CaseloadProvider,
  @Value("\${dpr.lib.user.role}") private val authorisedRole: String,
) {

  @Bean
  fun securityFilterChain(
    http: HttpSecurity,
    resourceServerCustomizer: ResourceServerConfigurationCustomizer,
  ): SecurityFilterChain = HmppsResourceServerConfiguration().hmppsSecurityFilterChain(http, resourceServerCustomizer)

  @Bean
  fun resourceServerCustomizer() = ResourceServerConfigurationCustomizer {
    oauth2 { tokenConverter = DefaultDprAuthAwareTokenConverter(caseloadProvider) }
    securityMatcher { paths = listOf("/user/caseload/active") }
    anyRequestRole { defaultRole = removeRolePrefix(authorisedRole) }
  }

  private fun removeRolePrefix(role: String) = role.replace("ROLE_", "")
}
