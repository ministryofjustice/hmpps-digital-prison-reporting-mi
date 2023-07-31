package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.FakeExternalMovementRepositoryTest.AllMovements.externalMovement1
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.FakeExternalMovementRepositoryTest.AllMovements.externalMovement2
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.FakeExternalMovementRepositoryTest.AllMovements.externalMovement3
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.FakeExternalMovementRepositoryTest.AllMovements.externalMovement4
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.FakeExternalMovementRepositoryTest.AllMovements.externalMovement5
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter.DIRECTION
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter.END_DATE
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter.START_DATE
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Collections.singletonMap

@Disabled
class FakeExternalMovementRepositoryTest {

  private val externalMovementRepository = FakeExternalMovementRepository()

  @Test
  fun `should return 2 external movements for the selected page 2 and pageSize 2 sorted by date in ascending order`() {
    val actual = externalMovementRepository.list(2, 2, "date", true, emptyMap())
    assertEquals(listOf(externalMovement3, externalMovement4), actual)
    assertEquals(2, actual.size)
  }

  @Test
  fun `should return 1 external movement for the selected page 3 and pageSize 2 sorted by date in ascending order`() {
    val actual = externalMovementRepository.list(3, 2, "date", true, emptyMap())
    assertEquals(listOf(externalMovement5), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return 5 external movements for the selected page 1 and pageSize 5 sorted by date in ascending order`() {
    val actual = externalMovementRepository.list(1, 5, "date", true, emptyMap())
    assertEquals(listOf(externalMovement1, externalMovement2, externalMovement3, externalMovement4, externalMovement5), actual)
    assertEquals(5, actual.size)
  }

  @Test
  fun `should return an empty list for the selected page 2 and pageSize 5 sorted by date in ascending order`() {
    val actual = externalMovementRepository.list(2, 5, "date", true, emptyMap())
    assertEquals(emptyList<ExternalMovementEntity>(), actual)
  }

  @Test
  fun `should return an empty list for the selected page 6 and pageSize 1 sorted by date in ascending order`() {
    val actual = externalMovementRepository.list(6, 1, "date", true, emptyMap())
    assertEquals(emptyList<ExternalMovementEntity>(), actual)
  }

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by date when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "date", expectedForAscending = externalMovement1, expectedForDescending = externalMovement5)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by time when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "time", expectedForAscending = externalMovement1, expectedForDescending = externalMovement4)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by prisoner when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "prisoner", expectedForAscending = externalMovement3, expectedForDescending = externalMovement1)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'from' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "origin", expectedForAscending = externalMovement4, expectedForDescending = externalMovement3)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'to' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "destination", expectedForAscending = externalMovement3, expectedForDescending = externalMovement2)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'direction' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "direction", expectedForAscending = externalMovement1, expectedForDescending = externalMovement4)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'type' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "type", expectedForAscending = externalMovement1, expectedForDescending = externalMovement5)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'reason' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "reason", expectedForAscending = externalMovement2, expectedForDescending = externalMovement1)

  @Test
  fun `should return a list of all results with no filters`() {
    val actual = externalMovementRepository.list(1, 20, "date", true, emptyMap())
    assertEquals(5, actual.size)
  }

  @Test
  fun `should return a list of inwards movements with an in direction filter`() {
    val actual = externalMovementRepository.list(1, 20, "date", true, singletonMap(DIRECTION, "in"))
    assertEquals(4, actual.size)
  }

  @Test
  fun `should return a list of inwards movements with an in direction filter regardless of the casing`() {
    val actual = externalMovementRepository.list(1, 20, "date", true, singletonMap(DIRECTION, "In"))
    assertEquals(4, actual.size)
  }

  @Test
  fun `should return a list of outwards movements with an out direction filter`() {
    val actual = externalMovementRepository.list(1, 20, "date", true, singletonMap(DIRECTION, "out"))
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return a list of outwards movements with an out direction filter regardless of the casing`() {
    val actual = externalMovementRepository.list(1, 20, "date", true, singletonMap(DIRECTION, "Out"))
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return a count of all results with no filters`() {
    val actual = externalMovementRepository.count(emptyMap())
    assertEquals(5L, actual)
  }

  @Test
  fun `should return a count of inwards movements with an in direction filter`() {
    val actual = externalMovementRepository.count(singletonMap(DIRECTION, "in"))
    assertEquals(4L, actual)
  }

  @Test
  fun `should return a count of outwards movements with an out direction filter`() {
    val actual = externalMovementRepository.count(singletonMap(DIRECTION, "out"))
    assertEquals(1L, actual)
  }

  @Test
  fun `should return a count of movements with a startDate filter`() {
    val actual = externalMovementRepository.count(singletonMap(START_DATE, LocalDate.parse("2023-05-01")))
    assertEquals(2, actual)
  }

  @Test
  fun `should return a count of movements with a endDate filter`() {
    val actual = externalMovementRepository.count(singletonMap(END_DATE, LocalDate.parse("2023-01-31")))
    assertEquals(1, actual)
  }

  @Test
  fun `should return a count of movements with a startDate and an endDate filter`() {
    val actual = externalMovementRepository.count(mapOf(START_DATE to LocalDate.parse("2023-04-30"), END_DATE to LocalDate.parse("2023-05-01")))
    assertEquals(2, actual)
  }

  @Test
  fun `should return a count of zero with a startDate greater than the latest movement date`() {
    val actual = externalMovementRepository.count(mapOf(START_DATE to LocalDate.parse("2025-04-30")))
    assertEquals(0, actual)
  }

  @Test
  fun `should return a count of zero with an endDate less than the earliest movement date`() {
    val actual = externalMovementRepository.count(mapOf(END_DATE to LocalDate.parse("2019-04-30")))
    assertEquals(0, actual)
  }

  @Test
  fun `should return a count of zero if the start date is after the end date`() {
    val actual = externalMovementRepository.count(mapOf(START_DATE to LocalDate.parse("2023-04-30"), END_DATE to LocalDate.parse("2019-05-01")))
    assertEquals(0, actual)
  }

  @Test
  fun `should return all the movements on or after the provided start date`() {
    val actual = externalMovementRepository.list(1, 10, "date", false, singletonMap(START_DATE, LocalDate.parse("2023-04-30")))
    assertEquals(listOf(externalMovement5, externalMovement4, externalMovement3), actual)
  }

  @Test
  fun `should return all the movements on or before the provided end date`() {
    val actual = externalMovementRepository.list(1, 10, "date", false, singletonMap(END_DATE, LocalDate.parse("2023-04-25")))
    assertEquals(listOf(externalMovement2, externalMovement1), actual)
  }

  @Test
  fun `should return all the movements between the provided start and end dates`() {
    val actual = externalMovementRepository.list(1, 10, "date", false, mapOf(START_DATE to LocalDate.parse("2023-04-25"), END_DATE to LocalDate.parse("2023-05-20")))
    assertEquals(listOf(externalMovement5, externalMovement4, externalMovement3, externalMovement2), actual)
  }

  @Test
  fun `should return no movements if the start date is after the latest movement date`() {
    val actual = externalMovementRepository.list(1, 10, "date", false, singletonMap(START_DATE, LocalDate.parse("2025-01-01")))
    assertEquals(emptyList<ExternalMovementEntity>(), actual)
  }

  @Test
  fun `should return no movements if the end date is before the earliest movement date`() {
    val actual = externalMovementRepository.list(1, 10, "date", false, singletonMap(END_DATE, LocalDate.parse("2015-01-01")))
    assertEquals(emptyList<ExternalMovementEntity>(), actual)
  }

  @Test
  fun `should return no movements if the start date is after the end date`() {
    val actual = externalMovementRepository.list(1, 10, "date", false, mapOf(START_DATE to LocalDate.parse("2023-05-01"), END_DATE to LocalDate.parse("2023-04-25")))
    assertEquals(emptyList<ExternalMovementEntity>(), actual)
  }

  private fun assertExternalMovements(sortColumn: String, expectedForAscending: ExternalMovementEntity, expectedForDescending: ExternalMovementEntity): List<DynamicTest> {
    return listOf(
      true to listOf(expectedForAscending),
      false to listOf(expectedForDescending),
    )
      .map { (sortedAsc, expected) ->
        DynamicTest.dynamicTest("When sorting by $sortColumn and sortedAsc is $sortedAsc the result is $expected") {
          val actual = externalMovementRepository.list(1, 1, sortColumn, sortedAsc, emptyMap())
          assertEquals(expected, actual)
          assertEquals(1, actual.size)
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
