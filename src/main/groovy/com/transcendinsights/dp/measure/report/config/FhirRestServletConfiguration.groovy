package com.transcendinsights.dp.measure.report.config

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.jpa.dao.DaoConfig
import ca.uhn.fhir.jpa.dao.IFhirSystemDao
import ca.uhn.fhir.jpa.provider.dstu3.JpaConformanceProviderDstu3
import ca.uhn.fhir.jpa.provider.dstu3.JpaSystemProviderDstu3
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider
import ca.uhn.fhir.rest.server.ETagSupportEnum
import ca.uhn.fhir.rest.server.EncodingEnum
import ca.uhn.fhir.rest.server.IPagingProvider
import ca.uhn.fhir.rest.server.RestfulServer
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.Meta
import org.springframework.core.env.Environment
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.cors.CorsConfiguration

import javax.servlet.ServletException

/**
 * @author Olu Oyedipe
 * @author Kurt Kremer
 * @since 2017-05-08.
 */
class FhirRestServletConfiguration extends RestfulServer {

  WebApplicationContext applicationContext

  final String allResourceProvidersBeanName = 'myResourceProvidersDstu3'

  final String systemProviderBeanName = 'mySystemProviderDstu3'

  final String systemDaoBeanName = 'mySystemDaoDstu3'

  FhirRestServletConfiguration(WebApplicationContext context) {
    this.applicationContext = context
  }

  @Override
  protected void initialize() throws ServletException {
    super.initialize()

    FhirVersionEnum fhirVersion = FhirVersionEnum.DSTU3
    setFhirContext(new FhirContext(fhirVersion))

    setResourceProviders(applicationContext.getBean(allResourceProvidersBeanName, List))

    setPlainProviders(applicationContext.getBean(systemProviderBeanName, JpaSystemProviderDstu3))

    IFhirSystemDao<Bundle, Meta> systemDao = applicationContext.getBean(systemDaoBeanName, IFhirSystemDao)
    JpaConformanceProviderDstu3 conformanceProvider =
        new JpaConformanceProviderDstu3(this, systemDao, applicationContext.getBean(DaoConfig))
    conformanceProvider.setImplementationDescription('TranscendInsights MeasureReport Service')
    setServerConformanceProvider(conformanceProvider)

    setETagSupport(ETagSupportEnum.ENABLED)

    setDefaultPrettyPrint(true)
    setDefaultResponseEncoding(EncodingEnum.JSON)

    def pageSize = Integer.parseInt(applicationContext.getBean(Environment).getProperty('paging-size'))
    IPagingProvider pagingProvider = applicationContext.getBean(DatabaseBackedPagingProvider)
    pagingProvider.setDefaultPageSize(pageSize)

    CorsConfiguration config = new CorsConfiguration().with {
      addAllowedHeader('Origin')
      addAllowedHeader('Accept')
      addAllowedHeader('Prefer')
      addAllowedHeader('X-Requested-With')
      addAllowedHeader('Content-Type')
      addAllowedHeader('Access-Control-Request-Method')
      addAllowedHeader('Access-Control-Request-Headers')
      addAllowedOrigin('*')
      addExposedHeader('Location')
      addExposedHeader('Content-Location')
      setAllowedMethods(Arrays.asList('GET', 'POST', 'PUT', 'OPTIONS'))

      it
    }

    registerInterceptor(new CorsInterceptor(config))

    def interceptorBeans = applicationContext.getBeansOfType(IServerInterceptor).values()

    interceptorBeans.each {
      this.registerInterceptor(it)
    }
  }
}
