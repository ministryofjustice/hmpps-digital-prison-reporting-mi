package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.FakeExternalMovementRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.Count
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter

@Service
data class ExternalMovementService(val fakeExternalMovementRepository: FakeExternalMovementRepository) {

  fun list(selectedPage: Long, pageSize: Long, sortColumn: String, sortedAsc: Boolean, filters: Map<ExternalMovementFilter, Any>): List<ExternalMovement> {
    return fakeExternalMovementRepository.list(selectedPage, pageSize, sortColumn, sortedAsc, filters)
  }

  fun count(filters: Map<ExternalMovementFilter, Any>): Count {
    return Count(fakeExternalMovementRepository.count(filters))
  }
}
