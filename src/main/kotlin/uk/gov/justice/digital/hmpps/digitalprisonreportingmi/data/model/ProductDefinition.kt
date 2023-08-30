package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model

data class ProductDefinition(
  val id: String,
  val name: String,
  val description: String? = null,
  val metaData: MetaData,
  val dataSources: List<DataSource>,
  val dataSets: List<DataSet>,
)
