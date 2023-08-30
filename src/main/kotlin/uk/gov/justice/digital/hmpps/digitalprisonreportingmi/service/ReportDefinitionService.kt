package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.EstablishmentRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.FieldDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ReportDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.VariantDefinition

@Service
class ReportDefinitionService() {

  fun getListForUser(): List<ReportDefinition> {
    return listOf(
      ReportDefinition(
        name = "External Movements",
        variants = listOf(
          VariantDefinition(
            name = "list",
            displayName = "List",
            fields = listOf(
              FieldDefinition(
                name = "prisonNumber",
                displayName = "Prison Number",
              ),
              FieldDefinition(
                name = "name",
                displayName = "Name",
              ),
            )
          )
        )
      )
    )
  }

}
