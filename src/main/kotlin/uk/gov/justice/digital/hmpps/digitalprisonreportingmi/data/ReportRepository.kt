package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Component

const val COUNT_STRING = "SELECT COUNT(*) FROM :tableName;"

@Component
class ReportRepository {
  @PersistenceContext
  lateinit var entityManager: EntityManager

  fun count(domain: String, table: String) : Long {
    return entityManager.createNativeQuery(COUNT_STRING)
      .setParameter("tableName", "${domain}_${table}")
      .singleResult as Long
  }
}
