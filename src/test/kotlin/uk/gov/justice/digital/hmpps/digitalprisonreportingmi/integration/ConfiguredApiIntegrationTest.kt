package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.util.UriBuilder
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.controller.ConfiguredApiController.FiltersPrefix.FILTERS_PREFIX
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.controller.ConfiguredApiController.FiltersPrefix.RANGE_FILTER_END_SUFFIX
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.controller.ConfiguredApiController.FiltersPrefix.RANGE_FILTER_START_SUFFIX
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementEntity
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.PrisonerEntity
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.PrisonerRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration.ConfiguredApiIntegrationTest.AllMovementPrisoners.DATE
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration.ConfiguredApiIntegrationTest.AllMovementPrisoners.DESTINATION
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration.ConfiguredApiIntegrationTest.AllMovementPrisoners.DIRECTION
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration.ConfiguredApiIntegrationTest.AllMovementPrisoners.NAME
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration.ConfiguredApiIntegrationTest.AllMovementPrisoners.ORIGIN
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration.ConfiguredApiIntegrationTest.AllMovementPrisoners.PRISON_NUMBER
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration.ConfiguredApiIntegrationTest.AllMovementPrisoners.REASON
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration.ConfiguredApiIntegrationTest.AllMovementPrisoners.TYPE
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration.ConfiguredApiIntegrationTest.AllMovementPrisoners.movementPrisoner4
import java.time.LocalDateTime

class ConfiguredApiIntegrationTest : IntegrationTestBase() {

  @Autowired
  lateinit var externalMovementRepository: ExternalMovementRepository

  @Autowired
  lateinit var prisonerRepository: PrisonerRepository

