package com.transcendinsights.dp.measure.report.config

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.jpa.dao.IFhirResourceDaoPatient
import ca.uhn.fhir.jpa.rp.dstu3.PatientResourceProvider
import ca.uhn.fhir.rest.server.IResourceProvider
//import com.transcendinsights.identity.patient.fhir.operations.MPIPatientResourceProvider
import org.hl7.fhir.dstu3.model.Patient
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

//  @Autowired
//  @Qualifier('myMeasureReportRpDstu3')
//  PatientResourceProvider originalMeasureReportResourceProvider
//
//  @Autowired
//  @Qualifier('myMeasureReportDaoDstu3')
//  IFhirResourceDaoPatient<Patient> myMeasureReportDao
//
//  @Autowired
//  FhirContext fhirContext
//
//  @Bean
//  MeasureReportResourceProvider tiMeasureReportResourceProvider() {
//    new TIMeasureReportResourceProvider().with {
//      dao = this.myPatientDao
//      context = fhirContext
//
//      it
//    }
//  }
//
//  @PostConstruct
//  void replaceMeasureReportResourceProvider() {
//    myResourceProviders.remove(originalMeasureReportResourceProvider)
//    myResourceProviders.add(tiMeasureReportResourceProvider())
//  }
}
