package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.model.FieldDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.model.FilterDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.model.FilterOption
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.model.FilterType
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.model.ReportDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.model.VariantDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.model.WordWrap
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ProductDefinitionRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.DataSet
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.ParameterDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.ProductDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.Report

@Service
class ReportDefinitionService(val productDefinitionRepository: ProductDefinitionRepository) {

  fun getListForUser(): List<ReportDefinition> {
    return productDefinitionRepository.getProductDefinitions()
      .map(this::map)
      .filter { it.variants.isNotEmpty() }
  }

  private fun map(definition: ProductDefinition): ReportDefinition = ReportDefinition(
    name = definition.name,
    description = definition.description,
    variants = definition.report.map { map(it, definition.dataSet) },
  )

  private fun map(definition: Report, dataSets: List<DataSet>): VariantDefinition {
    val dataSet = dataSets.find { it.id == definition.dataset.removePrefix("\$ref:") }!!

    return VariantDefinition(
      id = definition.id,
      name = definition.name,
      description = definition.description,
      specification = definition.specification,
      resourceName = dataSet.id,
      fields = dataSet.parameters.map(this::map),
    )
  }

  private fun map(definition: ParameterDefinition): FieldDefinition = FieldDefinition(
    name = definition.name,
    displayName = definition.displayName,
    dateFormat = definition.dateFormat,
    wordWrap = definition.wordWrap?.toString()?.let(WordWrap::valueOf),
    filter = definition.filter?.let(this::map),
    sortable = definition.sortable,
    defaultSortColumn = definition.defaultSortColumn,
  )

  private fun map(definition: uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.FilterDefinition): FilterDefinition = FilterDefinition(
    type = FilterType.valueOf(definition.type.toString()),
    staticOptions = definition.staticOptions?.map(this::map),
  )

  private fun map(definition: uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.FilterOption): FilterOption = FilterOption(
    name = definition.name,
    displayName = definition.displayName,
  )
}
