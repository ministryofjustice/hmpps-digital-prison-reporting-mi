package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient

@Configuration
class RedshiftDataApiConf(
  @Value("\${dpr.lib.redshiftdataapi.secretaccesskey}") private val secretAccessKey: String,
  @Value("\${dpr.lib.redshiftdataapi.accesskeyid}") private val accessKeyId: String,
) {
  @Bean
  fun redshiftDataClient(): RedshiftDataClient {
    return RedshiftDataClient.builder()
      .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
      .region(Region.EU_WEST_2)
      .build()
  }
}
