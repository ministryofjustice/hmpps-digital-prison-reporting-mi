package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.springframework.data.jpa.repository.JpaRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.ExternalMovementEntity

interface ExternalMovementRepository : JpaRepository<ExternalMovementEntity, String>
