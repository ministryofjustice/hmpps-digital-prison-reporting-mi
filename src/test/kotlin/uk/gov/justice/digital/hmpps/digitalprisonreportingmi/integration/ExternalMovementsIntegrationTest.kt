package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import org.junit.jupiter.api.Test
import org.springframework.web.util.UriBuilder

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
      .jsonPath("count").isEqualTo("501")
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
      .json("""[
        {"prisonNumber":"J0840YP","date":"2023-06-05","time":"12:56:00","from":"Elmley","to":"Altcourse","direction":"Out","type":"Transfer","reason":"Transfer Out to Other Establishment"},
        {"prisonNumber":"R2320PI","date":"2023-06-05","time":"04:27:00","from":"Northumberland","to":"Usk","direction":"Out","type":"Transfer","reason":"Transfer Out to Other Establishment"},
        {"prisonNumber":"B0030SC","date":"2023-06-05","time":"07:07:00","from":"Usk","to":"Thameside","direction":"In","type":"Admission","reason":"Unconvicted Remand"}
      ]       
      """)
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
      .json("""
      [
        {"prisonNumber":"J0840YP","date":"2023-06-05","time":"12:56:00","from":"Elmley","to":"Altcourse","direction":"Out","type":"Transfer","reason":"Transfer Out to Other Establishment"},
        {"prisonNumber":"R2320PI","date":"2023-06-05","time":"04:27:00","from":"Northumberland","to":"Usk","direction":"Out","type":"Transfer","reason":"Transfer Out to Other Establishment"},
        {"prisonNumber":"B0030SC","date":"2023-06-05","time":"07:07:00","from":"Usk","to":"Thameside","direction":"In","type":"Admission","reason":"Unconvicted Remand"},
        {"prisonNumber":"F3220GG","date":"2023-06-03","time":"21:43:00","from":"Kirkham","to":"Elmley","direction":"In","type":"Admission","reason":"Unconvicted Remand"},
        {"prisonNumber":"Q3430VX","date":"2023-06-02","time":"04:06:00","from":"Usk","to":"Send","direction":"Out","type":"Transfer","reason":"Transfer Out to Other Establishment"},
        {"prisonNumber":"F4010GF","date":"2023-06-01","time":"00:14:00","from":"Wakefield","to":"Garth","direction":"Out","type":"Transfer","reason":"Other"},
        {"prisonNumber":"J2800XP","date":"2023-04-28","time":"02:16:00","from":"Pentonville","to":"Featherstone","direction":"Out","type":"Transfer","reason":"Other"},
        {"prisonNumber":"J4650MC","date":"2023-04-27","time":"17:16:00","from":"Thameside","to":"Thameside","direction":"Out","type":"Transfer","reason":"Other"},
        {"prisonNumber":"Q9660WX","date":"2023-04-25","time":"12:19:00","from":"Elmley","to":"Pentonville","direction":"In","type":"Transfer","reason":"Transfer In from Other Establishment"},
        {"prisonNumber":"E0550PS","date":"2023-04-17","time":"23:48:00","from":"Lancaster Farms","to":"Garth","direction":"In","type":"Transfer","reason":"Transfer In from Other Establishment"}
      ]
      """)
  }

}
