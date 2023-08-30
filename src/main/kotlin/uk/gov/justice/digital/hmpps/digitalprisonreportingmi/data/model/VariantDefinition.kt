package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model

data class VariantDefinition(
  val name: String,
  val query: String,
  val displayName: String,
  val description: String? = null,
  val fields: List<FieldDefinition>,
)
