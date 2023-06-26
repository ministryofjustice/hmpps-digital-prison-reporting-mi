package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model

import java.time.LocalDate
import java.time.LocalTime

data class ExternalMovement(
  val prisonNumber: String,
  val date: LocalDate,
  val time: LocalTime,
  val from: String,
  val to: String,
  val direction: String,
  val type: String,
  val reason: String,
)
