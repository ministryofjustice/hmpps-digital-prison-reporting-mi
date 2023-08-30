package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.model

data class VariantDefinition(
  val id: String,
  val name: String,
  val resourceName: String,
  val description: String?,
  val specification: String?,
  val fields: List<FieldDefinition>,
)
