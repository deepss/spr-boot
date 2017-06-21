package com.transcendinsights.dp.measure.report.config

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

import javax.sql.DataSource

@Configuration
@Profile('local')
class LocalDataSourceConfiguration {
  @Bean
  @ConfigurationProperties('spring.datasource.tomcat')
  DataSource dataSource() {
    DataSourceBuilder.create().build()
  }
}
