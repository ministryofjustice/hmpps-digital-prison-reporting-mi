package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model

data class Parameter(
  val name: String,
  val displayName: String,
  val type: ParameterType,
  val dateFormat: String? = null,
  val wordWrap: WordWrap? = null,
  val filter: FilterDefinition? = null,
  val sortable: Boolean = true,
  val defaultSortColumn: Boolean = false,
)
