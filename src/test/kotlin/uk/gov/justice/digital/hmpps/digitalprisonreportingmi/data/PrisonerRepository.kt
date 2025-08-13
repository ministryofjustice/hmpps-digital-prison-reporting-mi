package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.springframework.data.repository.CrudRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.model.PrisonerEntity

interface PrisonerRepository : CrudRepository<PrisonerEntity, String>
