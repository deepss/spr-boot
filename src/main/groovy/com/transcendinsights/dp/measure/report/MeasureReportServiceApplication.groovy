package com.transcendinsights.dp.measure.report

import com.transcendinsights.dp.measure.report.config.FhirRestServletConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.web.context.WebApplicationContext

/**
 * Entry point into the Measure Report Service
 */
@SpringBootApplication
class MeasureReportServiceApplication {

  @Autowired
  WebApplicationContext context

  static void main(String[] args) {
    SpringApplication.run MeasureReportServiceApplication, args
  }

  @Bean
  ServletRegistrationBean fhirServlet() {
    def registration = new ServletRegistrationBean(new FhirRestServletConfiguration(context), '/fhir/*')
    registration.loadOnStartup = 1
    registration.initParameters.put('ImplementationDescription', 'FHIR JPA Server')
    registration.initParameters.put('FhirVersion', 'DSTU3')
    registration
  }

}
