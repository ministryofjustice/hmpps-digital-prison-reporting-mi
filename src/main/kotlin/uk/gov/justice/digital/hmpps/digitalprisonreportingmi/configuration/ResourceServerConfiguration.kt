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

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  @Bean
  fun securityFilterChain(
    http: HttpSecurity,
    resourceServerCustomizer: ResourceServerConfigurationCustomizer,
  ): SecurityFilterChain = HmppsResourceServerConfiguration().hmppsSecurityFilterChain(http, resourceServerCustomizer)

  @Bean
  fun resourceServerCustomizer(missingReportDataSource: DataSource) = ResourceServerConfigurationCustomizer {
    val hikari = missingReportDataSource as? HikariDataSource
    log.info("Hikari JDBC URL___: ${hikari?.jdbcUrl ?: "Unknown"}")
    log.info("Hikari driverClassName: ${hikari?.driverClassName ?: "Unknown"}")
    log.info("Hikari maximumPoolSize: ${hikari?.maximumPoolSize ?: "Unknown"}")
    log.info("Hikari minimumIdle: ${hikari?.minimumIdle ?: "Unknown"}")
    log.info("Hikari poolName: ${hikari?.poolName ?: "Unknown"}")

    val host = System.getenv("AURORA_MISSING_REPORT_JDBC_URL")
    log.info("Host was___: $host")
    val addr = InetAddress.getByName(host)
    log.info("Resolved IP___: ${addr.hostAddress}")

    oauth2 { tokenConverter = DefaultDprAuthAwareTokenConverter(caseloadProvider) }
    securityMatcher { paths = listOf("/user/caseload/active") }
    anyRequestRole { defaultRole = removeRolePrefix(authorisedRole) }
  }

  private fun removeRolePrefix(role: String) = role.replace("ROLE_", "")
}
