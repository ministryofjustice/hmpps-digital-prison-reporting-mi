package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.springframework.data.jpa.repository.JpaRepository

interface ConfiguredApiRepository: JpaRepository<Map<String,String>, String>, ConfiguredApiRepositoryCustom{

}
