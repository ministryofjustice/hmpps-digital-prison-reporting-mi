package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient

@Configuration
class RedshiftDataApiConf {
  @Bean
  fun redshiftDataClient(): RedshiftDataClient {
    return RedshiftDataClient.builder()
      .region(Region.EU_WEST_2)
      .credentialsProvider(DefaultCredentialsProvider.create())
      .build()
  }
}
