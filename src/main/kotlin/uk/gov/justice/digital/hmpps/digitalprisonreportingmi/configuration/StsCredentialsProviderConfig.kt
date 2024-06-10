package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sts.StsClient
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest

@Configuration
class StsCredentialsProviderConfig(
  @Value("\${dpr.lib.redshiftdataapi.tokenrefreshdurationsec}") private val tokenRefreshDurationSec: Int,
  @Value("\${dpr.lib.redshiftdataapi.rolearn}") private val roleArn: String,
  @Value("\${dpr.lib.redshiftdataapi.rolesessionname}") private val roleSessionName: String,
) {
  companion object {
    val REGION: Region = Region.EU_WEST_2
  }

  @Bean
  fun stsAssumeRoleCredentialsProvider(): StsAssumeRoleCredentialsProvider {
    val stsClient: StsClient = StsClient.builder()
      .region(REGION)
      .build()
    val roleRequest: AssumeRoleRequest = AssumeRoleRequest.builder()
      .roleArn(roleArn)
      .roleSessionName(roleSessionName)
      .durationSeconds(tokenRefreshDurationSec)
      .build()
    return StsAssumeRoleCredentialsProvider
      .builder()
      .stsClient(stsClient)
      .refreshRequest(roleRequest)
      .asyncCredentialUpdateEnabled(true)
      .build()
  }
}
