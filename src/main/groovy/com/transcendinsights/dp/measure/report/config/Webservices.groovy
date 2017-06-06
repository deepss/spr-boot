package com.transcendinsights.dp.measure.report.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.context.annotation.Configuration

/**
 * Webservices configuration
 *
 * Created by sxs9440 on 4/26/17.
 */
@Configuration
@ConfigurationProperties(prefix = 'webservices')
class Webservices {

  @NestedConfigurationProperty
  EventStoreServiceConfig eventStoreService
}
