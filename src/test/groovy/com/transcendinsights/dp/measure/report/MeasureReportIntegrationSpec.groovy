package com.transcendinsights.dp.measure.report

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.client.IGenericClient
import ca.uhn.fhir.rest.client.ServerValidationModeEnum
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor
import org.hl7.fhir.dstu3.model.*
import org.hl7.fhir.dstu3.model.codesystems.SearchParamType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author dxl0190
 * @since 2017-05-16.
 * parent class to allow mock of reference objects
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles('test')
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
        mockSearchParameter('date', Enumerations.SearchParamType.DATE)
        mockSearchParameter('measure',       Enumerations.SearchParamType.REFERENCE)
        mockSearchParameter('reportingOrganization',       Enumerations.SearchParamType.REFERENCE)
    }

    protected String mockMeasure(String id) {
        Measure newMeasure = new Measure().with {
            setStatus(Enumerations.PublicationStatus.ACTIVE)
            setId(id)
        }
        MethodOutcome measureCreated = restClient.create().resource(newMeasure).withId(id).execute()
        def meaCreatedId = measureCreated.id
        meaCreatedId
    }

    protected String mockPatient(String id, String firstName, String lastName,
                                 Date bdate, Enumerations.AdministrativeGender gender) {
        Patient patient = new Patient().with {
            setId(id)
            addName().addGiven(firstName).setFamily(lastName)
            setGender(gender)
            setBirthDate(bdate)
        }
        MethodOutcome patCreated = restClient.create().resource(patient).execute()
        def patId = patCreated.id
        patId
    }

    protected String mockOrganization(String id) {
        Organization newOrg = new Organization()
        newOrg.setId(id)
        MethodOutcome orgCreated = restClient.create().resource(newOrg).execute()
        def orgCreatedId = orgCreated.id
        orgCreatedId
    }

    @SuppressWarnings(['UnnecessaryObjectReferences'])
    protected String mockMeasureReport(String meaCreatedId, String patId, String id, Date mrDate) {
        MeasureReport measureReport = new MeasureReport().with {
            setId(id)
            setType(MeasureReport.MeasureReportType.INDIVIDUAL)
            setMeasure(new Reference(meaCreatedId))
            setPatient(new Reference(patId))
            setDate(mrDate)
        }
        MethodOutcome mReportCreated = restClient.create().resource(measureReport).execute()
        def mReportId = mReportCreated.id
        mReportId
    }

    @SuppressWarnings(['UnnecessaryObjectReferences'])
    protected String mockMeasureReportWithoutPatient(String meaCreatedId, String orgId, String id, Date mrDate) {
        MeasureReport measureReport = new MeasureReport().with {
            setId(id)
            setType(MeasureReport.MeasureReportType.SUMMARY)
            setMeasure(new Reference(meaCreatedId))
            setDate(mrDate)
            setReportingOrganization(new Reference(orgId))
        }
        MethodOutcome mReportCreated = restClient.create().resource(measureReport).execute()
        def mReportId = mReportCreated.id
        mReportId
    }

    protected String mockSearchParameter(String title, Enumerations.SearchParamType type) {
        List<CodeType> base = []
        CodeType mr = new CodeType('MeasureReport')
        base.add(mr)
        SearchParameter searchParameter = new SearchParameter().with {
            setStatus(Enumerations.PublicationStatus.ACTIVE)
            setTitle("$title")
            setBase(base)
            setCode("$title")
            setType(type)
            setExpression("MeasureReport.$title")
        }
        MethodOutcome searchCreated = restClient.create().resource(searchParameter).execute()
        def searchId = searchCreated.id
        searchId
    }
}
