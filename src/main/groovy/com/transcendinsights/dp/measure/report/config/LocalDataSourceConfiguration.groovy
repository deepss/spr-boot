package com.transcendinsights.dp.measure.report.config

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

import javax.sql.DataSource

/**
 * @author Olu Oyedipe
 * @author Kurt Kremer
 * @since 2017-05-08.
 */
@Configuration
@Profile('local')
class LocalDataSourceConfiguration {
  @Bean
  @ConfigurationProperties('spring.datasource')
  DataSource dataSource() {
    DataSourceBuilder.create().build()
  }
}
