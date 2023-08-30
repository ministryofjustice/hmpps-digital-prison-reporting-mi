package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.ParameterDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.FilterDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.FilterOption
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.FilterType
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.ProductDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.DataSet
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.DataSource
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.MetaData
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.WordWrap

@Service
class StubbedProductDefinitionRepository : ProductDefinitionRepository {

  override fun getProductDefinitions(): List<ProductDefinition> {
    val parameters = listOf(
      ParameterDefinition(
        name = "prisonNumber",
        displayName = "Prison Number",
      ),
      ParameterDefinition(
        name = "name",
        displayName = "Name",
        wordWrap = WordWrap.None,
      ),
      ParameterDefinition(
        name = "date",
        displayName = "Date",
        dateFormat = "dd/MM/yy",
        defaultSortColumn = true,
        filter = FilterDefinition(
          type = FilterType.DateRange,
        ),
      ),
      ParameterDefinition(
        name = "date",
        displayName = "Time",
        dateFormat = "HH:mm",
      ),
      ParameterDefinition(
        name = "origin",
        displayName = "From",
        wordWrap = WordWrap.None,
      ),
      ParameterDefinition(
        name = "destination",
        displayName = "To",
        wordWrap = WordWrap.None,
      ),
      ParameterDefinition(
        name = "direction",
        displayName = "Direction",
        filter = FilterDefinition(
          type = FilterType.Radio,
          staticOptions = listOf(
            FilterOption("in", "In"),
            FilterOption("out", "Out"),
          ),
        ),
      ),
      ParameterDefinition(
        name = "type",
        displayName = "Type",
      ),
      ParameterDefinition(
        name = "reason",
        displayName = "Reason",
      ),
    )

    return listOf(
      ProductDefinition(
        id = "1",
        name = "External Movements",
        metaData = MetaData(author = "Adam", version = "1.2.3.4", owner = "Eve"),
        dataSets = listOf(
          DataSet(
            id = "1",
            name = "list",
            displayName = "All movements",
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
            id = "2",
            name = "last-week",
            displayName = "Last week",
            description = "All movements in the past week",
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
        dataSources = listOf(
          DataSource(
            id = "1",
            name = "RedShift",
            connection = "redshift"
          )
        )
      ),
    )
  }
}
