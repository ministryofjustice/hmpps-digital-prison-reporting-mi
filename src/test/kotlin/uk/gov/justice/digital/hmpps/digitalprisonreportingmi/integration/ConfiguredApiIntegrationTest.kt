package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.util.UriBuilder
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.DATE
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.DESTINATION
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.DIRECTION
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.NAME
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.ORIGIN
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.PRISON_NUMBER
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.REASON
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.TYPE
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.movementPrisoner1
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.movementPrisoner2
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.movementPrisoner3
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.movementPrisoner4
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryTest.AllMovementPrisoners.movementPrisoner5
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepositoryTest
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepositoryTest.AllPrisoners
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.PrisonerRepository

class ConfiguredApiIntegrationTest : IntegrationTestBase() {

  @Autowired
  lateinit var externalMovementRepository: ExternalMovementRepository

  @Autowired
  lateinit var prisonerRepository: PrisonerRepository

  @BeforeEach
  fun setup() {
    ExternalMovementRepositoryTest.AllMovements.allExternalMovements.forEach {
      externalMovementRepository.save(it)
    }
    AllPrisoners.allPrisoners.forEach {
      prisonerRepository.save(it)
    }
  }

  @Test
  fun `Configured API returns value from the repository`() {
    webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/external-movements/external-movements/last-month")
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
        {"PRISONNUMBER": "${movementPrisoner5[PRISON_NUMBER]}", "NAME": "${movementPrisoner5[NAME]}", "DATE": "${movementPrisoner5[DATE]}", "ORIGIN": "${movementPrisoner5[ORIGIN]}", "DESTINATION": "${movementPrisoner5[DESTINATION]}", "DIRECTION": "${movementPrisoner5[DIRECTION]}", "TYPE": "${movementPrisoner5[TYPE]}", "REASON": "${movementPrisoner5[REASON]}"},
        {"PRISONNUMBER": "${movementPrisoner4[PRISON_NUMBER]}", "NAME": "${movementPrisoner4[NAME]}", "DATE": "${movementPrisoner4[DATE]}", "ORIGIN": "${movementPrisoner4[ORIGIN]}", "DESTINATION": "${movementPrisoner4[DESTINATION]}", "DIRECTION": "${movementPrisoner4[DIRECTION]}", "TYPE": "${movementPrisoner4[TYPE]}", "REASON": "${movementPrisoner4[REASON]}"},
        {"PRISONNUMBER": "${movementPrisoner3[PRISON_NUMBER]}", "NAME": "${movementPrisoner3[NAME]}", "DATE": "${movementPrisoner3[DATE]}", "ORIGIN": "${movementPrisoner3[ORIGIN]}", "DESTINATION": "${movementPrisoner3[DESTINATION]}", "DIRECTION": "${movementPrisoner3[DIRECTION]}", "TYPE": "${movementPrisoner3[TYPE]}", "REASON": "${movementPrisoner3[REASON]}"}
      ]       
      """,
      )
  }

  @Test
  fun `Configured API returns value matching the filters provided`() {
    webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/external-movements/external-movements/last-month")
          .queryParam("filters.date.start", "2023-04-25")
          .queryParam("filters.date.end", "2023-05-20")
          .queryParam("filters.direction", "Out")
          .build()
      }
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody()
      .json("""[
         {"PRISONNUMBER": "${movementPrisoner4[PRISON_NUMBER]}", "NAME": "${movementPrisoner4[NAME]}", "DATE": "${movementPrisoner4[DATE]}", "ORIGIN": "${movementPrisoner4[ORIGIN]}", "DESTINATION": "${movementPrisoner4[DESTINATION]}", "DIRECTION": "${movementPrisoner4[DIRECTION]}", "TYPE": "${movementPrisoner4[TYPE]}", "REASON": "${movementPrisoner4[REASON]}"}
      ]       
      """,
      )
    //         {"prisonNumber": "${prisoner7849.number}", "firstName": "${prisoner7849.firstName}", "lastName": "${prisoner7849.lastName}", "date": "2023-05-01", "time": "15:19:00", "from": "Cardiff", "to": "Maidstone", "direction": "Out", "type": "Transfer", "reason": "Transfer Out to Other Establishment"}
  }

  @Test
  fun `Configured API call without query params defaults to preset query params`() {
    webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/external-movements/external-movements/last-month")
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
        {"PRISONNUMBER": "${movementPrisoner5[PRISON_NUMBER]}", "NAME": "${movementPrisoner5[NAME]}", "DATE": "${movementPrisoner5[DATE]}", "ORIGIN": "${movementPrisoner5[ORIGIN]}", "DESTINATION": "${movementPrisoner5[DESTINATION]}", "DIRECTION": "${movementPrisoner5[DIRECTION]}", "TYPE": "${movementPrisoner5[TYPE]}", "REASON": "${movementPrisoner5[REASON]}"},
        {"PRISONNUMBER": "${movementPrisoner4[PRISON_NUMBER]}", "NAME": "${movementPrisoner4[NAME]}", "DATE": "${movementPrisoner4[DATE]}", "ORIGIN": "${movementPrisoner4[ORIGIN]}", "DESTINATION": "${movementPrisoner4[DESTINATION]}", "DIRECTION": "${movementPrisoner4[DIRECTION]}", "TYPE": "${movementPrisoner4[TYPE]}", "REASON": "${movementPrisoner4[REASON]}"},
        {"PRISONNUMBER": "${movementPrisoner3[PRISON_NUMBER]}", "NAME": "${movementPrisoner3[NAME]}", "DATE": "${movementPrisoner3[DATE]}", "ORIGIN": "${movementPrisoner3[ORIGIN]}", "DESTINATION": "${movementPrisoner3[DESTINATION]}", "DIRECTION": "${movementPrisoner3[DIRECTION]}", "TYPE": "${movementPrisoner3[TYPE]}", "REASON": "${movementPrisoner3[REASON]}"},
        {"PRISONNUMBER": "${movementPrisoner2[PRISON_NUMBER]}", "NAME": "${movementPrisoner2[NAME]}", "DATE": "${movementPrisoner2[DATE]}", "ORIGIN": "${movementPrisoner2[ORIGIN]}", "DESTINATION": "${movementPrisoner2[DESTINATION]}", "DIRECTION": "${movementPrisoner2[DIRECTION]}", "TYPE": "${movementPrisoner2[TYPE]}", "REASON": "${movementPrisoner2[REASON]}"},
        {"PRISONNUMBER": "${movementPrisoner1[PRISON_NUMBER]}", "NAME": "${movementPrisoner1[NAME]}", "DATE": "${movementPrisoner1[DATE]}", "ORIGIN": "${movementPrisoner1[ORIGIN]}", "DESTINATION": "${movementPrisoner1[DESTINATION]}", "DIRECTION": "${movementPrisoner1[DIRECTION]}", "TYPE": "${movementPrisoner1[TYPE]}", "REASON": "${movementPrisoner1[REASON]}"}
      ]
      """
      )
  }

  @ParameterizedTest
  @CsvSource(
    "In,  4",
    "Out, 1",
    ",    5",
  )
  fun `Configured API returns filtered values`(direction: String?, numberOfResults: Int) {
    val results = webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/external-movements/external-movements/last-month")
          .queryParam("filters.direction", direction)
          .build()
      }
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isOk()
      .expectBodyList<Map<String,Any>>()
      .hasSize(numberOfResults)
      .returnResult()
      .responseBody

    if (direction != null) {
      results?.forEach {
        assertThat(it[DIRECTION]).isEqualTo(direction)
      }
    }
  }

  @Test
  fun `Configured API returns 400 for invalid selectedPage query param`() {
    requestWithQueryAndAssert400("selectedPage", 0, "/external-movements/external-movements/last-month")
  }

  @Test
  fun `Configured API returns 400 for invalid pageSize query param`() {
    requestWithQueryAndAssert400("pageSize", 0, "/external-movements/external-movements/last-month")
  }

  @Test
  fun `Configured API returns 400 for invalid (wrong type) pageSize query param`() {
    requestWithQueryAndAssert400("pageSize", "a", "/external-movements/external-movements/last-month")
  }

  @Test
  fun `Configured API returns 400 for invalid sortColumn query param`() {
    requestWithQueryAndAssert400("sortColumn", "nonExistentColumn", "/external-movements/external-movements/last-month")
  }

  @Test
  fun `Configured API returns 400 for invalid sortedAsc query param`() {
    requestWithQueryAndAssert400("sortedAsc", "abc", "/external-movements/external-movements/last-month")
  }

  @Test
  fun `Configured API returns 400 for invalid startDate query param`() {
    requestWithQueryAndAssert400("filters.date.start", "abc", "/external-movements/external-movements/last-month")
  }

  @Test
  fun `External movements returns 400 for invalid endDate query param`() {
    requestWithQueryAndAssert400("endDate", "b", "/external-movements")
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
