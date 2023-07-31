package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementEntity
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter.DIRECTION
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementModel
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ExternalMovementServiceTest.AllEntities.allExternalMovementEntities
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service.ExternalMovementServiceTest.AllModels.allExternalMovementModels
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Collections.singletonMap

class ExternalMovementServiceTest {

  private val externalMovementRepository: ExternalMovementRepository = mock<ExternalMovementRepository>()
  private val externalMovementService = ExternalMovementService(externalMovementRepository)

  @BeforeEach
  fun setup() {
    whenever(externalMovementRepository.list(any(), any(), any(), any(), any())).thenReturn(allExternalMovementEntities)
  }

  @Test
  fun `should call the repository with the corresponding arguments and get the list of movements`() {
    val sortColumn = "date"
    val actual = externalMovementService.list(2, 2, sortColumn, true, singletonMap(DIRECTION, "in"))
    verify(externalMovementRepository, times(1)).list(2, 2, sortColumn, true, singletonMap(DIRECTION, "in"))
    assertEquals(allExternalMovementModels, actual)
  }

  object AllModels {
    val externalMovement1 = ExternalMovementModel(
      1,
      "8894",
      LocalDate.of(2023, 1, 31),
      LocalTime.of(3, 1),
      "Ranby",
      "Kirkham",
      "In",
      "Admission",
      "Unconvicted Remand",
    )
    val externalMovement2 = ExternalMovementModel(
      2,
      "5207",
      LocalDate.of(2023, 4, 25),
      LocalTime.of(12, 19),
      "Elmley",
      "Pentonville",
      "In",
      "Transfer",
      "Transfer In from Other Establishment",
    )
    val allExternalMovementModels = listOf(
      externalMovement1,
      externalMovement2,
    )
  }

  object AllEntities {
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
    val allExternalMovementEntities = listOf(
      externalMovement1,
      externalMovement2,
    )
  }
}
