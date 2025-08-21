package uk.gov.justice.digital.hmpps.digitalprisonreportingmi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.net.InetAddress

@SpringBootApplication
class DigitalPrisonReportingMi

fun main(args: Array<String>) {
  val host = System.getenv("AURORA_MISSING_REPORT_JDBC_URL")
  println("Early DNS Debug: Host = $host")
  try {
    val addr = InetAddress.getByName(host)
    println("Early DNS Debug: Resolved IP = ${addr.hostAddress}")
  } catch (e: Exception) {
    println("Early DNS Debug: DNS resolution failed: $e")
  }
  runApplication<DigitalPrisonReportingMi>(*args)
}
