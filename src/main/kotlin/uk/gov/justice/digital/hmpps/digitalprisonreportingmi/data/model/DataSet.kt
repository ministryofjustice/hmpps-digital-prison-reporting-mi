package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model

data class DataSet(
  val id: String,
  val name: String,
  val query: String,
  val displayName: String,
  val description: String? = null,
  val parameters: List<ParameterDefinition>,
)
