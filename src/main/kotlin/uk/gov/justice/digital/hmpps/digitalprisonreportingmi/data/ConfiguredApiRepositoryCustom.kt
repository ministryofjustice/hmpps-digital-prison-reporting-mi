package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import jakarta.validation.ValidationException
import org.apache.commons.lang3.time.StopWatch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
@Suppress("UNCHECKED_CAST")
class ConfiguredApiRepositoryCustom {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  @Autowired
  lateinit var jdbcTemplate: NamedParameterJdbcTemplate
  fun executeQuery(
    query: String,
    rangeFilters: Map<String, String>,
    filtersExcludingRange: Map<String, String>,
    selectedPage: Long,
    pageSize: Long,
    sortColumn: String,
    sortedAsc: Boolean,
  ): List<Map<String, Any>> {
    val preparedStatementNamedParams = MapSqlParameterSource()
    // TODO: Consider case insensitive non range filters
    filtersExcludingRange.forEach { preparedStatementNamedParams.addValue(it.key, it.value) }
    rangeFilters.forEach { preparedStatementNamedParams.addValue(it.key, it.value) }
    val whereNoRange = filtersExcludingRange.keys.map { k -> "$k = :$k" }.joinToString(" AND ").removeSuffix(" AND ").ifEmpty { null }
    val whereRange = rangeFilters.keys.map { k ->
      if (k.endsWith(".start")) {
        "${k.removeSuffix(".start")} >= :$k"
      } else if (k.endsWith(".end")) {
        "${k.removeSuffix(".end")} <= :$k"
      } else {
        throw ValidationException("Range filter does not have a .start or .end suffix: $k")
      }
    }.joinToString(" AND ")
      .removeSuffix(" AND ")
      .ifEmpty { null }

//    val whereClause = whereNoRange?.let{"WHERE $it AND $whereRange"} ?: whereRange?.let { "WHERE $it" } ?: ""
    val whereClause = whereNoRange?.let { "WHERE ${whereRange?.let { "$whereNoRange AND $whereRange"} ?: whereNoRange}" } ?: whereRange?.let { "WHERE $it" } ?: ""
    val sortingDirection = if (sortedAsc) "asc" else "desc"

    val stopwatch = StopWatch.createStarted()
    val result = jdbcTemplate.queryForList(
      """SELECT *
      FROM ($query) Q
      $whereClause
      ORDER BY $sortColumn $sortingDirection 
                    limit $pageSize OFFSET ($selectedPage - 1) * $pageSize;""",
      preparedStatementNamedParams,
    )
      .map {
        it.entries.associate { (k, v) ->
          if (v is Timestamp) {
            k to v.toLocalDateTime().toLocalDate().toString()
          } else {
            k to v
          }
        }
      }
    stopwatch.stop()
    log.debug("Query Execution time in ms: {}", stopwatch.time)
    return result
  }
}
