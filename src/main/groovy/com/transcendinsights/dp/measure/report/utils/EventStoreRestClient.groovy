package com.transcendinsights.dp.measure.report.utils

import com.transcendinsights.dp.measure.report.config.EventStoreServiceConfig
import com.transcendinsights.dp.measure.report.config.Webservices
import groovy.util.logging.Log4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

import javax.annotation.PostConstruct

/**
 * Event Store  Client
 *
 * Created by sxs9440 on 4/13/17.
 */
@Log4j
@Component
class EventStoreRestClient {

  @Autowired
  Webservices webservices

  EventStoreServiceConfig eventStoreServiceConfig

  @PostConstruct
  void init() {
    eventStoreServiceConfig = webservices.eventStoreService
  }

  String getEventBody(String eventId) {
    String path = "${eventStoreServiceConfig.eventbody}/${eventId}"
    String eventBody = new RestTemplate().getForObject(eventStoreServiceConfig.url + path, String, [:])
    eventBody
  }
}
