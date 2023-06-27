package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.FakeExternalMovementRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.FakeExternalMovementRepositoryTest.AllMovements.allExternalMovements

class ExternalMovementServiceTest {

  private val fakeExternalMovementRepository: FakeExternalMovementRepository = mock<FakeExternalMovementRepository>()
  private val externalMovementService = ExternalMovementService(fakeExternalMovementRepository)

  @BeforeEach
  fun setup() {
    whenever(fakeExternalMovementRepository.externalMovements(any(), any(), any(), any())).thenReturn(allExternalMovements)
  }

  @Test
  fun `should call the repository with the corresponding arguments and get the list of movements`() {
    val sortColumn = "date"
    val actual = externalMovementService.externalMovements(2, 2, sortColumn, true)
    verify(fakeExternalMovementRepository, times(1)).externalMovements(2, 2, sortColumn, true)
    assertEquals(allExternalMovements, actual)
  }
}
