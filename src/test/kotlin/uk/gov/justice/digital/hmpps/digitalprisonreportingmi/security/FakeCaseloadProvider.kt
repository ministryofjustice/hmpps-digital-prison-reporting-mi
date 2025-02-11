package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.security

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.CaseloadProvider

@Component
class FakeCaseloadProvider : CaseloadProvider {
  override fun getActiveCaseloadId(jwt: Jwt): String {
    return "LWSTMC"
  }

  override fun getCaseloadIds(jwt: Jwt): List<String> {
    return listOf("LWSTMC")
  }
}
