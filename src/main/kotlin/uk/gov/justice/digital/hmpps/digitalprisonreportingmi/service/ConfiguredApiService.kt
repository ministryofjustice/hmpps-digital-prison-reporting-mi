package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import jakarta.validation.ValidationException
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryCustom
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.StubbedProductDefinitionRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.DataSet

@Service
class ConfiguredApiService(
  val stubbedProductDefinitionRepository: StubbedProductDefinitionRepository,
  val configuredApiRepository: ConfiguredApiRepositoryCustom,
) {

  companion object {
    const val INVALID_REPORT_ID_MESSAGE = "Invalid report id provided."
    const val INVALID_FILTERS_MESSAGE = "Invalid filters provided."
    private const val schemaFieldPrefix = "\$ref:"
  }

  val startSuffix = ".start"
  val endSuffix = ".end"
  fun validateAndFetchData(
    reportId: String, dataSetId: String, reportVariantId: String,
    filters: Map<String, String>, selectedPage: Long, pageSize: Long,
    sortColumn: String?, sortedAsc: Boolean): List<Map<String, Any>> {

    validateFilters(reportId, reportVariantId, filters)
    val (rangeFilters, filtersExcludingRange) = filters.entries.partition { (k, _) -> k.endsWith(startSuffix) || k.endsWith(endSuffix)}
    val dataSet = stubbedProductDefinitionRepository.getDataSet(reportId, dataSetId)
    val validatedSortColumn = validateSortColumnOrGetDefault(sortColumn, reportId, dataSet, reportVariantId)
    //executeQuery for found dataSet
    return configuredApiRepository
      .executeQuery(dataSet.query, rangeFilters.associate(transformMapEntryToPair()),
        filtersExcludingRange.associate(transformMapEntryToPair()), selectedPage, pageSize, validatedSortColumn, sortedAsc)
  }

  fun calculateDefaultSortColumn(reportId: String, dataSetId: String, reportVariantId: String): String {
    return getReportVariantSpecFields(reportId, reportVariantId)
      ?.first { it.defaultSortColumn }
      ?.schemaField
      ?.removePrefix(schemaFieldPrefix)
      ?: throw ValidationException("Could not find default sort column for reportId: $reportId, dataSetId: $dataSetId, reportVariantId: $reportVariantId")
  }

  private fun validateSortColumnOrGetDefault(sortColumn: String?, reportId: String, dataSet: DataSet, reportVariantId: String): String {
    return sortColumn?.let {
      dataSet.schema.field.filter { schemaField -> schemaField.name == sortColumn }
        .ifEmpty { throw ValidationException("Invalid sortColumn provided: $sortColumn") }
        .first().name
    } ?: calculateDefaultSortColumn(reportId, dataSet.id, reportVariantId)

  }

  private fun validateFilters(reportId: String, reportVariantId: String, filters: Map<String, String>) {
    if (filters.isEmpty()) {
      return
    }
    getReportVariantSpecFields(reportId, reportVariantId)
      ?.filter { it.filter?.let { true } ?: false }
      ?.filter { truncateRangeFilters(filters).containsKey(it.schemaField.removePrefix(schemaFieldPrefix)) }
//      ?.takeIf { it.size ==  truncateRangeFilters(filters).size}
      ?.ifEmpty { throw ValidationException(INVALID_FILTERS_MESSAGE) }
      ?: throw ValidationException(INVALID_FILTERS_MESSAGE)
  }

  private fun getReportVariantSpecFields(reportId: String, reportVariantId: String) =
    stubbedProductDefinitionRepository.getProductDefinitions()
      .filter { it.id == reportId }
      .flatMap { it.report.filter { report -> report.id == reportVariantId } }
      .ifEmpty { throw ValidationException(INVALID_REPORT_ID_MESSAGE) }
      .first()
      .specification
      ?.field

  private fun truncateRangeFilters(filters: Map<String, String>): Map<String, String> {
    return filters.entries
      .associate { (k, v) -> truncateBasedOnSuffix(k, v) }
  }
  private fun truncateBasedOnSuffix(k: String, v: String): Pair<String, String> {
    return if (k.endsWith(startSuffix)) {
      k.substring(0, k.length - startSuffix.length) to v
    } else if (k.endsWith(endSuffix)) {
      k.substring(0, k.length - endSuffix.length) to v
    }
    else k to v
  }

  private fun transformMapEntryToPair(): (Map.Entry<String, String>) -> Pair<String, String> {
    return { (k, v) -> k to v }
  }
}