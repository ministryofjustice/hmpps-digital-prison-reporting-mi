package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.FakeExternalMovementRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement

@Service
data class ExternalMovementService(val fakeExternalMovementRepository: FakeExternalMovementRepository) {

  fun externalMovements(selectedPage: Long, pageSize: Long, sortColumn: String, sortedAsc: Boolean): List<ExternalMovement> {
    return fakeExternalMovementRepository.externalMovements(selectedPage, pageSize, sortColumn, sortedAsc)
  }
}
