package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model
enum class ExternalMovementFilter(private val paramName: String) {
  DIRECTION("filter.direction");

  companion object {
    fun paramNameMatches(paramName: String): Boolean =
      values().any { it.paramName == paramName }

    fun getFromParamName(paramName: String): ExternalMovementFilter =
      values().find { it.paramName == paramName }!!
  }
}
