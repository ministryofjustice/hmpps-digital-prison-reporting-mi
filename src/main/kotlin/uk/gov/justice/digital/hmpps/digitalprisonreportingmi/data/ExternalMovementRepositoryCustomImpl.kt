package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter
import java.sql.Timestamp
import java.time.LocalDate

class ExternalMovementRepositoryCustomImpl : ExternalMovementRepositoryCustom {

  private data class WhereClause(val mapSqlParameterSource: MapSqlParameterSource, val stringWhereClause: String)

  @Autowired
  lateinit var jdbcTemplate: NamedParameterJdbcTemplate
  override fun list(selectedPage: Long, pageSize: Long, sortColumn: String, sortedAsc: Boolean, filters: Map<ExternalMovementFilter, Any>): List<ExternalMovementEntity> {
    val (preparedStatementNamedParams, whereClause) = constructWhereClause(filters)
    val sortingDirection = if (sortedAsc) "asc" else "desc"

    val sql = """ SELECT id, prisoner, movements.date, movements.time, to_char(movements.time, 'HH24:MI:SS') as timeOnly, direction, type, origin, destination, reason 
                    FROM datamart.domain.movements_movements as movements
                    $whereClause 
                    ORDER BY $sortColumn $sortingDirection 
                    limit $pageSize OFFSET ($selectedPage - 1) * $pageSize;"""
    return jdbcTemplate.queryForList(
      sql,
      preparedStatementNamedParams,
    ).map { q ->
      ExternalMovementEntity(
        q["id"] as Long,
        q["prisoner"] as Long,
        (q["date"] as Timestamp).toLocalDateTime(),
        (q["time"] as Timestamp).toLocalDateTime(),
        q["origin"]?.let { it as String },
        q["destination"]?.let { it as String },
        q["direction"]?.let { it as String },
        q["type"] as String,
        q["reason"] as String,
      )
    }
  }

  override fun count(filters: Map<ExternalMovementFilter, Any>): Long {
    val whereClause = constructWhereClause(filters)
    return jdbcTemplate.queryForList(
      "SELECT count(*) as total FROM datamart.domain.movements_movements ${whereClause.stringWhereClause}",
      whereClause.mapSqlParameterSource,
    ).first()?.get("total") as Long
  }

  private fun constructWhereClause(filters: Map<ExternalMovementFilter, Any>): WhereClause {
    val preparedStatementNamedParams = MapSqlParameterSource()
    val directionCondition = filters[ExternalMovementFilter.DIRECTION]?.let { it as String }?.lowercase()?.let {
      preparedStatementNamedParams.addValue("direction", it)
      "lower(direction) = :direction"
    }
    val startDateCondition = filters[ExternalMovementFilter.START_DATE]?.let { it as LocalDate }?.toString()?.plus(" 00:00:00")?.let {
      preparedStatementNamedParams.addValue("startDate", it)
      "date >= :startDate"
    }
    val endDateCondition = filters[ExternalMovementFilter.END_DATE]?.let { it as LocalDate }?.toString()?.let {
      preparedStatementNamedParams.addValue("endDate", it)
      "date <= :endDate"
    }
    val dateConditions = startDateCondition?.plus(endDateCondition?.let { " AND $it" } ?: "")
      ?: endDateCondition?.plus(startDateCondition?.let { " AND $it" } ?: "")
    val allConditions = dateConditions?.plus(directionCondition?.let { " AND $it" } ?: "") ?: directionCondition
    val whereClause = allConditions?.let { "WHERE $it" } ?: ""
    return WhereClause(preparedStatementNamedParams, whereClause)
  }
}
