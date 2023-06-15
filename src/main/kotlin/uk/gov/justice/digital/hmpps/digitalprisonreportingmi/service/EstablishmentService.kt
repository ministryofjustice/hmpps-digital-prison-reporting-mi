package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.EstablishmentRepository

@Service
data class EstablishmentService(val establishmentRepository: EstablishmentRepository) {

  fun establishmentsCount(): Long {
    return establishmentRepository.count()
  }
}
