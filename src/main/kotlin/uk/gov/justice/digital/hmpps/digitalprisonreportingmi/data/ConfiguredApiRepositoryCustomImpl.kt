package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import jakarta.validation.ValidationException
import org.apache.commons.lang3.time.StopWatch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.model.ExternalMovementFilter
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ExternalMovementService
import java.sql.Timestamp
import java.time.LocalDate

@Service
@Suppress("UNCHECKED_CAST")
class ConfiguredApiRepositoryCustomImpl: ConfiguredApiRepositoryCustom {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

//  private data class WhereClause(val mapSqlParameterSource: MapSqlParameterSource, val stringWhereClause: String)
//
//  fun list(selectedPage: Long, pageSize: Long, sortColumn: String, sortedAsc: Boolean, filters: Map<ExternalMovementFilter, Any>): List<ExternalMovementPrisonerEntity> {
//    val (preparedStatementNamedParams, whereClause) = constructWhereClause(filters)
//    val sortingDirection = if (sortedAsc) "asc" else "desc"
//
//    val sql = """SELECT prisoners.number, prisoners.firstname, prisoners.lastname, movements.date,
//                    movements.time, to_char(movements.time, 'HH24:MI:SS') as timeOnly, movements.direction, movements.type,
//                    movements.origin, movements.destination, movements.reason
//                    FROM datamart.domain.movements_movements as movements
//                    JOIN datamart.domain.prisoner_prisoner as prisoners
//                    ON movements.prisoner = prisoners.id
//                    $whereClause
//                    ORDER BY ${constructOrderByClause(sortColumn, sortingDirection)}
//                    limit $pageSize OFFSET ($selectedPage - 1) * $pageSize;"""
//    val stopwatch = StopWatch.createStarted()
//    val externalMovementPrisonerEntities = jdbcTemplate.queryForList(
//      sql,
//      preparedStatementNamedParams,
//    ).map { q ->
//      ExternalMovementPrisonerEntity(
//        q["number"] as String,
//        q["firstname"] as String,
//        q["lastname"] as String,
//        (q["date"] as Timestamp).toLocalDateTime(),
//        (q["time"] as Timestamp).toLocalDateTime(),
//        q["origin"]?.let { it as String },
//        q["destination"]?.let { it as String },
//        q["direction"]?.let { it as String },
//        q["type"] as String,
//        q["reason"] as String,
//      )
//    }
//    stopwatch.stop()
//    log.info("Query Execution time in ms: {}", stopwatch.time)
//    return externalMovementPrisonerEntities
//  }
//
//  private fun constructWhereClause(filters: Map<ExternalMovementFilter, Any>): WhereClause {
//    val preparedStatementNamedParams = MapSqlParameterSource()
//    val directionCondition = filters[ExternalMovementFilter.DIRECTION]?.let { it as String }?.lowercase()?.let {
//      preparedStatementNamedParams.addValue("direction", it)
//      "lower(direction) = :direction"
//    }
//    val startDateCondition = filters[ExternalMovementFilter.START_DATE]?.let { it as LocalDate }?.toString()?.plus(" 00:00:00")?.let {
//      preparedStatementNamedParams.addValue("startDate", it)
//      "date >= :startDate"
//    }
//    val endDateCondition = filters[ExternalMovementFilter.END_DATE]?.let { it as LocalDate }?.toString()?.let {
//      preparedStatementNamedParams.addValue("endDate", it)
//      "date <= :endDate"
//    }
//    val dateConditions = startDateCondition?.plus(endDateCondition?.let { " AND $it" } ?: "")
//      ?: endDateCondition?.plus(startDateCondition?.let { " AND $it" } ?: "")
//    val allConditions = dateConditions?.plus(directionCondition?.let { " AND $it" } ?: "") ?: directionCondition
//    val whereClause = allConditions?.let { "WHERE $it" } ?: ""
//    return WhereClause(preparedStatementNamedParams, whereClause)
//  }

  @Autowired
  lateinit var jdbcTemplate: NamedParameterJdbcTemplate
  override fun executeQuery(
    query: String, rangeFilters: Map<String, String>,
    filtersExcludingRange: Map<String, String>,
    selectedPage: Long,
    pageSize: Long,
    sortColumn: String,
    sortedAsc: Boolean
  ): List<Map<String, Any>> {
    val preparedStatementNamedParams = MapSqlParameterSource()
    //TODO: Consider case insensitive non range filters
    filtersExcludingRange.forEach{preparedStatementNamedParams.addValue(it.key,it.value)}
    rangeFilters.forEach{preparedStatementNamedParams.addValue(it.key,it.value)}
    val whereNoRange = filtersExcludingRange.keys.map { k -> "$k = :$k" }.joinToString(" AND ").removeSuffix(" AND ").ifEmpty { null }
    val whereRange = rangeFilters.keys.map {k ->
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
    val whereClause = whereNoRange?.let{"WHERE ${whereRange?.let { "$whereNoRange AND $whereRange"} ?: whereNoRange}"} ?: whereRange?.let { "WHERE $it" } ?: ""
    val sortingDirection = if (sortedAsc) "asc" else "desc"

    val result = jdbcTemplate.queryForList(
      """SELECT *
      FROM ($query) Q
      $whereClause
      ORDER BY $sortColumn $sortingDirection 
                    limit $pageSize OFFSET ($selectedPage - 1) * $pageSize;""",
      preparedStatementNamedParams
    )
      .map { it.entries.associate { (k, v) ->
        if (v is Timestamp) {
          k to v.toLocalDateTime().toLocalDate().toString()
        } else {
          k to v
        }
      }
      }
    return result
  }


}
