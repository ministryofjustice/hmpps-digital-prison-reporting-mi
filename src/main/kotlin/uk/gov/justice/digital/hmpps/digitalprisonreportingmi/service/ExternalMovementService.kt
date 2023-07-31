package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementEntity
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.Count
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementFilter
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovementModel

@Service
data class ExternalMovementService(val externalMovementRepository: ExternalMovementRepository) {

  fun list(selectedPage: Long, pageSize: Long, sortColumn: String, sortedAsc: Boolean, filters: Map<ExternalMovementFilter, Any>): List<ExternalMovementModel> {
    return externalMovementRepository.list(selectedPage, pageSize, sortColumn, sortedAsc, filters).map { e -> toModel(e) }
  }

  fun count(filters: Map<ExternalMovementFilter, Any>): Count {
    return Count(externalMovementRepository.count(filters))
  }

  private fun toModel(entity: ExternalMovementEntity): ExternalMovementModel {
    return ExternalMovementModel(entity.id, entity.prisoner.toString(), entity.date.toLocalDate(), entity.time.toLocalTime(), entity.origin, entity.destination, entity.direction, entity.type, entity.reason)
  }
}
