package com.transcendinsights.dp.measure.report

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.client.IGenericClient
import ca.uhn.fhir.rest.client.ServerValidationModeEnum
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor
import org.hl7.fhir.dstu3.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Kurt Kremer
 * @since 2017-05-08.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles('test')
class MeasureReportServiceApplicationSpec extends Specification {

    @Shared
    IGenericClient restClient

    @Shared
    Boolean initialized

    @Shared
    FhirContext fhirContext = FhirContext.forDstu3()

    def injectDependencies(IGenericClient client) {
        this.restClient = client
    }

    @Autowired
    def mrsSetupSpec(@Value('${local.server.port}') int port, @Value('${security.user.password}') String password) {
        if (!initialized) {
          def serverBase = "http://localhost:$port/fhir"
          fhirContext.restfulClientFactory.serverValidationMode = ServerValidationModeEnum.NEVER
          fhirContext.restfulClientFactory.socketTimeout = 1200 * 1000

          def client = fhirContext.newRestfulGenericClient(serverBase)
          client.registerInterceptor(new LoggingInterceptor(true))
          client.registerInterceptor(new BasicAuthInterceptor('user', password))
          injectDependencies(client)

          initialized = true
        }
    }

    @SuppressWarnings(['UnnecessaryObjectReferences', 'UnnecessaryGetter'])
    def 'can fetch a patient by patientId within measureReport'() {
        given: 'create a measureReport with patient and measure'
        //create a measure
        Measure newMeasure = mockMeasure()
        MethodOutcome measureCreated = restClient.create().resource(newMeasure).execute()
        def meaCreatedId = measureCreated.id

        //create a patient
        Patient patient = mockPatient()
        MethodOutcome patCreated = restClient.create().resource(patient).execute()
        def patId = patCreated.id

        //create a measureReport having measure & patient (setting only the required fields)
        MeasureReport measureReport = new MeasureReport()
        measureReport.setId('MR001_PAT001')
        measureReport.setIdentifier(new Identifier().with {
          type = new CodeableConcept().addCoding(
                  new Coding('http://hl7.org/fhir/v2/0203', 'MR', 'MR number'))
          system = 'urn:oid:1.2.36.146.595.217.0.1'
          value = 'MR001_PAT001'
          it
        })
        measureReport.setStatus(MeasureReport.MeasureReportStatus.COMPLETE)
        measureReport.setType(MeasureReport.MeasureReportType.INDIVIDUAL)
        measureReport.setMeasure(new Reference(meaCreatedId))
        measureReport.setPatient(new Reference(patId))
        measureReport.setPeriod(new Period().
                setStart(new Date(2016, 1, 1)).setEnd(new Date(2016, 2, 1)))
        MethodOutcome mReportCreated = restClient.create().resource(measureReport).execute()
        def mReportId = mReportCreated.id

        when: 'patient is searched like- localhost:port/MeasureReport?patient=id'
        def searchUrl = 'MeasureReport?patient=' + patId
        Bundle searchMReport = restClient.search()
                .byUrl(searchUrl)
                .returnBundle(Bundle)
                .execute()

        then: 'the search returns measureReport for the patId'
        searchMReport.entry.get(0).getResource().resourceType.name() == 'MeasureReport'
        searchMReport.entry.get(0).getResource().id == mReportId.value
    }

    protected Measure mockMeasure() {
        Measure newMeasure = new Measure()
        newMeasure.setStatus(Enumerations.PublicationStatus.ACTIVE)
        newMeasure.setId('MEA001')
        newMeasure.addIdentifier(
                new Identifier().with {
                    type = new CodeableConcept().addCoding(
                            new Coding('http://hl7.org/fhir/v2/0203', 'MR', 'Measure number'))
                    system = 'urn:oid:1.2.36.146.595.217.0.1'
                    value = 'MEA001'
                    it
                }
        )
        newMeasure.setTitle('Influenza immunization')
        newMeasure
    }

    protected Patient mockPatient() {
        Patient patient = new Patient()
        patient.setId('PAT001')
        patient.addIdentifier(new Identifier().with {
            type = new CodeableConcept().addCoding(
                    new Coding('http://hl7.org/fhir/v2/0203', 'MR', 'Patient number'))
            system = 'urn:oid:1.2.36.146.595.217.0.1'
            value = 'PAT001'
            it
        })
        patient.addName().addGiven('New').setFamily('York')
        patient.setGender(Enumerations.AdministrativeGender.FEMALE)
        patient.setBirthDate(new Date(1880, 8, 1))
        patient
    }
}
