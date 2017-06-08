package com.transcendinsights.dp.measure.report

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.client.IGenericClient
import ca.uhn.fhir.rest.client.ServerValidationModeEnum
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor
import groovy.util.logging.Slf4j
import org.hl7.fhir.dstu3.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Olu Oyedipe, dxl0190
 * @since 2017-05-16.
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles('test')
@SuppressWarnings(['ParameterCount'])
class MeasureReportIntegrationSpec extends Specification {

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

    protected String mockMeasure(String id, String identifier) {
        Measure newMeasure = new Measure()
        newMeasure.setStatus(Enumerations.PublicationStatus.ACTIVE)
        newMeasure.setId(id)
        newMeasure.addIdentifier(
                new Identifier().with {
                    type = new CodeableConcept().addCoding(
                            new Coding('http://hl7.org/fhir/v2/0203', 'MR', 'Measure number'))
                    system = 'urn:oid:1.2.36.146.595.217.0.1'
                    value = identifier
                    it
                }
        )
        log.error "@!@ before create, id: ${newMeasure.id}"
        MethodOutcome measureCreated = restClient.create().resource(newMeasure).execute()
        def meaCreatedId = measureCreated.id
        log.error "@?@ after create, new id: ${meaCreatedId}"
        meaCreatedId
    }

    protected String mockPatient(String id, String identifier, String firstName, String lastName,
                                 Date bdate, Enumerations.AdministrativeGender gender) {
        Patient patient = new Patient()
        patient.setId(id)
        patient.addIdentifier(new Identifier().with {
            type = new CodeableConcept().addCoding(
                    new Coding('http://hl7.org/fhir/v2/0203', 'P', 'Patient number'))
            system = 'urn:oid:1.2.36.146.595.217.0.1'
            value = identifier
            it
        })
        patient.addName().addGiven(firstName).setFamily(lastName)
        patient.setGender(gender)
        patient.setBirthDate(bdate)
        MethodOutcome patCreated = restClient.create().resource(patient).execute()
        def patId = patCreated.id
        patId
    }

    protected String mockOrganization(String id, String identifier) {
        Organization newOrg = new Organization()
        newOrg.setId(id)
        newOrg.addIdentifier(
                new Identifier().with {
                    type = new CodeableConcept().addCoding(
                            new Coding('http://hl7.org/fhir/v2/0203', 'ORG',
                                    'ORG number'))
                    system = 'urn:oid:1.2.36.146.595.217.0.1'
                    value = identifier
                    it
                }
        )
        MethodOutcome orgCreated = restClient.create().resource(newOrg).execute()
        def orgCreatedId = orgCreated.id
        orgCreatedId
    }

    @SuppressWarnings(['UnnecessaryObjectReferences'])
    protected String mockMeasureReport(String meaCreatedId, String patId,
                                       String orgId, String id, String identifier,
                                       MeasureReport.MeasureReportStatus status,
                                       MeasureReport.MeasureReportType mType,
                                       Date start, Date end, Date mrDate) {
        //create a measureReport having measure & patient (setting only the required fields
        MeasureReport measureReport = new MeasureReport()
        measureReport.setId(id)
        measureReport.setIdentifier(new Identifier().with {
            type = new CodeableConcept().addCoding(
                    new Coding('http://hl7.org/fhir/v2/0203', 'MR', 'MR number'))
            system = 'urn:oid:1.2.36.146.595.217.0.1'
            value = identifier
            it
        })
        measureReport.setStatus(status)
        measureReport.setType(mType)
        measureReport.setMeasure(new Reference(meaCreatedId))
        measureReport.setPatient(new Reference(patId))
        measureReport.setDate(mrDate)
        measureReport.setReportingOrganization(new Reference(orgId))
        measureReport.setPeriod(new Period().setStart(start).setEnd(end))
        MethodOutcome mReportCreated = restClient.create().resource(measureReport).execute()
        def mReportId = mReportCreated.id
        mReportId
    }

    @SuppressWarnings(['UnnecessaryObjectReferences'])
    protected String mockMeasureReportWithoutPatient(String meaCreatedId, String id, String identifier,
                                                     MeasureReport.MeasureReportStatus status,
                                                     MeasureReport.MeasureReportType mrType,
                                                     Date start, Date end, Date mrDate,
                                                     String orgId) {
        //create a measureReport having measure & patient (setting only the required fields
        MeasureReport measureReport = new MeasureReport()
        measureReport.setId(id)
        measureReport.setIdentifier(new Identifier().with {
            type = new CodeableConcept().addCoding(
                    new Coding('http://hl7.org/fhir/v2/0203', 'MR', 'MR number'))
            system = 'urn:oid:1.2.36.146.595.217.0.1'
            value = identifier
            it
        })
        measureReport.setStatus(status)
        measureReport.setType(mrType)
        measureReport.setMeasure(new Reference(meaCreatedId))
        measureReport.setPeriod(new Period().setStart(start).setEnd(end))
        measureReport.setDate(mrDate)
        measureReport.setReportingOrganization(new Reference(orgId))
        MethodOutcome mReportCreated = restClient.create().resource(measureReport).execute()
        def mReportId = mReportCreated.id
        mReportId
    }
}
