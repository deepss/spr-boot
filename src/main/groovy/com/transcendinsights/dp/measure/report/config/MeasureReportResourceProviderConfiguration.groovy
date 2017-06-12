package com.transcendinsights.dp.measure.report.config

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.jpa.dao.IFhirResourceDao
import ca.uhn.fhir.jpa.rp.dstu3.MeasureReportResourceProvider
import ca.uhn.fhir.rest.server.IResourceProvider
import com.transcendinsights.dp.measure.report.fhir.operations.TIMeasureReportResourceProvider
import org.hl7.fhir.dstu3.model.MeasureReport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.annotation.PostConstruct

/**
 * @author Olu Oyedipe
 * @author Kurt Kremer
 * @since 2017-05-08.
 */
@Configuration
class MeasureReportResourceProviderConfiguration {

  @Autowired
  @Qualifier('myResourceProvidersDstu3')
  List<IResourceProvider> myResourceProviders

  @Autowired
  @Qualifier('myMeasureReportRpDstu3')
  MeasureReportResourceProvider originalMeasureReportResourceProvider

  @Autowired
  @Qualifier('myMeasureReportDaoDstu3')
  IFhirResourceDao<MeasureReport> newMeasureReportDao

  @Autowired
  FhirContext fhirContext

  @Bean
  MeasureReportResourceProvider tiMeasureReportResourceProvider() {
    new TIMeasureReportResourceProvider().with {
      dao = this.newMeasureReportDao
      context = fhirContext
      it
    }
  }

  @PostConstruct
  void replaceMeasureReportResourceProvider() {
    myResourceProviders.remove(originalMeasureReportResourceProvider)
    myResourceProviders.add(tiMeasureReportResourceProvider())
  }
}
