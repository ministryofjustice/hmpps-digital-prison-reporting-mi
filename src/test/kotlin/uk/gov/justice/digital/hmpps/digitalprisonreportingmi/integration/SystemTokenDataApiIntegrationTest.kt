package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.integration

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

class SystemTokenDataApiIntegrationTest : DataApiIntegrationTest() {

  companion object {
    @JvmStatic
    @DynamicPropertySource
    fun registerProperties(registry: DynamicPropertyRegistry) {
      registry.add("dpr.lib.definition.locations") { "dpd001-court-hospital-movements.json,external-movements.json" }
      // override system token enabled
      registry.add("dpr.lib.system.token.enabled") { "true" }
    }
  }
}
