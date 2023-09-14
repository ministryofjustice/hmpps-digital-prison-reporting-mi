package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class ConfiguredApiRepositoryCustomImpl: ConfiguredApiRepositoryCustom {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  @Autowired
  lateinit var jdbcTemplate: NamedParameterJdbcTemplate
  override fun executeQuery(query: String, rangeFilters: Map<String, String>, filtersExcludingRange: Map<String, String>): List<Map<String, String>> {
    val preparedStatementNamedParams = MapSqlParameterSource()
    filtersExcludingRange.forEach{preparedStatementNamedParams.addValue(it.key,it.value)}
    """"SELECT *
      FROM ($query) Q
      WHERE Q.date = ?"""
    return listOf()
  }


}
