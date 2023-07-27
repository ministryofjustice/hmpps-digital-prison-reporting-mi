package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.util.UriBuilder
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement

class ExternalMovementsIntegrationTest : IntegrationTestBase() {

  @Test
  fun `External movements count returns stubbed value`() {
    webTestClient.get()
      .uri("/external-movements/count")
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isOk
      .expectBody()
      .jsonPath("count").isEqualTo("5")
  }

  @ParameterizedTest
  @CsvSource(
    "In,  4",
    "Out, 1",
    ",    5",
  )
  fun `External movements count returns filtered value`(direction: String?, numberOfResults: Int) {
    webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/external-movements/count")
          .queryParam("direction", direction?.lowercase())
          .build()
      }
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody()
      .jsonPath("count").isEqualTo(numberOfResults.toString())
  }

  @Test
  fun `External movements returns stubbed value`() {
    webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/external-movements")
          .queryParam("selectedPage", 1)
          .queryParam("pageSize", 3)
          .queryParam("sortColumn", "date")
          .queryParam("sortedAsc", false)
          .build()
      }
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody()
      .json(
        """[
        {"prisonNumber":"Q966ABC","date":"2023-05-20","time":"14:00:00","origin":"Isle of Wight","destination":"Northumberland","direction":"In","type":"Transfer","reason":"Transfer In from Other Establishment"},
        {"prisonNumber":"Z966YYY","date":"2023-05-01","time":"15:19:00","origin":"Cardiff","destination":"Maidstone","direction":"Out","type":"Transfer","reason":"Transfer Out to Other Establishment"},
        {"prisonNumber":"A966ZZZ","date":"2023-04-30","time":"13:19:00","origin":"Wakefield","destination":"Dartmoor","direction":"In","type":"Transfer","reason":"Transfer In from Other Establishment"}
      ]       
      """,
      )
  }

  @Test
  fun `External movements call without query params defaults to preset query params`() {
    webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/external-movements")
          .build()
      }
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isOk
      .expectBody()
      .json(
        """
      [
        {"prisonNumber":"Q966ABC","date":"2023-05-20","time":"14:00:00","origin":"Isle of Wight","destination":"Northumberland","direction":"In","type":"Transfer","reason":"Transfer In from Other Establishment"},
        {"prisonNumber":"Z966YYY","date":"2023-05-01","time":"15:19:00","origin":"Cardiff","destination":"Maidstone","direction":"Out","type":"Transfer","reason":"Transfer Out to Other Establishment"},
        {"prisonNumber":"A966ZZZ","date":"2023-04-30","time":"13:19:00","origin":"Wakefield","destination":"Dartmoor","direction":"In","type":"Transfer","reason":"Transfer In from Other Establishment"},
        {"prisonNumber":"Q9660WX","date": "2023-04-25","time":"12:19:00","origin":"Elmley","destination":"Pentonville","direction":"In","type":"Transfer","reason":"Transfer In from Other Establishment"},
        {"prisonNumber":"N9980PJ","date": "2023-01-31","time":"03:01:00","origin":"Ranby","destination":"Kirkham","direction":"In","type":"Admission","reason":"Unconvicted Remand"}
      ]
      """,
      )
  }

  @ParameterizedTest
  @CsvSource(
    "In,  4",
    "Out, 1",
    ",    5",
  )
  fun `External movements returns filtered values`(direction: String?, numberOfResults: Int) {
    val results = webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/external-movements")
          .queryParam("direction", direction?.lowercase())
          .build()
      }
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isOk()
      .expectBodyList<ExternalMovement>()
      .hasSize(numberOfResults)
      .returnResult()
      .responseBody

    if (direction != null) {
      results?.forEach {
        assertThat(it.direction).isEqualTo(direction)
      }
    }
  }

  @Test
  fun `External movements returns 400 for invalid selectedPage query param`() {
    requestWithQueryAndAssert400("selectedPage", 0, "/external-movements")
  }

  @Test
  fun `External movements returns 400 for invalid pageSize query param`() {
    requestWithQueryAndAssert400("pageSize", 0, "/external-movements")
  }

  @Test
  fun `External movements returns 400 for invalid (wrong type) pageSize query param`() {
    requestWithQueryAndAssert400("pageSize", "a", "/external-movements")
  }

  @Test
  fun `External movements returns 400 for invalid sortColumn query param`() {
    requestWithQueryAndAssert400("sortColumn", "nonExistentColumn", "/external-movements")
  }

  @Test
  fun `External movements returns 400 for invalid sortedAsc query param`() {
    requestWithQueryAndAssert400("sortedAsc", "abc", "/external-movements")
  }

  @Test
  fun `External movements returns 400 for invalid startDate query param`() {
    requestWithQueryAndAssert400("startDate", "abc", "/external-movements")
  }

  @Test
  fun `External movements returns 400 for invalid endDate query param`() {
    requestWithQueryAndAssert400("endDate", "b", "/external-movements")
  }

  @Test
  fun `External movements count returns 400 for invalid startDate query param`() {
    requestWithQueryAndAssert400("startDate", "a", "/external-movements/count")
  }

  @Test
  fun `External movements count returns 400 for invalid endDate query param`() {
    requestWithQueryAndAssert400("endDate", "17-12-2050", "/external-movements/count")
  }

  @Test
  fun `External movements returns stubbed value matching the filters provided`() {
    webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/external-movements")
          .queryParam("startDate", "2023-04-25")
          .queryParam("endDate", "2023-05-20")
          .queryParam("direction", "out")
          .build()
      }
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody()
      .json(
        """[
         {"prisonNumber": "Z966YYY", "date": "2023-05-01", "time": "15:19:00", "origin": "Cardiff", "destination": "Maidstone", "direction": "Out", "type": "Transfer", "reason": "Transfer Out to Other Establishment"}
      ]       
      """,
      )
  }

  private fun requestWithQueryAndAssert400(paramName: String, paramValue: Any, path: String) {
    webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path(path)
          .queryParam(paramName, paramValue)
          .build()
      }
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isBadRequest
  }
}
