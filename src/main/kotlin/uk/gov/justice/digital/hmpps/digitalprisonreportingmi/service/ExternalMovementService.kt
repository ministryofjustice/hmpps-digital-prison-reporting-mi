package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.FakeExternalMovementRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.Count
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement

@Service
data class ExternalMovementService(val fakeExternalMovementRepository: FakeExternalMovementRepository) {

  fun list(selectedPage: Long, pageSize: Long, sortColumn: String, sortedAsc: Boolean, filters: Map<String, String>): List<ExternalMovement> {
    return fakeExternalMovementRepository.list(selectedPage, pageSize, sortColumn, sortedAsc, filters)
  }

  fun count(filters: Map<String, String>): Count {
    return Count(fakeExternalMovementRepository.count(filters))
  }
}
