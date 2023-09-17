package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.service

import jakarta.validation.ValidationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ConfiguredApiRepositoryCustom
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.StubbedProductDefinitionRepository

class ConfiguredApiServiceTest {
  private val stubbedProductDefinitionRepository: StubbedProductDefinitionRepository = StubbedProductDefinitionRepository()
  private val configuredApiRepository: ConfiguredApiRepositoryCustom = mock<ConfiguredApiRepositoryCustom>()
  private val configuredApiService = ConfiguredApiService(stubbedProductDefinitionRepository, configuredApiRepository)
  @Test
  fun `should call the repositories with the corresponding arguments and get a list of rows`() {
    val reportId = "external-movements"
    val reportVariantId = "last-month"
    val filters = mapOf("direction" to "in", "date.start" to "2023-04-25", "date.end" to "2023-09-10")
    val filtersExcludingRange = mapOf("direction" to "in")
    val rangeFilters = mapOf("date.start" to "2023-04-25", "date.end" to "2023-09-10")
    val selectedPage = 1L
    val pageSize = 10L
    val sortColumn = "date"
    val sortedAsc = true
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

    whenever(configuredApiRepository.executeQuery(dataSet.query, rangeFilters, filtersExcludingRange, selectedPage, pageSize, sortColumn, sortedAsc)).thenReturn(expectedResult)

    val actual = configuredApiService.validateAndFetchData(reportId, dataSet.id, reportVariantId, filters, selectedPage, pageSize, sortColumn, sortedAsc)

    verify(configuredApiRepository, times(1)).executeQuery(dataSet.query, rangeFilters, filtersExcludingRange, selectedPage, pageSize, sortColumn, sortedAsc)
    assertEquals(expectedResult, actual)
  }

  @Test
  fun `the service calls the repository without filters if no filters are provided`() {
    val reportId = "external-movements"
    val reportVariantId = "last-month"
    val dataSet = stubbedProductDefinitionRepository.getProductDefinitions().first().dataSet.first()
    val expectedResult = listOf(
      mapOf("prisonNumber" to "1")
    )
    val selectedPage = 1L
    val pageSize = 10L
    val sortColumn = "date"
    val sortedAsc = true

    whenever(configuredApiRepository.executeQuery(dataSet.query, emptyMap(), emptyMap(), selectedPage, pageSize, sortColumn, sortedAsc)).thenReturn(expectedResult)

    val actual = configuredApiService.validateAndFetchData(reportId, dataSet.id, reportVariantId, emptyMap(), selectedPage, pageSize, sortColumn, sortedAsc)

    verify(configuredApiRepository, times(1)).executeQuery(dataSet.query, emptyMap(), emptyMap(), 1, 10, "date", true)
    assertEquals(expectedResult, actual)
  }

  @Test
  fun `should throw an exception for invalid report variant`() {
    val reportId = "external-movements"
    val reportVariantId = "non existent variant"
    val filters = mapOf("direction" to "in", "date.start" to "2023-04-25", "date.end" to "2023-09-10")
    val selectedPage = 1L
    val pageSize = 10L
    val sortColumn = "date"
    val sortedAsc = true

    val e = org.junit.jupiter.api.assertThrows<ValidationException> {
      configuredApiService.validateAndFetchData(reportId, "dataSetId", reportVariantId, filters, selectedPage, pageSize, sortColumn, sortedAsc)
    }
    assertEquals(ConfiguredApiService.INVALID_REPORT_ID_MESSAGE, e.message)
    verify(configuredApiRepository, times(0)).executeQuery(any(), any(), any(), any(), any(), any(), any())
  }

  @Test
  fun `should throw an exception for invalid filter`() {
    val reportId = "external-movements"
    val reportVariantId = "last-month"
    val filters = mapOf("non existent filter" to "blah")
    val selectedPage = 1L
    val pageSize = 10L
    val sortColumn = "date"
    val sortedAsc = true

    val e = org.junit.jupiter.api.assertThrows<ValidationException> {
      configuredApiService.validateAndFetchData(reportId, "dataSetId", reportVariantId, filters, selectedPage, pageSize, sortColumn, sortedAsc)
    }
    assertEquals(ConfiguredApiService.INVALID_FILTERS_MESSAGE, e.message)
    verify(configuredApiRepository, times(0)).executeQuery(any(), any(), any(), any(), any(), any(), any())
  }

  @Test
  fun `should call the configuredApiRepository with the default sort column if none is provided`() {
    val reportId = "external-movements"
    val reportVariantId = "last-month"
    val filters = mapOf("direction" to "in", "date.start" to "2023-04-25", "date.end" to "2023-09-10")
    val filtersExcludingRange = mapOf("direction" to "in")
    val rangeFilters = mapOf("date.start" to "2023-04-25", "date.end" to "2023-09-10")
    val selectedPage = 1L
    val pageSize = 10L
    val sortColumn = "date"
    val sortedAsc = true
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

    whenever(configuredApiRepository.executeQuery(dataSet.query, rangeFilters, filtersExcludingRange, selectedPage, pageSize, sortColumn, sortedAsc)).thenReturn(expectedResult)

    val actual = configuredApiService.validateAndFetchData(reportId, dataSet.id, reportVariantId, filters, selectedPage, pageSize, null, sortedAsc)

    verify(configuredApiRepository, times(1)).executeQuery(dataSet.query, rangeFilters, filtersExcludingRange, selectedPage, pageSize, sortColumn, sortedAsc)
    assertEquals(expectedResult, actual)
  }
}