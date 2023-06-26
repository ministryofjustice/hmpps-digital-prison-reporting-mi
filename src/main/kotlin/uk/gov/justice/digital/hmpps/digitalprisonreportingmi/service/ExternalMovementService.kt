package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import jakarta.validation.ValidationException
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement

@Service
data class ExternalMovementService(val externalMovementRepository: ExternalMovementRepository) {

  fun externalMovements(selectedPage: Long, pageSize: Long, sortColumn: String, sortedAsc: Boolean): List<ExternalMovement> {
    val allExternalMovements = externalMovementRepository
      .externalMovements()
//      .sortedWith( Comparator.comparing { em -> em.javaClass.getField(sortColumn).get(em)})
    val allExternalMovementsSorted = when (sortColumn) {
      "date" -> allExternalMovements.sortedBy { it.date }
      "time" -> allExternalMovements.sortedBy { it.time }
      "from" -> allExternalMovements.sortedBy { it.from }
      "to" -> allExternalMovements.sortedBy { it.to }
      "type" -> allExternalMovements.sortedBy { it.type }
      else -> throw ValidationException("Invalid sort column $sortColumn")
    }
    if (!sortedAsc) {
      allExternalMovementsSorted.asReversed()
    }
    return allExternalMovementsSorted
      .stream()
      .skip((selectedPage - 1) * pageSize)
      .limit(pageSize)
      .toList()
  }
}
