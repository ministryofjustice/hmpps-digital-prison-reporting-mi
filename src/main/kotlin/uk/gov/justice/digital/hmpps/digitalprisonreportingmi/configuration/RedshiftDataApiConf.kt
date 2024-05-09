package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient
import software.amazon.awssdk.services.sts.StsClient
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse
import software.amazon.awssdk.services.sts.model.Credentials

@Configuration
class RedshiftDataApiConf(
  @Value("\${dpr.lib.redshiftdataapi.secretaccesskey}") private val secretAccessKey: String,
  @Value("\${dpr.lib.redshiftdataapi.accesskeyid}") private val accessKeyId: String,
) {
  @Bean
  fun redshiftDataClient(): RedshiftDataClient {
    val region = Region.US_WEST_2
    val stsClient: StsClient = StsClient.builder()
      .region(region)
      .build()

    val credentials = assumeGivenRole(stsClient, "arn:aws:iam::771283872747:role/dpr-cross-account-role-demo", "dpr-cross-account-role-session")
    stsClient.close()
    return RedshiftDataClient.builder()
//      .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(credentials.accessKeyId(), credentials.secretAccessKey())))
      .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(credentials.accessKeyId(), credentials.secretAccessKey())))
      //      .credentialsProvider(WebIdentityTokenFileCredentialsProvider.builder().webIdentityTokenFile(Path.of(""))
//        .roleArn("")
//        .build())
      .region(Region.EU_WEST_2)
      .build()
  }

  fun assumeGivenRole(stsClient: StsClient, roleArn: String?, roleSessionName: String?): Credentials {
    val roleRequest: AssumeRoleRequest = AssumeRoleRequest.builder()
      .roleArn(roleArn)
      .roleSessionName(roleSessionName)
      .build()
    val roleResponse: AssumeRoleResponse = stsClient.assumeRole(roleRequest)
    val myCreds: Credentials = roleResponse.credentials()

//      val exTime: Instant = myCreds.expiration()
//      val tokenInfo: String = myCreds.sessionToken()

    return myCreds
  }
}
