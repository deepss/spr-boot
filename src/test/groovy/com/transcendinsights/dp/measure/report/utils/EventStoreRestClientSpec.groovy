package com.transcendinsights.dp.measure.report.utils

import com.transcendinsights.dp.measure.report.config.EventStoreServiceConfig
import org.springframework.web.client.HttpClientErrorException
import ratpack.groovy.test.embed.GroovyEmbeddedApp
import ratpack.test.embed.EmbeddedApp
import spock.lang.Specification

/**
 * EventStoreRestClient Unit Tests
 *
 * Created by sxs9440 on 4/24/17.
 */

class EventStoreRestClientSpec extends Specification {

  def 'Querying event body for an event from EventStore return event body'() {

    given: 'A Mock eventStore '
    EmbeddedApp mockService = GroovyEmbeddedApp.of {
      handlers {
        get('eventBody/3') {
          response.status(200)
          response.contentType('application/json')
          render this.class.getResourceAsStream('/measure_report_bundle.json').text
        }
      }
    }
    and: 'configure EventStoreRestClient'
    EventStoreRestClient eventStoreRestClient = getEventStoreRestClientWithMockService(mockService.address.toString())

    when: 'Queried for eventBody of event Id 3'
    String eventBody = eventStoreRestClient.getEventBody('3')

    then: 'Returns event body'
    eventBody
  }

  def 'Querying for non-existing event id throws 404 Not Found exception'() {
    given: 'A Mock eventStore '
    EmbeddedApp mockService = GroovyEmbeddedApp.of {
      handlers {
        get('eventBody/3') {
          response.status(200)
          response.contentType('application/json')
          render this.class.getResourceAsStream('/measure_report_bundle.json').text
        }
      }
    }
    and: 'configure EventStoreRestClient'
    EventStoreRestClient eventStoreRestClient = getEventStoreRestClientWithMockService(mockService.address.toString())

    when: 'Queried for eventBody of non-existing event Id 2'
    eventStoreRestClient.getEventBody('2')

    then: 'throws 404 '
    thrown HttpClientErrorException
  }

  private EventStoreRestClient getEventStoreRestClientWithMockService(String url) {
    EventStoreRestClient eventStoreRestClient = new EventStoreRestClient()
    EventStoreServiceConfig eventStoreServiceConfig = new EventStoreServiceConfig()
    eventStoreServiceConfig.with {
      setUrl(url)
      setEventbody('eventBody')
    }
    eventStoreRestClient.eventStoreServiceConfig = eventStoreServiceConfig
    eventStoreRestClient
  }
}
