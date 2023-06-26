package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by date in ascending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "date", true)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement1), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by date in descending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "date", false)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement5), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by time in ascending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "time", true)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement1), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by time in descending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "time", false)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement4), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by prisonNumber in ascending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "prisonNumber", true)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement3), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by prisonNumber in descending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "prisonNumber", false)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement4), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by 'from' in ascending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "from", true)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement4), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by 'from' in descending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "from", false)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement3), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by 'to' in ascending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "to", true)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement3), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by 'to' in descending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "to", false)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement2), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by direction in ascending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "direction", true)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement1), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by direction in descending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "direction", false)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement4), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by type in ascending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "type", true)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement1), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by type in descending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "type", false)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement5), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by reason in ascending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "reason", true)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement2), actual)
    assertEquals(1, actual.size)
  }

  @Test
  fun `should return all external movements for the selected page and pageSize sorted by reason in descending order`() {
    val actual = externalMovementService.externalMovements(1, 1, "reason", false)
    verify(externalMovementRepository, times(1)).externalMovements()
    assertEquals(listOf(externalMovement1), actual)
    assertEquals(1, actual.size)
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
