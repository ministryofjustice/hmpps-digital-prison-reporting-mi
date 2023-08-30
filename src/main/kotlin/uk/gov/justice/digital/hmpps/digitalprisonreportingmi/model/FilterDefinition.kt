package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model

class FilterDefinition (
  type: FilterType,
  staticOptions: List<FilterOption>?,
)

enum class FilterType {
  Radio,
  Select,
  DateRange
}