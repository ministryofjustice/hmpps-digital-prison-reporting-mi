package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.smithy.kotlin.runtime.auth.awscredentials.CredentialsProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider

@Configuration
class DynamoDbConfig {

  @Bean
  fun dynamoDbClient(stsAssumeRoleCredentialsProvider: StsAssumeRoleCredentialsProvider): DynamoDbClient {
    return DynamoDbClient {
      region = StsCredentialsProviderConfig.REGION.toString()
      credentialsProvider = stsAssumeRoleCredentialsProvider as CredentialsProvider
    }
  }
}
