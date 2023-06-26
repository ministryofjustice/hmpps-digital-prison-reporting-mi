package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ExternalMovementServiceTest.AllMovements.allExternalMovements
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ExternalMovementServiceTest.AllMovements.externalMovement1
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ExternalMovementServiceTest.AllMovements.externalMovement2
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ExternalMovementServiceTest.AllMovements.externalMovement3
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ExternalMovementServiceTest.AllMovements.externalMovement4
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ExternalMovementServiceTest.AllMovements.externalMovement5
import java.time.LocalDate
import java.time.LocalTime

class ExternalMovementServiceTest {

  private val externalMovementRepository: ExternalMovementRepository = mock<ExternalMovementRepository>()
  private val externalMovementService = ExternalMovementService(externalMovementRepository)

  @BeforeEach
  fun setup() {
    whenever(externalMovementRepository.externalMovements()).thenReturn(allExternalMovements)
  }

  @Test
  fun `should return 2 external movements for the selected page 2 and pageSize 2 sorted by date in ascending order`() {
    val actual = externalMovementService.externalMovements(2, 2, "date", true)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement3, externalMovement4), actual)
    assertEquals(2, actual.size)
  }

  @Test
  fun `should return 1 external movement for the selected page 3 and pageSize 2 sorted by date in ascending order`() {
    val actual = externalMovementService.externalMovements(3, 2, "date", true)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement5), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return 5 external movements for the selected page 1 and pageSize 5 sorted by date in ascending order`() {
    val actual = externalMovementService.externalMovements(1, 5, "date", true)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement1, externalMovement2, externalMovement3, externalMovement4, externalMovement5), actual)
    assertEquals(5, actual.size)
  }

  @Test
  fun `should return an empty list for the selected page 2 and pageSize 5 sorted by date in ascending order`() {
    val actual = externalMovementService.externalMovements(2, 5, "date", true)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(emptyList<ExternalMovement>(), actual)
  }

  @Test
  fun `should return an empty list for the selected page 6 and pageSize 1 sorted by date in ascending order`() {
    val actual = externalMovementService.externalMovements(6, 1, "date", true)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(emptyList<ExternalMovement>(), actual)
  }

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by date when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "date", expectedForAscending = externalMovement1, expectedForDescending = externalMovement5)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by time when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "time", expectedForAscending = externalMovement1, expectedForDescending = externalMovement4)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by prisonNumber when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "prisonNumber", expectedForAscending = externalMovement3, expectedForDescending = externalMovement4)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'from' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "from", expectedForAscending = externalMovement4, expectedForDescending = externalMovement3)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'to' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "to", expectedForAscending = externalMovement3, expectedForDescending = externalMovement2)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'direction' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "direction", expectedForAscending = externalMovement1, expectedForDescending = externalMovement4)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'type' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "type", expectedForAscending = externalMovement1, expectedForDescending = externalMovement5)

  @TestFactory
  fun `should return all external movements for the selected page and pageSize sorted by 'reason' when sortedAsc is true and when it is false`() =
    assertExternalMovements(sortColumn = "reason", expectedForAscending = externalMovement2, expectedForDescending = externalMovement1)

  private fun assertExternalMovements(sortColumn: String, expectedForAscending: ExternalMovement, expectedForDescending: ExternalMovement): List<DynamicTest> {
    return listOf(
      true to listOf(expectedForAscending),
      false to listOf(expectedForDescending),
    )
      .map { (sortedAsc, expected) ->
        DynamicTest.dynamicTest("When sorting by $sortColumn and sortedAsc is $sortedAsc the result is $expected") {
          val actual = externalMovementService.externalMovements(1, 1, sortColumn, sortedAsc)
          verify(externalMovementRepository, times(1)).externalMovements()
          Mockito.clearInvocations(externalMovementRepository)
          assertEquals(expected, actual)
          assertEquals(1, actual.size)
        }
      }
  }
  private object AllMovements {
    val externalMovement1 = ExternalMovement(
      "N9980PJ",
      LocalDate.of(2023, 1, 31),
      LocalTime.of(3, 1),
      "Ranby",
      "Kirkham",
      "In",
      "Admission",
      "Unconvicted Remand",
    )
    val externalMovement2 = ExternalMovement(
      "Q9660WX",
      LocalDate.of(2023, 4, 25),
      LocalTime.of(12, 19),
      "Elmley",
      "Pentonville",
      "In",
      "Transfer",
      "Transfer In from Other Establishment",
    )
    val externalMovement3 = ExternalMovement(
      "A966ZZZ",
      LocalDate.of(2023, 4, 30),
      LocalTime.of(13, 19),
      "Wakefield",
      "Dartmoor",
      "In",
      "Transfer",
      "Transfer In from Other Establishment",
    )
    val externalMovement4 = ExternalMovement(
      "Z966YYY",
      LocalDate.of(2023, 5, 1),
      LocalTime.of(15, 19),
      "Cardiff",
      "Maidstone",
      "Out",
      "Transfer",
      "Transfer In from Other Establishment",
    )
    val externalMovement5 = ExternalMovement(
      "Q966ABC",
      LocalDate.of(2023, 5, 20),
      LocalTime.of(14, 0),
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
