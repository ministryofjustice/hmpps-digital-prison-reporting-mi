package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementRequest
import software.amazon.awssdk.services.sts.StsClient
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest
import kotlin.jvm.optionals.getOrElse

@Configuration
class RedshiftDataApiConf(
  @Value("\${dpr.lib.redshiftdataapi.secretaccesskey}") private val secretAccessKey: String,
  @Value("\${dpr.lib.redshiftdataapi.accesskeyid}") private val accessKeyId: String,
  @Value("\${dpr.lib.redshiftdataapi.database}") private val database: String,
  @Value("\${dpr.lib.redshiftdataapi.clusterid}") private val clusterId: String,
  @Value("\${dpr.lib.redshiftdataapi.secretarn}") private val secretArn: String,
) {
  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  @Bean
  fun executeStatementRequestBuilder(): ExecuteStatementRequest.Builder {
    return ExecuteStatementRequest.builder()
      .clusterIdentifier("dpr-redshift-development")
      .database("datamart")
      .secretArn("arn:aws:secretsmanager:eu-west-2:771283872747:secret:dpr-redshift-secret-development-rLHcQZ")
  }

  @Bean
  fun redshiftDataClient(): RedshiftDataClient {
    val region = Region.EU_WEST_2
    val stsClient: StsClient = StsClient.builder()
      .region(region)
      .build()

    val credentials = assumeGivenRole(stsClient, "arn:aws:iam::771283872747:role/dpr-cross-account-role-demo", "dpr-cross-account-role-session")
//    stsClient.close()
    val staticCredentialsProvider = StaticCredentialsProvider.create(
      AwsSessionCredentials.create(credentials.accessKeyId(), credentials.secretAccessKey(), credentials.sessionToken()),
    )
    return RedshiftDataClient.builder()
//      .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(credentials.accessKeyId(), credentials.secretAccessKey())))
      .credentialsProvider(staticCredentialsProvider)
      //      .credentialsProvider(WebIdentityTokenFileCredentialsProvider.builder().webIdentityTokenFile(Path.of(""))
//        .build())
      .region(Region.EU_WEST_2)
      .build()
  }

  fun assumeGivenRole(stsClient: StsClient, roleArn: String?, roleSessionName: String?): AwsSessionCredentials {
    val roleRequest: AssumeRoleRequest = AssumeRoleRequest.builder()
      .roleArn(roleArn)
      .roleSessionName(roleSessionName)
      .durationSeconds(900)
      .build()
    val stsAssumeRoleCredentialsProvider = StsAssumeRoleCredentialsProvider
      .builder()
      .stsClient(stsClient)
      .refreshRequest(roleRequest)
      .asyncCredentialUpdateEnabled(true)
      .build()
    val resolveCredentials: AwsSessionCredentials = stsAssumeRoleCredentialsProvider.resolveCredentials() as AwsSessionCredentials
    log.info("Caller Identinty Account: {}", stsClient.callerIdentity.account())
    log.info("Caller Identinty Arn: {}", stsClient.callerIdentity.arn())
    log.info("Caller Identinty User Id: {}", stsClient.callerIdentity.userId())
    log.info("Credentials Duration: {}", resolveCredentials.expirationTime().getOrElse { "Unknown duration." })
//    val roleResponse: AssumeRoleResponse = stsClient.assumeRole(roleRequest)
//    val roleResponse: AssumeRoleResponse = stsClient.assumeRole(roleRequest)
//    val myCreds: Credentials = roleResponse.credentials()

    return resolveCredentials
  }
}
