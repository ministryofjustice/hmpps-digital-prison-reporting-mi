package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementRequest
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider

@Configuration
class RedshiftDataApiConf(
  @Value("\${dpr.lib.redshiftdataapi.database}") private val database: String,
  @Value("\${dpr.lib.redshiftdataapi.clusterid}") private val clusterId: String,
  @Value("\${dpr.lib.redshiftdataapi.secretarn}") private val secretArn: String,
  @Value("\${dpr.lib.redshiftdataapi.tokenrefreshdurationsec}") private val tokenRefreshDurationSec: Int,
  @Value("\${dpr.lib.redshiftdataapi.rolearn}") private val roleArn: String,
  @Value("\${dpr.lib.redshiftdataapi.rolesessionname}") private val roleSessionName: String,
  val stsAssumeRoleCredentialsProvider: StsAssumeRoleCredentialsProvider,
) {

  @Bean
  fun executeStatementRequestBuilder(): ExecuteStatementRequest.Builder {
    return ExecuteStatementRequest.builder()
      .clusterIdentifier(clusterId)
      .database(database)
      .secretArn(secretArn)
  }

  @Bean
  fun redshiftDataClient(): RedshiftDataClient {
    val region = Region.EU_WEST_2
    return RedshiftDataClient.builder()
      .credentialsProvider(stsAssumeRoleCredentialsProvider)
      .region(region)
      .build()
  }
}
