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
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter.DIRECTION
import java.util.Collections.singletonMap

class ExternalMovementServiceTest {

  private val fakeExternalMovementRepository: FakeExternalMovementRepository = mock<FakeExternalMovementRepository>()
  private val externalMovementService = ExternalMovementService(fakeExternalMovementRepository)

  @BeforeEach
  fun setup() {
    whenever(fakeExternalMovementRepository.list(any(), any(), any(), any(), any())).thenReturn(allExternalMovements)
  }

  @Test
  fun `should call the repository with the corresponding arguments and get the list of movements`() {
    val sortColumn = "date"
    val actual = externalMovementService.list(2, 2, sortColumn, true, singletonMap(DIRECTION, "in"))
    verify(fakeExternalMovementRepository, times(1)).list(2, 2, sortColumn, true, singletonMap(DIRECTION, "in"))
    assertEquals(allExternalMovements, actual)
  }
}
