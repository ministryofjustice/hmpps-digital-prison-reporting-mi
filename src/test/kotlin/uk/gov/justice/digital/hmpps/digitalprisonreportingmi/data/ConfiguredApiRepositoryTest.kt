package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.movementPrisoner3
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.movementPrisoner4
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovements.allExternalMovements
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllPrisoners.allPrisoners
import java.time.LocalDateTime
import java.util.*
import java.sql.Timestamp


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ConfiguredApiRepositoryTest {

  @Autowired
  lateinit var externalMovementRepository: ExternalMovementRepository

  @Autowired
  lateinit var prisonerRepository: PrisonerRepository

  @Autowired
  lateinit var configuredApiRepository: ConfiguredApiRepositoryCustom

  @BeforeEach
  fun setup() {
    allExternalMovements.forEach {
      externalMovementRepository.save(it)
    }
    allPrisoners.forEach {
      prisonerRepository.save(it)
    }
  }

  val query = "SELECT " +
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
  "ON movements.prisoner = prisoners.id"

  @Test
  fun `should return 2 external movements for the selected page 2 and pageSize 2 sorted by date in ascending order`() {
    val rangeFilters = mapOf("date.start" to "2023-04-25", "date.end" to "2023-09-10")
    val filtersExcludingRange = mapOf("direction" to "In")

    val actual = configuredApiRepository.executeQuery(query, emptyMap(), emptyMap(), 2, 2, "date", true)
//    Assertions.assertEquals(listOf(movementPrisoner3, movementPrisoner4), actual)
    Assertions.assertEquals(listOf(movementPrisoner3, movementPrisoner4), actual)
    Assertions.assertEquals(2, actual.size)
  }

//  @Test
//  fun `should return 1 external movement for the selected page 3 and pageSize 2 sorted by date in ascending order`() {
//    val actual = externalMovementRepository.list(3, 2, "date", true, emptyMap())
//    Assertions.assertEquals(listOf(movementPrisoner5), actual)
//    Assertions.assertEquals(1, actual.size)
//  }
//
//  @Test
//  fun `should return 5 external movements for the selected page 1 and pageSize 5 sorted by date in ascending order`() {
//    val actual = externalMovementRepository.list(1, 5, "date", true, emptyMap())
//    Assertions.assertEquals(listOf(movementPrisoner1, movementPrisoner2, movementPrisoner3, movementPrisoner4, movementPrisoner5), actual)
//    Assertions.assertEquals(5, actual.size)
//  }
//
//  @Test
//  fun `should return an empty list for the selected page 2 and pageSize 5 sorted by date in ascending order`() {
//    val actual = externalMovementRepository.list(2, 5, "date", true, emptyMap())
//    Assertions.assertEquals(emptyList<ExternalMovementPrisonerEntity>(), actual)
//  }
//
//  @Test
//  fun `should return an empty list for the selected page 6 and pageSize 1 sorted by date in ascending order`() {
//    val actual = externalMovementRepository.list(6, 1, "date", true, emptyMap())
//    Assertions.assertEquals(emptyList<ExternalMovementPrisonerEntity>(), actual)
//  }
//
//  @TestFactory
//  fun `should return all external movements for the selected page and pageSize sorted by date when sortedAsc is true and when it is false`() =
//    assertExternalMovements(sortColumn = ExternalMovementService.SortingColumns.date, expectedForAscending = movementPrisoner1, expectedForDescending = movementPrisoner5)
//
//  @TestFactory
//  fun `should return all external movements for the selected page and pageSize sorted by time when sortedAsc is true and when it is false`() =
//    assertExternalMovements(sortColumn = ExternalMovementService.SortingColumns.timeOnly, expectedForAscending = movementPrisoner1, expectedForDescending = movementPrisoner4)
//
//  @TestFactory
//  fun `should return all external movements for the selected page and pageSize sorted by prisoner when sortedAsc is true and when it is false`() =
//    assertExternalMovements(sortColumn = ExternalMovementService.SortingColumns.prisonNumber, expectedForAscending = movementPrisoner1, expectedForDescending = movementPrisoner3)
//
//  @TestFactory
//  fun `should return all external movements for the selected page and pageSize sorted by 'origin' when sortedAsc is true and when it is false`() =
//    assertExternalMovements(sortColumn = ExternalMovementService.SortingColumns.origin, expectedForAscending = movementPrisoner4, expectedForDescending = movementPrisoner3)
//
//  @TestFactory
//  fun `should return all external movements for the selected page and pageSize sorted by 'destination' when sortedAsc is true and when it is false`() =
//    assertExternalMovements(sortColumn = ExternalMovementService.SortingColumns.destination, expectedForAscending = movementPrisoner3, expectedForDescending = movementPrisoner2)
//
//  @TestFactory
//  fun `should return all external movements for the selected page and pageSize sorted by 'direction' when sortedAsc is true and when it is false`() =
//    assertExternalMovements(sortColumn = ExternalMovementService.SortingColumns.direction, expectedForAscending = movementPrisoner1, expectedForDescending = movementPrisoner4)
//
//  @TestFactory
//  fun `should return all external movements for the selected page and pageSize sorted by 'type' when sortedAsc is true and when it is false`() =
//    assertExternalMovements(sortColumn = ExternalMovementService.SortingColumns.type, expectedForAscending = movementPrisoner1, expectedForDescending = movementPrisoner2)
//
//  @TestFactory
//  fun `should return all external movements for the selected page and pageSize sorted by 'reason' when sortedAsc is true and when it is false`() =
//    assertExternalMovements(sortColumn = ExternalMovementService.SortingColumns.reason, expectedForAscending = movementPrisoner2, expectedForDescending = movementPrisoner1)
//
//  @TestFactory
//  fun `should return all external movements for the selected page and pageSize sorted by 'lastname,firstname' when sortedAsc is true and when it is false`() =
//    assertExternalMovements(sortColumn = ExternalMovementService.SortingColumns.name, expectedForAscending = movementPrisoner2, expectedForDescending = movementPrisoner4)
//
//  @Test
//  fun `should return a list of all results with no filters`() {
//    val actual = externalMovementRepository.list(1, 20, "date", true, emptyMap())
//    Assertions.assertEquals(5, actual.size)
//  }
//
//  @Test
//  fun `should return a list of inwards movements with an in direction filter`() {
//    val actual = externalMovementRepository.list(1, 20, "date", true, Collections.singletonMap(ExternalMovementFilter.DIRECTION, "In"))
//    Assertions.assertEquals(4, actual.size)
//  }
//
//  @Test
//  fun `should return a list of inwards movements with an in direction filter regardless of the casing`() {
//    val actual = externalMovementRepository.list(1, 20, "date", true, Collections.singletonMap(ExternalMovementFilter.DIRECTION, "In"))
//    Assertions.assertEquals(4, actual.size)
//  }
//
//  @Test
//  fun `should return a list of outwards movements with an out direction filter`() {
//    val actual = externalMovementRepository.list(1, 20, "date", true, Collections.singletonMap(ExternalMovementFilter.DIRECTION, "Out"))
//    Assertions.assertEquals(1, actual.size)
//  }
//
//  @Test
//  fun `should return a list of outwards movements with an out direction filter regardless of the casing`() {
//    val actual = externalMovementRepository.list(1, 20, "date", true, Collections.singletonMap(ExternalMovementFilter.DIRECTION, "out"))
//    Assertions.assertEquals(1, actual.size)
//  }
//
//  @Test
//  fun `should return a count of all results with no filters`() {
//    val actual = externalMovementRepository.count(emptyMap())
//    Assertions.assertEquals(5L, actual)
//  }
//
//  @Test
//  fun `should return a count of inwards movements with an in direction filter`() {
//    val actual = externalMovementRepository.count(Collections.singletonMap(ExternalMovementFilter.DIRECTION, "in"))
//    Assertions.assertEquals(4L, actual)
//  }
//
//  @Test
//  fun `should return a count of outwards movements with an out direction filter`() {
//    val actual = externalMovementRepository.count(Collections.singletonMap(ExternalMovementFilter.DIRECTION, "out"))
//    Assertions.assertEquals(1L, actual)
//  }
//
//  @Test
//  fun `should return a count of movements with a startDate filter`() {
//    val actual = externalMovementRepository.count(Collections.singletonMap(ExternalMovementFilter.START_DATE, LocalDate.parse("2023-05-01")))
//    Assertions.assertEquals(2, actual)
//  }
//
//  @Test
//  fun `should return a count of movements with a endDate filter`() {
//    val actual = externalMovementRepository.count(Collections.singletonMap(ExternalMovementFilter.END_DATE, LocalDate.parse("2023-01-31")))
//    Assertions.assertEquals(1, actual)
//  }
//
//  @Test
//  fun `should return a count of movements with a startDate and an endDate filter`() {
//    val actual = externalMovementRepository.count(mapOf(ExternalMovementFilter.START_DATE to LocalDate.parse("2023-04-30"), ExternalMovementFilter.END_DATE to LocalDate.parse("2023-05-01")))
//    Assertions.assertEquals(2, actual)
//  }
//
//  @Test
//  fun `should return a count of zero with a startDate greater than the latest movement date`() {
//    val actual = externalMovementRepository.count(mapOf(ExternalMovementFilter.START_DATE to LocalDate.parse("2025-04-30")))
//    Assertions.assertEquals(0, actual)
//  }
//
//  @Test
//  fun `should return a count of zero with an endDate less than the earliest movement date`() {
//    val actual = externalMovementRepository.count(mapOf(ExternalMovementFilter.END_DATE to LocalDate.parse("2019-04-30")))
//    Assertions.assertEquals(0, actual)
//  }
//
//  @Test
//  fun `should return a count of zero if the start date is after the end date`() {
//    val actual = externalMovementRepository.count(mapOf(ExternalMovementFilter.START_DATE to LocalDate.parse("2023-04-30"), ExternalMovementFilter.END_DATE to LocalDate.parse("2019-05-01")))
//    Assertions.assertEquals(0, actual)
//  }
//
//  @Test
//  fun `should return all the movements on or after the provided start date`() {
//    val actual = externalMovementRepository.list(1, 10, "date", false, Collections.singletonMap(ExternalMovementFilter.START_DATE, LocalDate.parse("2023-04-30")))
//    Assertions.assertEquals(listOf(movementPrisoner5, movementPrisoner4, movementPrisoner3), actual)
//  }
//
//  @Test
//  fun `should return all the movements on or before the provided end date`() {
//    val actual = externalMovementRepository.list(1, 10, "date", false, Collections.singletonMap(ExternalMovementFilter.END_DATE, LocalDate.parse("2023-04-25")))
//    Assertions.assertEquals(listOf(movementPrisoner2, movementPrisoner1), actual)
//  }
//
//  @Test
//  fun `should return all the movements between the provided start and end dates`() {
//    val actual = externalMovementRepository.list(1, 10, "date", false, mapOf(ExternalMovementFilter.START_DATE to LocalDate.parse("2023-04-25"), ExternalMovementFilter.END_DATE to LocalDate.parse("2023-05-20")))
//    Assertions.assertEquals(listOf(movementPrisoner5, movementPrisoner4, movementPrisoner3, movementPrisoner2), actual)
//  }
//
//  @Test
//  fun `should return no movements if the start date is after the latest movement date`() {
//    val actual = externalMovementRepository.list(1, 10, "date", false, Collections.singletonMap(ExternalMovementFilter.START_DATE, LocalDate.parse("2025-01-01")))
//    Assertions.assertEquals(emptyList<ExternalMovementPrisonerEntity>(), actual)
//  }
//
//  @Test
//  fun `should return no movements if the end date is before the earliest movement date`() {
//    val actual = externalMovementRepository.list(1, 10, "date", false, Collections.singletonMap(ExternalMovementFilter.END_DATE, LocalDate.parse("2015-01-01")))
//    Assertions.assertEquals(emptyList<ExternalMovementPrisonerEntity>(), actual)
//  }
//
//  @Test
//  fun `should return no movements if the start date is after the end date`() {
//    val actual = externalMovementRepository.list(1, 10, "date", false, mapOf(ExternalMovementFilter.START_DATE to LocalDate.parse("2023-05-01"), ExternalMovementFilter.END_DATE to LocalDate.parse("2023-04-25")))
//    Assertions.assertEquals(emptyList<ExternalMovementPrisonerEntity>(), actual)
//  }
//
//  @Test
//  fun `should not throw an error when origin, destination or direction are null`() {
//    val externalMovementNullValues = ExternalMovementEntity(
//      6,
//      9846,
//      LocalDateTime.of(2050, 6, 1, 0, 0, 0),
//      LocalDateTime.of(2050, 6, 1, 12, 0, 0),
//      null,
//      null,
//      null,
//      "Transfer",
//      "Transfer In from Other Establishment",
//    )
//    val prisoner9846 = PrisonerEntity(9846, "W2505GF", "FirstName6", "LastName6", null)
//    val movementPrisonerNullValues = ExternalMovementPrisonerEntity(
//      "W2505GF",
//      "FirstName6",
//      "LastName6",
//      LocalDateTime.of(2050, 6, 1, 0, 0, 0),
//      LocalDateTime.of(2050, 6, 1, 12, 0, 0),
//      null,
//      null,
//      null,
//      "Transfer",
//      "Transfer In from Other Establishment",
//    )
//    try {
//      externalMovementRepository.save(externalMovementNullValues)
//      prisonerRepository.save(prisoner9846)
//      val actual = externalMovementRepository.list(
//        1,
//        1,
//        "date",
//        true,
//        mapOf(
//          ExternalMovementFilter.START_DATE to LocalDate.of(2050, 6, 1),
//          ExternalMovementFilter.END_DATE to LocalDate.of(2050, 6, 1),
//        ),
//      )
//      Assertions.assertEquals(listOf(movementPrisonerNullValues), actual)
//      Assertions.assertEquals(1, actual.size)
//    } finally {
//      externalMovementRepository.delete(externalMovementNullValues)
//      prisonerRepository.delete(prisoner9846)
//    }
//  }
//
//  private fun assertExternalMovements(sortColumn: String, expectedForAscending: ExternalMovementPrisonerEntity, expectedForDescending: ExternalMovementPrisonerEntity): List<DynamicTest> {
//    return listOf(
//      true to listOf(expectedForAscending),
//      false to listOf(expectedForDescending),
//    )
//      .map { (sortedAsc, expected) ->
//        DynamicTest.dynamicTest("When sorting by $sortColumn and sortedAsc is $sortedAsc the result is $expected") {
//          val actual = externalMovementRepository.list(1, 1, sortColumn, sortedAsc, emptyMap())
//          Assertions.assertEquals(expected, actual)
//          Assertions.assertEquals(1, actual.size)
//        }
//      }
//  }

  object AllMovements {
    val externalMovement1 = ExternalMovementEntity(
      1,
      8894,
      LocalDateTime.of(2023, 1, 31, 0, 0, 0),
      LocalDateTime.of(2023, 1, 31, 3, 1, 0),
      "Ranby",
      "Kirkham",
      "In",
      "Admission",
      "Unconvicted Remand",
    )
    val externalMovement2 = ExternalMovementEntity(
      2,
      5207,
      LocalDateTime.of(2023, 4, 25, 0, 0, 0),
      LocalDateTime.of(2023, 4, 25, 12, 19, 0),
      "Elmley",
      "Pentonville",
      "In",
      "Transfer",
      "Transfer In from Other Establishment",
    )
    val externalMovement3 = ExternalMovementEntity(
      3,
      4800,
      LocalDateTime.of(2023, 4, 30, 0, 0, 0),
      LocalDateTime.of(2023, 4, 30, 13, 19, 0),
      "Wakefield",
      "Dartmoor",
      "In",
      "Transfer",
      "Transfer In from Other Establishment",
    )
    val externalMovement4 = ExternalMovementEntity(
      4,
      7849,
      LocalDateTime.of(2023, 5, 1, 0, 0, 0),
      LocalDateTime.of(2023, 5, 1, 15, 19, 0),
      "Cardiff",
      "Maidstone",
      "Out",
      "Transfer",
      "Transfer Out to Other Establishment",
    )
    val externalMovement5 = ExternalMovementEntity(
      5,
      6851,
      LocalDateTime.of(2023, 5, 20, 0, 0, 0),
      LocalDateTime.of(2023, 5, 20, 14, 0, 0),
      "Isle of Wight",
      "Northumberland",
      "In",
      "Transfer",
      "Transfer In from Other Establishment",
    )
    val allExternalMovements = listOf(
      externalMovement1,
      externalMovement2,
      externalMovement3,
      externalMovement4,
      externalMovement5,
    )
  }
  object AllPrisoners {
    //    val prisoner8894 = PrisonerEntity(8894, "G2504UV", "FirstName2", "LastName1", null)
    val prisoner8894 = mapOf("id" to 8894, "number" to "G2504UV", "firstName" to "FirstName2", "lastName" to "LastName1", "livingUnitReference" to null)

    //    val prisoner5207 = PrisonerEntity(5207, "G2927UV", "FirstName1", "LastName1", null)
    val prisoner5207 = mapOf("id" to 5207, "number" to "G2927UV", "firstName" to "FirstName1", "lastName" to "LastName1", "livingUnitReference" to null)

    //    val prisoner4800 = PrisonerEntity(4800, "G3418VR", "FirstName3", "LastName3", null)
    val prisoner4800 = mapOf("id" to 4800, "number" to "G3418VR", "firstName" to "FirstName3", "lastName" to "LastName3", "livingUnitReference" to null)

    //    val prisoner7849 = PrisonerEntity(7849, "G3411VR", "FirstName4", "LastName5", 142595)
    val prisoner7849 = mapOf("id" to 7849, "number" to "G3411VR", "firstName" to "FirstName4", "lastName" to "LastName5", "livingUnitReference" to 142595)

    //    val prisoner6851 = PrisonerEntity(6851, "G3154UG", "FirstName5", "LastName5", null)
    val prisoner6851 = mapOf("id" to 6851, "number" to "G3154UG", "firstName" to "FirstName5", "lastName" to "LastName5", "livingUnitReference" to null)


    val allPrisoners = listOf(
      PrisonerEntity(8894, "G2504UV", "FirstName2", "LastName1", null),
      PrisonerEntity(5207, "G2927UV", "FirstName1", "LastName1", null),
      PrisonerEntity(4800, "G3418VR", "FirstName3", "LastName3", null),
      PrisonerEntity(7849, "G3411VR", "FirstName4", "LastName5", 142595),
      PrisonerEntity(6851, "G3154UG", "FirstName5", "LastName5", null),
    )
  }

  object AllMovementPrisoners {
    const val PRISON_NUMBER = "PRISONNUMBER"
    const val NAME = "NAME"
    const val DATE = "DATE"
    const val DIRECTION = "DIRECTION"
    const val TYPE = "TYPE"
    const val ORIGIN = "ORIGIN"
    const val DESTINATION = "DESTINATION"
    const val REASON = "REASON"

    val allMovementPrisoners = allExternalMovements.mapIndexed { i, em ->
      ExternalMovementPrisonerEntity(
        allPrisoners[i].number,
        allPrisoners[i].firstName, allPrisoners[i].lastName, em.date, em.time,
        em.origin, em.destination, em.direction, em.type, em.reason,
      )
    }
//    val movementPrisoner1 = allMovementPrisoners[0]
    val movementPrisoner1 = mapOf(PRISON_NUMBER to "G2504UV", NAME to "LastName1, F", DATE to "2023-01-31", DIRECTION to "In", TYPE to "Admission", ORIGIN to "Ranby", DESTINATION to "Kirkham", REASON to "Unconvicted Remand")

//    val movementPrisoner2 = allMovementPrisoners[1]
    val movementPrisoner2 = mapOf(PRISON_NUMBER to "G2927UV", NAME to "LastName1, F", DATE to "2023-04-25", DIRECTION to "In", TYPE to "Transfer", ORIGIN to "Elmley", DESTINATION to "Pentonville", REASON to "Transfer In from Other Establishment")

//    val movementPrisoner3 = allMovementPrisoners[2]
    val movementPrisoner3 = mapOf(PRISON_NUMBER to "G3418VR", NAME to "LastName3, F", DATE to "2023-04-30", DIRECTION to "In", TYPE to "Transfer", ORIGIN to "Wakefield", DESTINATION to "Dartmoor", REASON to "Transfer In from Other Establishment")

//    val movementPrisoner4 = allMovementPrisoners[3]
    val movementPrisoner4 = mapOf(PRISON_NUMBER to "G3411VR", NAME to "LastName5, F", DATE to "2023-05-01", DIRECTION to "Out", TYPE to "Transfer", ORIGIN to "Cardiff", DESTINATION to "Maidstone", REASON to "Transfer Out to Other Establishment")

//    val movementPrisoner5 = allMovementPrisoners[4]
    val movementPrisoner5 = mapOf(PRISON_NUMBER to "G3154UG", NAME to "LastName5, F", DATE to "2023-05-20", DIRECTION to "In", TYPE to "Transfer", ORIGIN to "Isle of Wight", DESTINATION to "Northumberland", REASON to "Transfer In from Other Establishment")
  }
}