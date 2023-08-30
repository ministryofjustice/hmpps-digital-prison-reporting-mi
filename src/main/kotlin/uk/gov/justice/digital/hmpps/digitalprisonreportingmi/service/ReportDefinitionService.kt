package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.model.*
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ProductDefinitionRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.ProductDefinition

@Service
class ReportDefinitionService(val productDefinitionRepository: ProductDefinitionRepository) {

  fun getListForUser(): List<ReportDefinition> {
    return productDefinitionRepository.getProductDefinitions().map(this::map)
  }

  private fun map(definition: ProductDefinition) : ReportDefinition = ReportDefinition(
    name = definition.name,
    description = definition.description,
    variants = definition.variants.map(this::map)
  )

  private fun map(definition: uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.VariantDefinition) : VariantDefinition = VariantDefinition(
    name = definition.name,
    displayName = definition.displayName,
    description = definition.description,
    fields = definition.fields.map(this::map)
  )

  private fun map(definition: uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.FieldDefinition) : FieldDefinition = FieldDefinition(
    name = definition.name,
    displayName = definition.displayName,
    dateFormat = definition.dateFormat,
    wordWrap = definition.wordWrap?.toString()?.let(WordWrap::valueOf),
    filter = definition.filter?.let(this::map),
    sortable = definition.sortable,
    defaultSortColumn = definition.defaultSortColumn
  )

  private fun map(definition: uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.FilterDefinition): FilterDefinition = FilterDefinition(
    type = FilterType.valueOf(definition.type.toString()),
    staticOptions = definition.staticOptions?.map(this::map)
  )

  private fun map(definition: uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.FilterOption): FilterOption = FilterOption(
    name = definition.name,
    displayName = definition.displayName
  )
}
