package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.athena.AthenaClient
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration.StsCredentialsProviderConfig.Companion.REGION

@Configuration
class AthenaConfig {

  @Bean
  fun athenaClient(stsAssumeRoleCredentialsProvider: StsAssumeRoleCredentialsProvider): AthenaClient {
    return AthenaClient.builder()
      .region(REGION)
      .credentialsProvider(stsAssumeRoleCredentialsProvider)
      .build()
  }
}
