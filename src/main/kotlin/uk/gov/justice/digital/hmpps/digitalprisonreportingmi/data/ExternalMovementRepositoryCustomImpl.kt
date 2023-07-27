package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter
import java.time.LocalDate
import java.time.LocalTime

class ExternalMovementRepositoryCustomImpl : ExternalMovementRepositoryCustom {
  @Autowired
  lateinit var jdbcTemplate: JdbcTemplate
  override fun list(selectedPage: Long, pageSize: Long, sortColumn: String, sortedAsc: Boolean, filters: Map<ExternalMovementFilter, Any>): List<ExternalMovement> {
    var whereClause = "WHERE "
//    val directionCondition = filters[ExternalMovementFilter.DIRECTION]?.let { it as String }?.lowercase()?.let { "direction = '$it'" }
    val startDateCondition = filters[ExternalMovementFilter.START_DATE]?.let { it as LocalDate }?.toString()?.let { "date >= $it" }
    val endDateCondition = filters[ExternalMovementFilter.END_DATE]?.let { it as LocalDate }?.toString()?.let { "date <= $it" }
    whereClause += startDateCondition?.plus(" AND $endDateCondition")
      ?: endDateCondition?.plus(" AND $startDateCondition") ?: ""
    whereClause = if ("WHERE " == whereClause) "" else whereClause
    val sortingDirection = if (sortedAsc) "asc" else "desc"
    val sql = """ SELECT * FROM datamart.domain.movement_movements
                  $whereClause 
                  ORDER BY $sortColumn $sortingDirection limit $pageSize OFFSET ($selectedPage - 1) * $pageSize;"""
    return jdbcTemplate.query(
      sql,
    ) { rs, rowNum ->
      ExternalMovement(
        rs.getLong("id"),
        rs.getString("prison_number"),
        LocalDate.parse(rs.getString("date")),
        LocalTime.parse(rs.getString("time")),
        rs.getString("origin"),
        rs.getString("destination"),
        rs.getString("direction"),
        rs.getString("type"),
        rs.getString("reason"),
      )
    }
  }
}
