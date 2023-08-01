package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model

import java.time.LocalDate
import java.time.LocalTime

data class ExternalMovementModel(
  val id: Long,
  // This is the "prisoner" column in Redshift. Keeping is as prisonNumber for not breaking the UI for now and will change in the future.
  val prisonNumber: String,
  val date: LocalDate,
  val time: LocalTime,
  val from: String?,
  val to: String?,
  val direction: String?,
  val type: String,
  val reason: String,
)
