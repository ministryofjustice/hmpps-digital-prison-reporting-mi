package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.validation.ValidationException
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement

@Service
class FakeExternalMovementRepository {

  fun externalMovements(selectedPage: Long, pageSize: Long, sortColumn: String, sortedAsc: Boolean): List<ExternalMovement> {
    val mapper = jacksonObjectMapper()
    mapper.registerModule(JavaTimeModule())
    return this::class.java.classLoader.getResource("fakeExternalMovementsData.json")?.readText()?.let {
      sort(sortColumn, mapper.readValue<List<ExternalMovement>>(it), sortedAsc)
        .stream()
        .skip((selectedPage - 1) * pageSize)
        .limit(pageSize)
        .toList()
    } ?: emptyList()
  }

  private fun sort(sortColumn: String, allExternalMovements: List<ExternalMovement>, sortedAsc: Boolean): List<ExternalMovement> {
    val allExternalMovementsSorted =
      when (sortColumn) {
        "date" -> allExternalMovements.sortedBy { it.date }
        "time" -> allExternalMovements.sortedBy { it.time }
        "prisonNumber" -> allExternalMovements.sortedBy { it.prisonNumber }
        "direction" -> allExternalMovements.sortedBy { it.direction }
        "from" -> allExternalMovements.sortedBy { it.from }
        "to" -> allExternalMovements.sortedBy { it.to }
        "type" -> allExternalMovements.sortedBy { it.type }
        "reason" -> allExternalMovements.sortedBy { it.reason }
        else -> throw ValidationException("Invalid sort column $sortColumn")
      }
    return if (!sortedAsc) allExternalMovementsSorted.reversed() else allExternalMovementsSorted
  }
}
