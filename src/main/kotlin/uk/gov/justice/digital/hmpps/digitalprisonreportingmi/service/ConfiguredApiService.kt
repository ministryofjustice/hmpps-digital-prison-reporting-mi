package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import jakarta.validation.ValidationException
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryCustom
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.StubbedProductDefinitionRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.DataSet
import java.sql.Timestamp

@Service
class ConfiguredApiService(
  val stubbedProductDefinitionRepository: StubbedProductDefinitionRepository,
  val configuredApiRepository: ConfiguredApiRepositoryCustom,
) {

  companion object {
    const val INVALID_REPORT_ID_MESSAGE = "Invalid report id provided."
    const val INVALID_FILTERS_MESSAGE = "Invalid filters provided."
  }

  val startSuffix = ".start"
  val endSuffix = ".end"
  fun validateAndFetchData(
    reportId: String, dataSetId: String, reportVariantId: String,
    filters: Map<String, String>, selectedPage: Long, pageSize: Long,
    sortColumn: String, sortedAsc: Boolean): List<Map<String, Any>> {
    //validateFilters

    validateFilters(reportId, reportVariantId, filters)
    val (rangeFilters, filtersExcludingRange) = filters.entries.partition { (k, _) -> k.endsWith(startSuffix) || k.endsWith(endSuffix)}
    val dataSet = stubbedProductDefinitionRepository.getDataSet(reportId, dataSetId)
    validateSortColumn(sortColumn, dataSet)
    //executeQuery for found dataSet
    return configuredApiRepository
      .executeQuery(dataSet.query, rangeFilters.associate(transformMapEntryToPair()), filtersExcludingRange.associate(transformMapEntryToPair()), selectedPage, pageSize, sortColumn, sortedAsc)
  }

  private fun validateSortColumn(sortColumn: String, dataSet: DataSet) {
    dataSet.schema.field.filter { it.name == sortColumn }.ifEmpty { throw ValidationException("Invalid sortColumn provided: $sortColumn") }
  }

  private fun validateFilters(reportId: String, reportVariantId: String, filters: Map<String, String>) {
    if (filters.isEmpty()) {
      return
    }
    stubbedProductDefinitionRepository.getProductDefinitions()
      .filter { it.id == reportId }
      .flatMap { it.report.filter { report -> report.id == reportVariantId } }
      .ifEmpty { throw ValidationException(INVALID_REPORT_ID_MESSAGE) }
      .first()
      .specification
      ?.field
      ?.filter { truncateRangeFilters(filters).containsKey(it.displayName.lowercase()) }
      ?.takeIf { it.size ==  truncateRangeFilters(filters).size}
      ?.ifEmpty { throw ValidationException(INVALID_FILTERS_MESSAGE) }
      ?: throw ValidationException(INVALID_FILTERS_MESSAGE)
  }

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