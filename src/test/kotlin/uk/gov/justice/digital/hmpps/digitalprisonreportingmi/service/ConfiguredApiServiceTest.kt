package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import jakarta.validation.ValidationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepository
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.StubbedProductDefinitionRepository

class ConfiguredApiServiceTest {
  private val stubbedProductDefinitionRepository: StubbedProductDefinitionRepository = StubbedProductDefinitionRepository()
  private val configuredApiRepository: ConfiguredApiRepository = mock<ConfiguredApiRepository>()
  private val configuredApiService = ConfiguredApiService(stubbedProductDefinitionRepository, configuredApiRepository)
  @Test
  fun `should call the repositories with the corresponding arguments and get a list of rows`() {
    val reportId = "external-movements"
    val reportVariantId = "last-month"
    val filters = mapOf("direction" to "in", "date.start" to "2023-04-25", "date.end" to "2023-09-10")
    val filtersExcludingRange = mapOf("direction" to "in")
    val rangeFilters = mapOf("date.start" to "2023-04-25", "date.end" to "2023-09-10")
    val dataSet = stubbedProductDefinitionRepository.getProductDefinitions().first().dataSet.first()
    val expectedResult = listOf(
      mapOf("prisonNumber" to "1"),
      mapOf("name" to "FirstName"),
      mapOf("date" to "2023-05-20"),
      mapOf("origin" to "OriginLocation"),
      mapOf("destination" to "DestinationLocation"),
      mapOf("direction" to "in"),
      mapOf("type" to "trn"),
      mapOf("reason" to "normal transfer"),
      )

    whenever(configuredApiRepository.executeQuery(dataSet.query, rangeFilters, filtersExcludingRange)).thenReturn(expectedResult)

    val actual = configuredApiService.validateAndFetchData(reportId, dataSet.id, reportVariantId, filters)

    verify(configuredApiRepository, times(1)).executeQuery(dataSet.query, rangeFilters, filtersExcludingRange)
    assertEquals(expectedResult, actual)
  }

  @Test
  fun `should throw an exception for invalid report variant`() {
    val reportId = "external-movements"
    val reportVariantId = "non existent variant"
    val filters = mapOf("direction" to "in", "date.start" to "2023-04-25", "date.end" to "2023-09-10")

    val e = org.junit.jupiter.api.assertThrows<ValidationException> { configuredApiService.validateAndFetchData(reportId, "dataSetId", reportVariantId, filters) }
    assertEquals(ConfiguredApiService.INVALID_REPORT_ID_MESSAGE, e.message)
    verify(configuredApiRepository, times(0)).executeQuery(any(), any(), any())
  }

  @Test
  fun `should throw an exception for invalid filter`() {
    val reportId = "external-movements"
    val reportVariantId = "last-month"
    val filters = mapOf("non existent filter" to "blah")

    val e = org.junit.jupiter.api.assertThrows<ValidationException> { configuredApiService.validateAndFetchData(reportId, "dataSetId", reportVariantId, filters) }
    assertEquals(ConfiguredApiService.INVALID_FILTERS_MESSAGE, e.message)
    verify(configuredApiRepository, times(0)).executeQuery(any(), any(), any())
  }
}