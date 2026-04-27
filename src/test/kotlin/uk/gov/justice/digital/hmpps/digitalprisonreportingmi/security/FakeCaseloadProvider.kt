package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.security

import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.CaseloadResponse
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.UserPermissionProvider
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.security.authentication.AuthUser
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.service.model.Caseload
import uk.gov.justice.hmpps.kotlin.auth.AuthSource

@Component
class FakeCaseloadProvider : UserPermissionProvider {
  override fun getUserInfo(username: String): AuthUser = AuthUser(
    username = "userA",
    active = true,
    name = "userA",
    authSource = AuthSource.NOMIS,
    userId = "abc123",
    uuid = "989q-2f3f-2g3-g34",
  )

  override fun getCaseloads(username: String): CaseloadResponse = CaseloadResponse(
    username = "userA",
    active = true,
    accountType = "GENERAL",
    activeCaseload = Caseload(id = "LWSTMC", name = "Lowestoft (North East Suffolk) Magistrat"),
    caseloads = listOf(Caseload("LWSTMC", "Lowestoft (North East Suffolk) Magistrat")),
  )

  override fun getUsersRoles(username: String): List<String> = emptyList()
}
