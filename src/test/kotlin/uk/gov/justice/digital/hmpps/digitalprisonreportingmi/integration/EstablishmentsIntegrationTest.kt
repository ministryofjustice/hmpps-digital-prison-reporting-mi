package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import org.junit.jupiter.api.Test

class EstablishmentsIntegrationTest: IntegrationTestBase() {
  @Test
  fun `Establishments count returns stubbed value`() {
    webTestClient.get()
      .uri("/establishments/count")
      .exchange()
      .expectStatus()
      .isOk
      .expectBody()
      .jsonPath("count").isEqualTo("501")
  }
}