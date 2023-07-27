package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.springframework.data.jpa.repository.JpaRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement

interface ExternalMovementRepository : JpaRepository<ExternalMovement, String>, ExternalMovementRepositoryCustom
