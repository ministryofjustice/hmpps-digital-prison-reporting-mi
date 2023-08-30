package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model

data class ProductDefinition(
  val name: String,
  val description: String? = null,
  val variants: List<VariantDefinition>,
)
