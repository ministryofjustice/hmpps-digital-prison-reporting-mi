package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ReportRepository

@Service
data class ReportService(val repository: ReportRepository) {

  fun count(domain: String, table: String): Long {
    return repository.count(domain, table)
  }
}
