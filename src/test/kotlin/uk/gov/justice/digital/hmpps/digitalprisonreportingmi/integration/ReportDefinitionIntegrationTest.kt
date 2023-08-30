package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.expectBodyList
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

    assertThat(allVariant.id).isEqualTo("1.a")
    assertThat(allVariant.resourceName).isEqualTo("list")
    assertThat(allVariant.description).isNull()
    assertThat(allVariant.name).isEqualTo("All movements")
    assertThat(allVariant.fields).hasSize(9)

    val lastWeekVariant = definition.variants[1]
    assertThat(lastWeekVariant.id).isEqualTo("1.b")
    assertThat(lastWeekVariant.resourceName).isEqualTo("last-week")
    assertThat(lastWeekVariant.description).isEqualTo("All movements in the past week")
    assertThat(lastWeekVariant.name).isEqualTo("Last week")
    assertThat(lastWeekVariant.fields).hasSize(9)
  }
}
