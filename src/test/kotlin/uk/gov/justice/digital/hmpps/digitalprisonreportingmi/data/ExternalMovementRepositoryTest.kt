package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepositoryTest.AllMovements.allExternalMovements
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ExternalMovementRepositoryTest {

  @Autowired
  lateinit var externalMovementRepository: ExternalMovementRepository

  @BeforeEach
  fun setup() {
    allExternalMovements.forEach {
      externalMovementRepository.save(it)
    }
  }

  @Test
  fun `should return 2 external movements for the selected page 2 and pageSize 2 sorted by date in ascending order`() {
    val actual = externalMovementRepository.list(2, 2, "date", true, emptyMap())
    Assertions.assertEquals(listOf(AllMovements.externalMovement3, AllMovements.externalMovement4), actual)
    Assertions.assertEquals(2, actual.size)
  }

  @Test
  fun `should return 1 external movement for the selected page 3 and pageSize 2 sorted by date in ascending order`() {
    val actual = externalMovementRepository.list(3, 2, "date", true, emptyMap())
    Assertions.assertEquals(listOf(AllMovements.externalMovement5), actual)
    Assertions.assertEquals(1, actual.size)
  }

  @Test
  fun `should return 5 external movements for the selected page 1 and pageSize 5 sorted by date in ascending order`() {
    val actual = externalMovementRepository.list(1, 5, "date", true, emptyMap())
    Assertions.assertEquals(listOf(AllMovements.externalMovement1, AllMovements.externalMovement2, AllMovements.externalMovement3, AllMovements.externalMovement4, AllMovements.externalMovement5), actual)
    Assertions.assertEquals(5, actual.size)
  }

  @Test
  fun `should return an empty list for the selected page 2 and pageSize 5 sorted by date in ascending order`() {
    val actual = externalMovementRepository.list(2, 5, "date", true, emptyMap())
    Assertions.assertEquals(emptyList<ExternalMovementEntity>(), actual)
  }

  @Test
  fun `should return an empty list for the selected page 6 and pageSize 1 sorted by date in ascending order`() {
    val actual = externalMovementRepository.list(6, 1, "date", true, emptyMap())
    Assertions.assertEquals(emptyList<ExternalMovementEntity>(), actual)
  }

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by date when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "date", expectedForAscending = AllMovements.externalMovement1, expectedForDescending = AllMovements.externalMovement5)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by time when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "timeOnly", expectedForAscending = AllMovements.externalMovement1, expectedForDescending = AllMovements.externalMovement4)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by prisoner when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "prisoner", expectedForAscending = AllMovements.externalMovement3, expectedForDescending = AllMovements.externalMovement1)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'origin' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "origin", expectedForAscending = AllMovements.externalMovement4, expectedForDescending = AllMovements.externalMovement3)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'destination' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "destination", expectedForAscending = AllMovements.externalMovement3, expectedForDescending = AllMovements.externalMovement2)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'direction' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "direction", expectedForAscending = AllMovements.externalMovement1, expectedForDescending = AllMovements.externalMovement4)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'type' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "type", expectedForAscending = AllMovements.externalMovement1, expectedForDescending = AllMovements.externalMovement2)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'reason' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "reason", expectedForAscending = AllMovements.externalMovement2, expectedForDescending = AllMovements.externalMovement1)

  @Test
  fun `should return a list of all results with no filters`() {
    val actual = externalMovementRepository.list(1, 20, "date", true, emptyMap())
    Assertions.assertEquals(5, actual.size)
  }

  @Test
  fun `should return a list of inwards movements with an in direction filter`() {
    val actual = externalMovementRepository.list(1, 20, "date", true, Collections.singletonMap(ExternalMovementFilter.DIRECTION, "In"))
    Assertions.assertEquals(4, actual.size)
  }

  @Test
  fun `should return a list of inwards movements with an in direction filter regardless of the casing`() {
    val actual = externalMovementRepository.list(1, 20, "date", true, Collections.singletonMap(ExternalMovementFilter.DIRECTION, "In"))
    Assertions.assertEquals(4, actual.size)
  }

  @Test
  fun `should return a list of outwards movements with an out direction filter`() {
    val actual = externalMovementRepository.list(1, 20, "date", true, Collections.singletonMap(ExternalMovementFilter.DIRECTION, "Out"))
    Assertions.assertEquals(1, actual.size)
  }

  @Test
  fun `should return a list of outwards movements with an out direction filter regardless of the casing`() {
    val actual = externalMovementRepository.list(1, 20, "date", true, Collections.singletonMap(ExternalMovementFilter.DIRECTION, "out"))
    Assertions.assertEquals(1, actual.size)
  }

  @Test
  fun `should return a count of all results with no filters`() {
    val actual = externalMovementRepository.count(emptyMap())
    Assertions.assertEquals(5L, actual)
  }

  @Test
  fun `should return a count of inwards movements with an in direction filter`() {
    val actual = externalMovementRepository.count(Collections.singletonMap(ExternalMovementFilter.DIRECTION, "in"))
    Assertions.assertEquals(4L, actual)
  }

  @Test
  fun `should return a count of outwards movements with an out direction filter`() {
    val actual = externalMovementRepository.count(Collections.singletonMap(ExternalMovementFilter.DIRECTION, "out"))
    Assertions.assertEquals(1L, actual)
  }

  @Test
  fun `should return a count of movements with a startDate filter`() {
    val actual = externalMovementRepository.count(Collections.singletonMap(ExternalMovementFilter.START_DATE, LocalDate.parse("2023-05-01")))
    Assertions.assertEquals(2, actual)
  }

  @Test
  fun `should return a count of movements with a endDate filter`() {
    val actual = externalMovementRepository.count(Collections.singletonMap(ExternalMovementFilter.END_DATE, LocalDate.parse("2023-01-31")))
    Assertions.assertEquals(1, actual)
  }

  @Test
  fun `should return a count of movements with a startDate and an endDate filter`() {
    val actual = externalMovementRepository.count(mapOf(ExternalMovementFilter.START_DATE to LocalDate.parse("2023-04-30"), ExternalMovementFilter.END_DATE to LocalDate.parse("2023-05-01")))
    Assertions.assertEquals(2, actual)
  }

  @Test
  fun `should return a count of zero with a startDate greater than the latest movement date`() {
    val actual = externalMovementRepository.count(mapOf(ExternalMovementFilter.START_DATE to LocalDate.parse("2025-04-30")))
    Assertions.assertEquals(0, actual)
  }

  @Test
  fun `should return a count of zero with an endDate less than the earliest movement date`() {
    val actual = externalMovementRepository.count(mapOf(ExternalMovementFilter.END_DATE to LocalDate.parse("2019-04-30")))
    Assertions.assertEquals(0, actual)
  }

  @Test
  fun `should return a count of zero if the start date is after the end date`() {
    val actual = externalMovementRepository.count(mapOf(ExternalMovementFilter.START_DATE to LocalDate.parse("2023-04-30"), ExternalMovementFilter.END_DATE to LocalDate.parse("2019-05-01")))
    Assertions.assertEquals(0, actual)
  }

  @Test
  fun `should return all the movements on or after the provided start date`() {
    val actual = externalMovementRepository.list(1, 10, "date", false, Collections.singletonMap(ExternalMovementFilter.START_DATE, LocalDate.parse("2023-04-30")))
    Assertions.assertEquals(listOf(AllMovements.externalMovement5, AllMovements.externalMovement4, AllMovements.externalMovement3), actual)
  }

  @Test
  fun `should return all the movements on or before the provided end date`() {
    val actual = externalMovementRepository.list(1, 10, "date", false, Collections.singletonMap(ExternalMovementFilter.END_DATE, LocalDate.parse("2023-04-25")))
    Assertions.assertEquals(listOf(AllMovements.externalMovement2, AllMovements.externalMovement1), actual)
  }

  @Test
  fun `should return all the movements between the provided start and end dates`() {
    val actual = externalMovementRepository.list(1, 10, "date", false, mapOf(ExternalMovementFilter.START_DATE to LocalDate.parse("2023-04-25"), ExternalMovementFilter.END_DATE to LocalDate.parse("2023-05-20")))
    Assertions.assertEquals(listOf(AllMovements.externalMovement5, AllMovements.externalMovement4, AllMovements.externalMovement3, AllMovements.externalMovement2), actual)
  }

  @Test
  fun `should return no movements if the start date is after the latest movement date`() {
    val actual = externalMovementRepository.list(1, 10, "date", false, Collections.singletonMap(ExternalMovementFilter.START_DATE, LocalDate.parse("2025-01-01")))
    Assertions.assertEquals(emptyList<ExternalMovementEntity>(), actual)
  }

  @Test
  fun `should return no movements if the end date is before the earliest movement date`() {
    val actual = externalMovementRepository.list(1, 10, "date", false, Collections.singletonMap(ExternalMovementFilter.END_DATE, LocalDate.parse("2015-01-01")))
    Assertions.assertEquals(emptyList<ExternalMovementEntity>(), actual)
  }

  @Test
  fun `should return no movements if the start date is after the end date`() {
    val actual = externalMovementRepository.list(1, 10, "date", false, mapOf(ExternalMovementFilter.START_DATE to LocalDate.parse("2023-05-01"), ExternalMovementFilter.END_DATE to LocalDate.parse("2023-04-25")))
    Assertions.assertEquals(emptyList<ExternalMovementEntity>(), actual)
  }

  private fun assertExternalMovements(sortColumn: String, expectedForAscending: ExternalMovementEntity, expectedForDescending: ExternalMovementEntity): List<DynamicTest> {
    return listOf(
      true to listOf(expectedForAscending),
      false to listOf(expectedForDescending),
    )
      .map { (sortedAsc, expected) ->
        DynamicTest.dynamicTest("When sorting by $sortColumn and sortedAsc is $sortedAsc the result is $expected") {
          val actual = externalMovementRepository.list(1, 1, sortColumn, sortedAsc, emptyMap())
          Assertions.assertEquals(expected, actual)
          Assertions.assertEquals(1, actual.size)
        }
      }
  }

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
}
