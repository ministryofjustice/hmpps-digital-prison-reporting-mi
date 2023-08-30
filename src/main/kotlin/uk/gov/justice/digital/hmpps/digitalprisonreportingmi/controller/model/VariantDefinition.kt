package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.model

data class VariantDefinition(
  val name: String,
  val displayName: String,
  val description: String? = null,
  val fields: List<FieldDefinition>,
)