  @BeforeEach
  override fun setup() {
    super.setup()
    AllMovements.allExternalMovements.forEach {
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
          .path("/reports/external-movements/last-month")
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
        {"prisonNumber": "${movementPrisoner4[PRISON_NUMBER]}", "name": "${movementPrisoner4[NAME]}", "date": "${movementPrisoner4[DATE]}", "origin": "${movementPrisoner4[ORIGIN]}", "destination": "${movementPrisoner4[DESTINATION]}", "direction": "${movementPrisoner4[DIRECTION]}", "type": "${movementPrisoner4[TYPE]}", "reason": "${movementPrisoner4[REASON]}"}
      ]       
      """,
      )
  }

  @Test
  fun `Configured API count returns the number of records`() {
    webTestClient.get()
      .uri("/reports/external-movements/last-month/count")
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isOk
      .expectBody()
      .jsonPath("count").isEqualTo("1")
  }

  @ParameterizedTest
  @CsvSource(
    "In,  0",
    "Out, 1",
    ",    1",
  )
  fun `Configured API count returns filtered value`(direction: String?, numberOfResults: Int) {
    webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/reports/external-movements/last-month/count")
          .queryParam("filters.direction", direction?.lowercase())
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
  fun `Configured API returns value matching the filters provided`() {
    webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/reports/external-movements/last-month")
          .queryParam("${FILTERS_PREFIX}date$RANGE_FILTER_START_SUFFIX", "2023-04-25")
          .queryParam("${FILTERS_PREFIX}date$RANGE_FILTER_END_SUFFIX", "2023-05-20")
          .queryParam("${FILTERS_PREFIX}direction", "out")
          .build()
      }
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody()
      .json(
        """[
         {"prisonNumber": "${movementPrisoner4[PRISON_NUMBER]}", "name": "${movementPrisoner4[NAME]}", "date": "${movementPrisoner4[DATE]}", "origin": "${movementPrisoner4[ORIGIN]}", "destination": "${movementPrisoner4[DESTINATION]}", "direction": "${movementPrisoner4[DIRECTION]}", "type": "${movementPrisoner4[TYPE]}", "reason": "${movementPrisoner4[REASON]}"}
      ]       
      """,
      )
  }

  @Test
  fun `Configured API call without query params defaults to preset query params`() {
    webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/reports/external-movements/last-month")
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
        {"prisonNumber": "${movementPrisoner4[PRISON_NUMBER]}", "name": "${movementPrisoner4[NAME]}", "date": "${movementPrisoner4[DATE]}", "origin": "${movementPrisoner4[ORIGIN]}", "destination": "${movementPrisoner4[DESTINATION]}", "direction": "${movementPrisoner4[DIRECTION]}", "type": "${movementPrisoner4[TYPE]}", "reason": "${movementPrisoner4[REASON]}"}
      ]
      """,
      )
  }

  @ParameterizedTest
  @CsvSource(
    "in,  0",
    "In,  0",
    "out, 1",
    "Out, 1",
    ",    1",
  )
  fun `Configured API returns filtered values`(direction: String?, numberOfResults: Int) {
    val results = webTestClient.get()
      .uri { uriBuilder: UriBuilder ->
        uriBuilder
          .path("/reports/external-movements/last-month")
          .queryParam("${FILTERS_PREFIX}direction", direction)
          .build()
      }
      .headers(setAuthorisation(roles = listOf(authorisedRole)))
      .exchange()
      .expectStatus()
      .isOk()
      .expectBodyList<Map<String, Any>>()
      .hasSize(numberOfResults)
      .returnResult()
      .responseBody

    if (direction != null) {
      results?.forEach {
        assertThat(it["direction"].toString().lowercase()).isEqualTo(direction.lowercase())
      }
    }
  }

  @Test
  fun `Configured API returns 400 for invalid selectedPage query param`() {
    requestWithQueryAndAssert400("selectedPage", 0, "/reports/external-movements/last-month")
  }

  @Test
  fun `Configured API returns 400 for invalid pageSize query param`() {
    requestWithQueryAndAssert400("pageSize", 0, "/reports/external-movements/last-month")
  }

  @Test
  fun `Configured API returns 400 for invalid (wrong type) pageSize query param`() {
    requestWithQueryAndAssert400("pageSize", "a", "/reports/external-movements/last-month")
  }

  @Test
  fun `Configured API returns 400 for invalid sortColumn query param`() {
    requestWithQueryAndAssert400("sortColumn", "nonExistentColumn", "/reports/external-movements/last-month")
  }

  @Test
  fun `Configured API returns 400 for invalid sortedAsc query param`() {
    requestWithQueryAndAssert400("sortedAsc", "abc", "/reports/external-movements/last-month")
  }

  @Test
  fun `Configured API returns 400 for non-existent filter`() {
    requestWithQueryAndAssert400("${FILTERS_PREFIX}abc", "abc", "/reports/external-movements/last-month")
  }

  @Test
  fun `Configured API count returns 400 for non-existent filter`() {
    requestWithQueryAndAssert400("${FILTERS_PREFIX}abc", "abc", "/reports/external-movements/last-month/count")
  }

  @Test
  fun `Configured API returns 400 for a report field which is not a filter`() {
    requestWithQueryAndAssert400("${FILTERS_PREFIX}name", "some name", "/reports/external-movements/last-month")
  }

  @Test
  fun `Configured API count returns 400 for a report field which is not a filter`() {
    requestWithQueryAndAssert400("${FILTERS_PREFIX}name", "some name", "/reports/external-movements/last-month/count")
  }

  @Test
  fun `Configured API returns 400 for invalid startDate query param`() {
    requestWithQueryAndAssert400("${FILTERS_PREFIX}date$RANGE_FILTER_START_SUFFIX", "abc", "/reports/external-movements/last-month")
  }

  @Test
  fun `External movements returns 400 for invalid endDate query param`() {
    requestWithQueryAndAssert400("${FILTERS_PREFIX}date$RANGE_FILTER_END_SUFFIX", "b", "/reports/external-movements/last-month")
  }

  @Test
  fun `Configured API count returns 400 for invalid startDate query param`() {
    requestWithQueryAndAssert400("filters.startDate", "a", "/reports/external-movements/last-month/count")
  }

  @Test
  fun `Configured API count returns 400 for invalid endDate query param`() {
    requestWithQueryAndAssert400("filters.endDate", "17-12-2050", "/reports/external-movements/last-month/count")
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

  object AllMovements {
    val externalMovement1 = ExternalMovementEntity(
      1,
      8894,
      LocalDateTime.of(2023, 1, 31, 0, 0, 0),
      LocalDateTime.of(2023, 1, 31, 3, 1, 0),
      "KINGSTON (HMP)",
      "PTI",
      "THORN CROSS (HMPYOI)",
      "TCI",
      "In",
      "Admission",
      "Unconvicted Remand",
    )
    val externalMovement2 = ExternalMovementEntity(
      2,
      5207,
      LocalDateTime.of(2023, 4, 25, 0, 0, 0),
      LocalDateTime.of(2023, 4, 25, 12, 19, 0),
      "Leicester Crown Court",
      "LEICCC",
      "LEICESTER (HMP)",
      "LCI",
      "In",
      "Transfer",
      "Transfer In from Other Establishment",
    )
    val externalMovement3 = ExternalMovementEntity(
      3,
      4800,
      LocalDateTime.of(2023, 4, 30, 0, 0, 0),
      LocalDateTime.of(2023, 4, 30, 13, 19, 0),
      "BEDFORD (HMP)",
      "BFI",
      "NORTH SEA CAMP (HMP)",
      "NSI",
      "In",
      "Transfer",
      "Transfer In from Other Establishment",
    )
    val externalMovement4 = ExternalMovementEntity(
      4,
      7849,
      LocalDateTime.of(2023, 5, 1, 0, 0, 0),
      LocalDateTime.of(2023, 5, 1, 15, 19, 0),
      "Lowestoft (North East Suffolk) Magistrat",
      "LWSTMC",
      "WANDSWORTH (HMP)",
      "WWI",
      "Out",
      "Transfer",
      "Transfer Out to Other Establishment",
    )
    val externalMovement5 = ExternalMovementEntity(
      5,
      6851,
      LocalDateTime.of(2023, 5, 20, 0, 0, 0),
      LocalDateTime.of(2023, 5, 20, 14, 0, 0),
      "Bolton Crown Court",
      "BOLTCC",
      "HMP HEWELL",
      "HEI",
      "In",
      "Transfer",
      "Transfer In from Other Establishment",
    )
    val allExternalMovements = listOf(
      externalMovement1,
      externalMovement2,
      externalMovement3,
      externalMovement4,
      externalMovement5,
    )
  }
  object AllPrisoners {
    val prisoner8894 = mapOf("id" to 8894, "number" to "G2504UV", "firstName" to "FirstName2", "lastName" to "LastName1", "livingUnitReference" to null)

    val prisoner5207 = mapOf("id" to 5207, "number" to "G2927UV", "firstName" to "FirstName1", "lastName" to "LastName1", "livingUnitReference" to null)

    val prisoner4800 = mapOf("id" to 4800, "number" to "G3418VR", "firstName" to "FirstName3", "lastName" to "LastName3", "livingUnitReference" to null)

    val prisoner7849 = mapOf("id" to 7849, "number" to "G3411VR", "firstName" to "FirstName4", "lastName" to "LastName5", "livingUnitReference" to 142595)

    val prisoner6851 = mapOf("id" to 6851, "number" to "G3154UG", "firstName" to "FirstName5", "lastName" to "LastName5", "livingUnitReference" to null)

    val allPrisoners = listOf(
      PrisonerEntity(8894, "G2504UV", "FirstName2", "LastName1", null),
      PrisonerEntity(5207, "G2927UV", "FirstName1", "LastName1", null),
      PrisonerEntity(4800, "G3418VR", "FirstName3", "LastName3", null),
      PrisonerEntity(7849, "G3411VR", "FirstName4", "LastName5", 142595),
      PrisonerEntity(6851, "G3154UG", "FirstName5", "LastName5", null),
    )
  }

  object AllMovementPrisoners {
    const val PRISON_NUMBER = "PRISONNUMBER"
    const val NAME = "NAME"
    const val DATE = "DATE"
    const val DIRECTION = "DIRECTION"
    const val TYPE = "TYPE"
    const val ORIGIN = "ORIGIN"
    const val ORIGIN_CODE = "ORIGIN_CODE"
    const val DESTINATION = "DESTINATION"
    const val DESTINATION_CODE = "DESTINATION_CODE"
    const val REASON = "REASON"

    val movementPrisoner1 = mapOf(PRISON_NUMBER to "G2504UV", NAME to "LastName1, F", DATE to "2023-01-31", DIRECTION to "In", TYPE to "Admission", ORIGIN to "KINGSTON (HMP)", ORIGIN_CODE to "PTI", DESTINATION to "THORN CROSS (HMPYOI)", DESTINATION_CODE to "TCI", REASON to "Unconvicted Remand")

    val movementPrisoner2 = mapOf(PRISON_NUMBER to "G2927UV", NAME to "LastName1, F", DATE to "2023-04-25", DIRECTION to "In", TYPE to "Transfer", ORIGIN to "Leicester Crown Court", ORIGIN_CODE to "LEICCC", DESTINATION to "LEICESTER (HMP)", DESTINATION_CODE to "LCI", REASON to "Transfer In from Other Establishment")

    val movementPrisoner3 = mapOf(PRISON_NUMBER to "G3418VR", NAME to "LastName3, F", DATE to "2023-04-30", DIRECTION to "In", TYPE to "Transfer", ORIGIN to "BEDFORD (HMP)", ORIGIN_CODE to "BFI", DESTINATION to "NORTH SEA CAMP (HMP)", DESTINATION_CODE to "NSI", REASON to "Transfer In from Other Establishment")

    val movementPrisoner4 = mapOf(PRISON_NUMBER to "G3411VR", NAME to "LastName5, F", DATE to "2023-05-01", DIRECTION to "Out", TYPE to "Transfer", ORIGIN to "Lowestoft (North East Suffolk) Magistrat", ORIGIN_CODE to "LWSTMC", DESTINATION to "WANDSWORTH (HMP)", DESTINATION_CODE to "WWI", REASON to "Transfer Out to Other Establishment")

    val movementPrisoner5 = mapOf(PRISON_NUMBER to "G3154UG", NAME to "LastName5, F", DATE to "2023-05-20", DIRECTION to "In", TYPE to "Transfer", ORIGIN to "Bolton Crown Court", ORIGIN_CODE to "BOLTCC", DESTINATION to "HMP HEWELL", DESTINATION_CODE to "HEI", REASON to "Transfer In from Other Establishment")
  }
}
