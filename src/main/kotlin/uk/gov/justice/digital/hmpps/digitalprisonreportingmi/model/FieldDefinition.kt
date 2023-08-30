package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model

class FieldDefinition (
  name: String,
  displayName: String,
  dateFormat: String? = null,
  wordWrap: WordWrap? = null,
  filter: FilterDefinition? = null,
  sortable: Boolean = true,
  defaultSortColumn: Boolean = false,
)

enum class WordWrap {
  None
}