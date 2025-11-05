package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.configuration

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.config.JpaRepositoryConfiguration

@EnableJpaRepositories(
  basePackages = ["uk.gov.justice.digital.hmpps.digitalprisonreportingmi"]
)
@EntityScan(
  basePackages = ["uk.gov.justice.digital.hmpps.digitalprisonreportingmi"]
)
@TestConfiguration
class JpaRepositoryConfigurationApp