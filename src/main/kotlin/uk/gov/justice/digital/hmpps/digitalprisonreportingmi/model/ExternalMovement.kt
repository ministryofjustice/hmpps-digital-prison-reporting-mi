package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalTime
@Entity
@Table(name = "movement_movements", schema = "domain")
data class ExternalMovement(
  @Id val id: Long,
  val prisoner: Long,
  val date: LocalDate,
  val time: LocalTime,
  val origin: String,
  val destination: String,
  val direction: String,
  val type: String,
  val reason: String,
)
