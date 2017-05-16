package com.transcendinsights.dp.measure.report.config

import groovy.util.logging.Slf4j
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.Cloud
import org.springframework.cloud.CloudFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

import javax.sql.DataSource

/**
 * @author Olu Oyedipe
 * @author Kurt Kremer
 * @since 2017-05-08.
 */
@Slf4j
@Configuration
@Profile('cloud')
class CloudDataSourceConfiguration {
  @Bean
  Cloud cloud() {
    new CloudFactory().cloud
  }

  @Bean
  @ConfigurationProperties('spring.datasource')
  DataSource dataSource() {
    log.info('Getting the cloud data source')
    cloud().getSingletonServiceConnector(DataSource, null)
  }
}
