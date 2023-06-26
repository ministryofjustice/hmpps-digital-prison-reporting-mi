package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement

@Service
class ExternalMovementRepository {

  fun externalMovements(): List<ExternalMovement> {
    val mapper = jacksonObjectMapper()
    mapper.registerModule(JavaTimeModule())
    val json = this::class.java.classLoader.getResource("fakeExternalMovementsData.json")?.readText()
    return json?.let { mapper.readValue<List<ExternalMovement>>(it) } ?: emptyList()
  }
}
