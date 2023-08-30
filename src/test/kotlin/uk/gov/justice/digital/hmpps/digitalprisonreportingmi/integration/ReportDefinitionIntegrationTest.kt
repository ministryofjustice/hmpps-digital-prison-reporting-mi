package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.util.UriBuilder
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepositoryTest
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepositoryTest.AllPrisoners
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepositoryTest.AllPrisoners.prisoner4800
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepositoryTest.AllPrisoners.prisoner5207
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepositoryTest.AllPrisoners.prisoner6851
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepositoryTest.AllPrisoners.prisoner7849
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepositoryTest.AllPrisoners.prisoner8894
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.PrisonerRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.model.ExternalMovementModel
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller.model.ReportDefinition

class ReportDefinitionIntegrationTest : IntegrationTestBase() {

  @Test
  fun `Stubbed definition is returned as expected`() {
    val result = webTestClient.get()
      .uri("/definitions")
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isOk
      .expectBodyList<ReportDefinition>()
      .returnResult()

    assertThat(result.responseBody).isNotNull
    assertThat(result.responseBody).hasSize(1)
    assertThat(result.responseBody).first().isNotNull

    val definition = result.responseBody!!.first()

    assertThat(definition.name).isEqualTo("External Movements")
    assertThat(definition.description).isNull()
    assertThat(definition.variants).hasSize(2)
    assertThat(definition.variants[0]).isNotNull
    assertThat(definition.variants[1]).isNotNull

    val allVariant = definition.variants[0]

    assertThat(allVariant.name).isEqualTo("list")
    assertThat(allVariant.description).isNull()
    assertThat(allVariant.displayName).isEqualTo("All movements")
    assertThat(allVariant.fields).hasSize(9)

    val lastWeekVariant = definition.variants[1]
    assertThat(lastWeekVariant.name).isEqualTo("last-week")
    assertThat(lastWeekVariant.description).isEqualTo("All movements in the past week")
    assertThat(lastWeekVariant.displayName).isEqualTo("Last week")
    assertThat(lastWeekVariant.fields).hasSize(9)
  }
}
