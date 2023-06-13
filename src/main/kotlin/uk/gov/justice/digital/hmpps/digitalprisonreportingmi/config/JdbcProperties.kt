package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("redshift.jdbc")
class JdbcProperties(
  url: String,
  user: String,
  password: String,
)
