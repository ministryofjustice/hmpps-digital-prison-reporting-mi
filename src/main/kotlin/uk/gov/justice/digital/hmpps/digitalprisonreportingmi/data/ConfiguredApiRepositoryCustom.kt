package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.springframework.stereotype.Service

@Service
interface ConfiguredApiRepositoryCustom {

  fun executeQuery(query: String, rangeFilters: Map<String, String>, filtersExcludingRange: Map<String, String>, selectedPage: Long, pageSize: Long, sortColumn: String, sortedAsc: Boolean): List<Map<String, Any>>

}
