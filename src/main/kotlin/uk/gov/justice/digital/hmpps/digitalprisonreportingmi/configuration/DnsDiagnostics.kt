package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import com.zaxxer.hikari.HikariDataSource
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.InetAddress
import javax.sql.DataSource

@Component
class DnsDiagnostics(
  private val missingReportDataSource: DataSource

) {
  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  @PostConstruct
  fun logAuroraDns() {
    try {
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
    } catch (e: Exception) {
      log.error("DNS resolution failed", e)
    }
  }
}