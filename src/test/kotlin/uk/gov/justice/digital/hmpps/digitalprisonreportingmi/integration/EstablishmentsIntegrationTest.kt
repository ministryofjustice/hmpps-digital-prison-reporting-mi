package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.Establishment
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.EstablishmentRepository
import java.util.stream.IntStream

class EstablishmentsIntegrationTest : IntegrationTestBase() {

  @Autowired
  lateinit var establishmentRepository: EstablishmentRepository

  @BeforeEach
  fun setup() {
    establishmentRepository.deleteAll()
  }

  @Test
  fun `Establishments count returns expected value`() {
    val expectedCount = 2
    IntStream.range(0, expectedCount).parallel().forEach {
      establishmentRepository.save(Establishment("Establishment $it", it.toString()))
    }

    webTestClient.get()
      .uri("/establishments/count")
      .exchange()
      .expectStatus()
      .isOk
      .expectBody()
      .jsonPath("count").isEqualTo(expectedCount)
  }

  @Test
  fun `Establishments count returns 200 with count zero when the table is empty`() {
    webTestClient.get()
      .uri("/establishments/count")
      .exchange()
      .expectStatus()
      .isOk
      .expectBody()
      .jsonPath("count").isEqualTo(0)
  }
}
