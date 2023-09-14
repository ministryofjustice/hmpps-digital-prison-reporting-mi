package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

interface ConfiguredApiRepositoryCustom {

  fun executeQuery(query: String, rangeFilters: Map<String, String>, filtersExcludingRange: Map<String, String>): List<Map<String, String>>

}
