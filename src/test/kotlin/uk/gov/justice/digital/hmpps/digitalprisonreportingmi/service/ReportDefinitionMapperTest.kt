package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.model.RenderMethod.HTML
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.DataSet
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.DataSource
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.FilterDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.FilterOption
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.FilterType
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.MetaData
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.ParameterType
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.ProductDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.RenderMethod
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.Report
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.ReportField
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.Schema
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.SchemaField
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.Specification
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.WordWrap
import java.time.LocalDate
import java.util.Collections.singletonMap

class ReportDefinitionMapperTest {

  private val fullProductDefinition: ProductDefinition = ProductDefinition(
    id = "1",
    name = "2",
    description = "3",
    metaData = MetaData(
      author = "4",
      version = "5",
      owner = "6",
      purpose = "7",
      profile = "8",
      dqri = "9",
    ),
    dataSet = listOf(
      DataSet(
        id = "10",
        name = "11",
        query = "12",
        schema = Schema(
          field = listOf(
            SchemaField(
              name = "13",
              type = ParameterType.Long,
            ),
          ),
        ),
      ),
    ),
    dataSource = listOf(
      DataSource(
        id = "18",
        name = "19",
        connection = "20",
      ),
    ),
    report = listOf(
      Report(
        id = "21",
        name = "22",
        description = "23",
        created = LocalDate.MAX,
        version = "24",
        dataset = "\$ref:10",
        policy = listOf("25"),
        render = RenderMethod.PDF,
        schedule = "26",
        specification = Specification(
          template = "27",
          field = listOf(
            ReportField(
              schemaField = "\$ref:13",
              displayName = "14",
              wordWrap = WordWrap.None,
              filter = FilterDefinition(
                type = FilterType.Radio,
                staticOptions = listOf(
                  FilterOption(
                    name = "16",
                    displayName = "17",
                  ),
                ),
              ),
              sortable = true,
              defaultSortColumn = true,
            ),
          ),
        ),
        destination = listOf(singletonMap("28", "29")),
      ),
    ),
  )

  @Test
  fun `Getting report list for user maps full data correctly`() {
    val mapper = ReportDefinitionMapper()

    val result = mapper.map(fullProductDefinition, null)

    assertThat(result).isNotNull
    assertThat(result.id).isEqualTo(fullProductDefinition.id)
    assertThat(result.name).isEqualTo(fullProductDefinition.name)
    assertThat(result.description).isEqualTo(fullProductDefinition.description)
    assertThat(result.variants).isNotEmpty
    assertThat(result.variants).hasSize(1)

    val variant = result.variants.first()

    assertThat(variant.id).isEqualTo(fullProductDefinition.report.first().id)
    assertThat(variant.name).isEqualTo(fullProductDefinition.report.first().name)
    assertThat(variant.resourceName).isEqualTo(fullProductDefinition.dataSet.first().id)
    assertThat(variant.resourceName).isEqualTo(fullProductDefinition.dataSet.first().id)
    assertThat(variant.description).isEqualTo(fullProductDefinition.report.first().description)
    assertThat(variant.specification).isNotNull
    assertThat(variant.specification?.template).isEqualTo(fullProductDefinition.report.first().specification?.template)
    assertThat(variant.specification?.fields).isNotEmpty
    assertThat(variant.specification?.fields).hasSize(1)

    val field = variant.specification!!.fields.first()
    val sourceSchemaField = fullProductDefinition.dataSet.first().schema.field.first()
    val sourceReportField = fullProductDefinition.report.first().specification!!.field.first()

    assertThat(field.name).isEqualTo(sourceSchemaField.name)
    assertThat(field.displayName).isEqualTo(sourceReportField.displayName)
    assertThat(field.wordWrap.toString()).isEqualTo(sourceReportField.wordWrap.toString())
    assertThat(field.sortable).isEqualTo(sourceReportField.sortable)
    assertThat(field.defaultSortColumn).isEqualTo(sourceReportField.defaultSortColumn)
    assertThat(field.filter).isNotNull
    assertThat(field.filter?.type.toString()).isEqualTo(sourceReportField.filter?.type.toString())
    assertThat(field.filter?.staticOptions).isNotEmpty
    assertThat(field.filter?.staticOptions).hasSize(1)
    assertThat(field.type.toString()).isEqualTo(sourceSchemaField.type.toString())

    val filterOption = field.filter?.staticOptions?.first()
    val sourceFilterOption = sourceReportField.filter?.staticOptions?.first()

    assertThat(filterOption?.name).isEqualTo(sourceFilterOption?.name)
    assertThat(filterOption?.displayName).isEqualTo(sourceFilterOption?.displayName)
  }

  @Test
  fun `Getting report list for user maps minimal data successfully`() {
    val productDefinition = ProductDefinition(
      id = "1",
      name = "2",
      metaData = MetaData(
        author = "3",
        owner = "4",
        version = "5",
      ),
    )
    val mapper = ReportDefinitionMapper()

    val result = mapper.map(productDefinition, null)

    assertThat(result).isNotNull
    assertThat(result.variants).hasSize(0)
  }

  @Test
  fun `Getting report list for user fails when mapping report with no matching dataset`() {
    val productDefinition = ProductDefinition(
      id = "1",
      name = "2",
      metaData = MetaData(
        author = "3",
        owner = "4",
        version = "5",
      ),
      report = listOf(
        Report(
          id = "6",
          name = "7",
          created = LocalDate.MAX,
          version = "8",
          dataset = "\$ref:9",
          render = RenderMethod.SVG,
        ),
      ),
    )
    val mapper = ReportDefinitionMapper()

    val exception = assertThrows(IllegalArgumentException::class.java) {
      mapper.map(productDefinition, null)
    }

    assertThat(exception).message().isEqualTo("Could not find matching DataSet '9'")
  }

  @Test
  fun `Getting HTML report list returns relevant reports`() {
    val productDefinition = ProductDefinition(
      id = "1",
      name = "2",
      metaData = MetaData(
        author = "3",
        owner = "4",
        version = "5",
      ),
      dataSet = listOf(
        DataSet(
          id = "10",
          name = "11",
          query = "12",
          schema = Schema(
            field = emptyList(),
          ),
        ),
      ),
      report = listOf(
        Report(
          id = "13",
          name = "14",
          created = LocalDate.MAX,
          version = "15",
          dataset = "\$ref:10",
          render = RenderMethod.SVG,
        ),
        Report(
          id = "16",
          name = "17",
          created = LocalDate.MAX,
          version = "18",
          dataset = "\$ref:10",
          render = RenderMethod.HTML,
        ),
      ),
    )
    val mapper = ReportDefinitionMapper()

    val result = mapper.map(productDefinition, HTML)

    assertThat(result).isNotNull
    assertThat(result.variants).hasSize(1)
    assertThat(result.variants.first().id).isEqualTo("16")
  }
}
