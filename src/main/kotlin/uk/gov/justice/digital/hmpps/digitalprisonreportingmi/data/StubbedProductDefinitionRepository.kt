package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.*

@Service
class StubbedProductDefinitionRepository : ProductDefinitionRepository {

  override fun getProductDefinitions(): List<ProductDefinition> {
    val fields = listOf(
      FieldDefinition(
        name = "prisonNumber",
        displayName = "Prison Number",
      ),
      FieldDefinition(
        name = "name",
        displayName = "Name",
        wordWrap = WordWrap.None
      ),
      FieldDefinition(
        name = "date",
        displayName = "Date",
        dateFormat = "dd/MM/yy",
        defaultSortColumn = true,
        filter = FilterDefinition(
          type = FilterType.DateRange
        )
      ),
      FieldDefinition(
        name = "date",
        displayName = "Time",
        dateFormat = "HH:mm"
      ),
      FieldDefinition(
        name = "origin",
        displayName = "From",
        wordWrap = WordWrap.None
      ),
      FieldDefinition(
        name = "destination",
        displayName = "To",
        wordWrap = WordWrap.None
      ),
      FieldDefinition(
        name = "direction",
        displayName = "Direction",
        filter = FilterDefinition(
          type = FilterType.Radio,
          staticOptions = listOf(
            FilterOption("in", "In"),
            FilterOption("out", "Out"),
          )
        )
      ),
      FieldDefinition(
        name = "type",
        displayName = "Type"
      ),
      FieldDefinition(
        name = "reason",
        displayName = "Reason"
      )
    )

    return listOf(
      ProductDefinition(
        name = "External Movements",
        variants = listOf(
          VariantDefinition(
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
            fields = fields
          ),
          VariantDefinition(
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
            fields = fields
          )
        )
      )
    )
  }
  
}
