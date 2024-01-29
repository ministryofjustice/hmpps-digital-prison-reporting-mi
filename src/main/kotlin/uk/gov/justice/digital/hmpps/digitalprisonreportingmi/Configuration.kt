package uk.gov.justice.digital.hmpps.digitalprisonreportingmi

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
class Configuration() {

  @Bean
  @ConfigurationProperties("spring.datasource.activities")
  fun activitiesDataSourceProperties(): DataSourceProperties {
    return DataSourceProperties()
  }

  @Bean
  @ConfigurationProperties("spring.datasource.redshift")
  fun redshiftDataSourceProperties(): DataSourceProperties {
    return DataSourceProperties()
  }

  @Bean("activities")
  fun activitiesDataSource(): DataSource? {
    return activitiesDataSourceProperties()
      .initializeDataSourceBuilder()
      .build()
  }

  @Bean(name=["datamart", "redshift"])
  fun redshiftDataSource(): DataSource? {
    return redshiftDataSourceProperties()
      .initializeDataSourceBuilder()
      .build()
  }
}
