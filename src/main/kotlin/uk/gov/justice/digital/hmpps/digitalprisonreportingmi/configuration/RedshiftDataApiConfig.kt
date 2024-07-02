package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration.StsCredentialsProviderConfig.Companion.REGION

@Configuration
class RedshiftDataApiConfig {

  @Bean
  fun redshiftDataClient(stsAssumeRoleCredentialsProvider: StsAssumeRoleCredentialsProvider): RedshiftDataClient {
    return RedshiftDataClient.builder()
      .region(REGION)
      .credentialsProvider(stsAssumeRoleCredentialsProvider)
      .build()
  }
}
