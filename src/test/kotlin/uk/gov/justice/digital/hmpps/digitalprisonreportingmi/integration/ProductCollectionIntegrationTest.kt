package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.util.UriBuilder
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.productCollection.ProductCollection
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.productCollection.ProductCollectionSummary

/**
 * Just ensure that product collections works - smoke test
 */
class ProductCollectionIntegrationTest : IntegrationTestBase() {

  @BeforeEach
  override fun setup() {
    super.setup()
    productCollectionRepository.save(ProductCollection("col1", "", "", mutableSetOf(), mutableSetOf()))
  }

  @Test
  fun `should save and retrieve a product collection`() {
    val productCollections = webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/productCollections")
          .build()
      }
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isOk
      .expectBody<Collection<ProductCollectionSummary>>()
      .returnResult()
      .responseBody

    assertThat(productCollections).isNotEmpty()
  }
}
