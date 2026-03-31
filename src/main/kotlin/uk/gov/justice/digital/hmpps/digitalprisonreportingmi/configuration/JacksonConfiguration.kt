package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.module.SimpleModule
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.DateDeserializer
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.DateSerializer

@Configuration
class JacksonConfiguration {

  @Bean
  fun customDateModule(): SimpleModule = SimpleModule().apply {
    addSerializer(java.sql.Date::class.java, DateSerializer())
    addDeserializer(java.sql.Date::class.java, DateDeserializer())
  }
}
