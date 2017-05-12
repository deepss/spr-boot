package com.transcendinsights.dp.measure.report.config

import ca.uhn.fhir.jpa.config.BaseJavaConfigDstu3
import ca.uhn.fhir.jpa.dao.DaoConfig
import ca.uhn.fhir.jpa.util.SubscriptionsRequireManualActivationInterceptorDstu3
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor
import org.apache.commons.lang3.time.DateUtils
import org.hibernate.jpa.HibernatePersistenceProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.annotation.EnableTransactionManagement

import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * @author Olu Oyedipe
 * @author Kurt Kremer
 * @since 2017-05-08.
 */
@Configuration
@EnableTransactionManagement
class FhirServerConfigDstu3 extends BaseJavaConfigDstu3 {

  static final String TRUE = 'true'
  static final String FALSE = 'false'

  @Autowired
  DataSource dataSource

  @Bean
  DaoConfig daoConfig() {
    new DaoConfig().with {
      setSubscriptionEnabled(true)
      setSubscriptionPollDelay(5000)
      setSubscriptionPurgeInactiveAfterMillis(DateUtils.MILLIS_PER_HOUR)
      setAllowExternalReferences(true)
      it
    }
  }

  @Bean
  LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    new LocalContainerEntityManagerFactoryBean().with {
      setPersistenceUnitName('HAPI_PU')
      setDataSource(this.dataSource)
      setPackagesToScan('ca.uhn.fhir.jpa.entity')
      setPersistenceProvider(new HibernatePersistenceProvider())
      setJpaProperties(jpaProperties())

      afterPropertiesSet()
      it
    }
  }

  Properties jpaProperties() {
    new Properties().with {
      put('hibernate.dialect', 'org.hibernate.dialect.MySQLDialect')
      put('hibernate.format_sql', TRUE)
      put('hibernate.show_sql', FALSE)
      put('hibernate.hbm2ddl.auto', 'update')
      put('hibernate.jdbc.batch_size', '20')
      put('hibernate.cache.use_query_cache', FALSE)
      put('hibernate.cache.use_second_level_cache', FALSE)
      put('hibernate.cache.use_structured_entries', FALSE)
      put('hibernate.cache.use_minimal_puts', FALSE)
      put('hibernate.search.default.directory_provider', 'filesystem')
      put('hibernate.search.default.indexBase', 'target/lucenefiles')
      put('hibernate.search.lucene_version', 'LUCENE_CURRENT')

      it
    }
  }

  @Bean
  IServerInterceptor responseHighlighterInterceptor() {
    new ResponseHighlighterInterceptor()
  }

  @Bean
  IServerInterceptor subscriptionSecurityInterceptor() {
    new SubscriptionsRequireManualActivationInterceptorDstu3()
  }

  @Bean
  JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    new JpaTransactionManager().with {
      setEntityManagerFactory(entityManagerFactory)

      it
    }
  }
}
