package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.DataSet
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.DataSource
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.FilterDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.FilterOption
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.FilterType
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.MetaData
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.Parameter
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.ParameterType
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.ProductDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.RenderMethod
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.Report
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.WordWrap
import java.time.LocalDate

@Service
class StubbedProductDefinitionRepository : ProductDefinitionRepository {

  override fun getProductDefinitions(): List<ProductDefinition> {
    val parameters = listOf(
      Parameter(
        name = "prisonNumber",
        displayName = "Prison Number",
        type = ParameterType.String,
      ),
      Parameter(
        name = "name",
        displayName = "Name",
        wordWrap = WordWrap.None,
        type = ParameterType.String,
      ),
      Parameter(
        name = "date",
        displayName = "Date",
        dateFormat = "dd/MM/yy",
        defaultSortColumn = true,
        filter = FilterDefinition(
          type = FilterType.DateRange,
        ),
        type = ParameterType.Date,
      ),
      Parameter(
        name = "date",
        displayName = "Time",
        dateFormat = "HH:mm",
        type = ParameterType.Date,
      ),
      Parameter(
        name = "origin",
        displayName = "From",
        wordWrap = WordWrap.None,
        type = ParameterType.String,
      ),
      Parameter(
        name = "destination",
        displayName = "To",
        wordWrap = WordWrap.None,
        type = ParameterType.String,
      ),
      Parameter(
        name = "direction",
        displayName = "Direction",
        filter = FilterDefinition(
          type = FilterType.Radio,
          staticOptions = listOf(
            FilterOption("in", "In"),
            FilterOption("out", "Out"),
          ),
        ),
        type = ParameterType.String,
      ),
      Parameter(
        name = "type",
        displayName = "Type",
        type = ParameterType.String,
      ),
      Parameter(
        name = "reason",
        displayName = "Reason",
        type = ParameterType.String,
      ),
    )

    return listOf(
      ProductDefinition(
        id = "1",
        name = "External Movements",
        metaData = MetaData(author = "Adam", version = "1.2.3", owner = "Eve"),
        dataSet = listOf(
          DataSet(
            id = "list",
            name = "All movements",
            query = "SELECT " +
              "prisoners.number AS prisonNumber," +
              "CONCAT(prisoners.lastname, ', ', substring(prisoners.firstname, 1, 1)) AS name," +
              "movements.date," +
              "movements.direction," +
              "movements.type," +
              "movements.origin," +
              "movements.destination," +
              "movements.reason\n" +
              "FROM datamart.domain.movements_movements as movements\n" +
              "JOIN datamart.domain.prisoner_prisoner as prisoners\n" +
              "ON movements.prisoner = prisoners.id",
            parameters = parameters,
          ),
          DataSet(
            id = "last-week",
            name = "Last week",
            query = "SELECT " +
              "prisoners.number AS prisonNumber," +
              "CONCAT(prisoners.lastname, ', ', substring(prisoners.firstname, 1, 1)) AS name," +
              "movements.date," +
              "movements.direction," +
              "movements.type," +
              "movements.origin," +
              "movements.destination," +
              "movements.reason\n" +
              "FROM datamart.domain.movements_movements as movements\n" +
              "JOIN datamart.domain.prisoner_prisoner as prisoners\n" +
              "ON movements.prisoner = prisoners.id\n" +
              "WHERE DATE_PART('day', CURRENT_DATE() - movements.date) BETWEEN 0 AND 7",
            parameters = parameters,
          ),
        ),
        dataSource = listOf(
          DataSource(
            id = "1",
            name = "RedShift",
            connection = "redshift",
          ),
        ),
        report = listOf(
          Report(
            id = "1.a",
            name = "All movements",
            dataset = "\$ref:list",
            policy = emptyList(),
            specification = "list",
            render = RenderMethod.HTML,
            created = LocalDate.now(),
            version = "1.2.3",
          ),
          Report(
            id = "1.b",
            name = "Last week",
            description = "All movements in the past week",
            dataset = "\$ref:last-week",
            policy = emptyList(),
            specification = "list",
            render = RenderMethod.HTML,
            created = LocalDate.now(),
            version = "1.2.3",
          ),
        ),
      ),
    )
  }
}
