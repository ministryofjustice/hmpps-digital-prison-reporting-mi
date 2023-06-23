package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement

@Service
class ExternalMovementService {

  fun externalMovements(selectedPage: Int, pageSize: Int, sortColumn: String, sortedAsc:Boolean): List<ExternalMovement> {
    return emptyList()
  }
}
