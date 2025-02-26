package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.security

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.CaseloadProvider
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.service.model.Caseload

@Component
class FakeCaseloadProvider : CaseloadProvider {
  override fun getActiveCaseloadId(jwt: Jwt): String = "LWSTMC"

  override fun getCaseloads(jwt: Jwt): List<Caseload> = listOf(Caseload("LWSTMC", "Lowestoft (North East Suffolk) Magistrat"))
}
