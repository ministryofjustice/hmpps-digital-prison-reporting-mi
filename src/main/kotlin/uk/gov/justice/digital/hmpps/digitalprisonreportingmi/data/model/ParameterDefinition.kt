package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model

data class ParameterDefinition(
  val name: String,
  val displayName: String,
  val dateFormat: String? = null,
  val wordWrap: WordWrap? = null,
  val filter: FilterDefinition? = null,
  val sortable: Boolean = true,
  val defaultSortColumn: Boolean = false,
)
