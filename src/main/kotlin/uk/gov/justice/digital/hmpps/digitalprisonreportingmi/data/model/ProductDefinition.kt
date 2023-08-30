package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model

data class ProductDefinition(
  val id: String,
  val name: String,
  val description: String? = null,
  val metaData: MetaData,
  val dataSource: List<DataSource>,
  val dataSet: List<DataSet>,
  val report: List<Report>,
)
