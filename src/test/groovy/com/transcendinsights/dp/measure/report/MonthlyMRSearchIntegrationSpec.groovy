package com.transcendinsights.dp.measure.report

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.model.primitive.IdDt
import ca.uhn.fhir.rest.client.IGenericClient
import ca.uhn.fhir.rest.client.ServerValidationModeEnum
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor
import ca.uhn.fhir.rest.method.SearchParameter
import ca.uhn.fhir.rest.method.SearchStyleEnum
import ca.uhn.fhir.rest.server.IBundleProvider
import com.transcendinsights.dp.measure.report.fhir.operations.TIMeasureReportResourceProvider
import groovy.util.logging.Slf4j
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.DateType
import org.hl7.fhir.dstu3.model.Enumerations
import org.hl7.fhir.dstu3.model.MeasureReport
import org.hl7.fhir.dstu3.model.Parameters
import org.hl7.fhir.instance.model.StringType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Shared

import javax.measure.Measure
import java.lang.reflect.Parameter
import java.text.SimpleDateFormat

/**
 * Created by sxl9349 on 6/4/17.
 */
@Slf4j
class MonthlyMRSearchIntegrationSpec extends MeasureReportIntegrationSpec{

    def 'fetch measureReports by orgId, measureId and date'() {

        given: 'create measureReports with measure & organization'

        def mea = mockMeasure('M01', 'Measure-01')
        def org = mockOrganization('ORG01', 'Organization-01')
        log.error "@@@!@@@ meaId: $mea ; orgId: $org"

        def mReport01 = mockMeasureReportWithoutPatient(mea, 'MR-001', 'MeasureReport-001',
                MeasureReport.MeasureReportStatus.COMPLETE, MeasureReport.MeasureReportType.SUMMARY,
                new SimpleDateFormat('yyyy-MM-dd').parse('2017-01-01'), new SimpleDateFormat('yyyy-MM-dd').parse('2017-01-31'),
                new SimpleDateFormat('yyyy-MM-dd').parse('2017-01-31'), org)
        def mReport02 = mockMeasureReportWithoutPatient(mea, 'MR-002', 'MeasureReport-002',
                MeasureReport.MeasureReportStatus.COMPLETE, MeasureReport.MeasureReportType.SUMMARY,
                new SimpleDateFormat('yyyy-MM-dd').parse('2017-01-01'), new SimpleDateFormat('yyyy-MM-dd').parse('2017-02-28'),
                new SimpleDateFormat('yyyy-MM-dd').parse('2017-02-28'), org)
        def mReport03 = mockMeasureReportWithoutPatient(mea, 'MR-003', 'MeasureReport-003',
                MeasureReport.MeasureReportStatus.COMPLETE, MeasureReport.MeasureReportType.SUMMARY,
                new SimpleDateFormat('yyyy-MM-dd').parse('2017-01-01'), new SimpleDateFormat('yyyy-MM-dd').parse('2017-03-31'),
                new SimpleDateFormat('yyyy-MM-dd').parse('2017-03-31'), org)
//        def mReport04 = mockMeasureReportWithoutPatient(mea, 'MR-004', 'MeasureReport-004',
//                MeasureReport.MeasureReportStatus.COMPLETE, MeasureReport.MeasureReportType.SUMMARY,
//                new Date(2017, 01, 01), new Date(2017, 04, 30),
//                new Date(2017, 04, 30), org)
//        def mReport05 = mockMeasureReportWithoutPatient(mea, 'MR-005', 'MeasureReport-005',
//                MeasureReport.MeasureReportStatus.COMPLETE, MeasureReport.MeasureReportType.SUMMARY,
//                new Date(2017, 01, 01), new Date(2017, 05, 31),
//                new Date(2017, 05, 31), org)

        when: 'MeasureReports are searched by POST operation http://localhost:port/fhir/MeasureReport/$monthlyMeasureReport'

        def searchUrlM = "Measure?identifier=Measure-01"
        Bundle searchM = restClient.search()
                .byUrl(searchUrlM)
                .returnBundle(Bundle)
                .execute()

        def searchUrlOrg = "Organization?identifier=Organization-01"
        Bundle searchOrg = restClient.search()
                .byUrl(searchUrlOrg)
                .returnBundle(Bundle)
                .execute()
        def searchUrlMR = "MeasureReport"
        Bundle searchMR = restClient.search()
                .byUrl(searchUrlMR)
                .returnBundle(Bundle)
                .execute()

        // Create a client to talk to the HeathIntersections server
//        FhirContext ctx = FhirContext.forDstu2();
//        IGenericClient client = ctx.newRestfulGenericClient("http://fhir-dev.healthintersections.com.au/open");
//        client.registerInterceptor(new LoggingInterceptor(true));true

// Create the input parameters to pass to the server
        Parameters inParams = new Parameters()
        inParams.addParameter().setName('orgId').setValue(new org.hl7.fhir.dstu3.model.StringType(org))
        inParams.addParameter().setName('measureId').setValue(new org.hl7.fhir.dstu3.model.StringType(mea))
        inParams.addParameter().setName('date').setValue(new DateType('2017-02-28'))

// Invoke $everything on "Patient/1"
        Parameters outParams = restClient
                .operation()
                .onType(MeasureReport)
                .named('$monthlyMeasureReport')
                .withParameters(inParams)
                .encodedJson()
                //.returnBundle(Bundle)
                .execute()

        //println "######$outParams"
        def searchMReport = outParams.parameterFirstRep.getResource() as Bundle

//        def searchMReport = outParams.get
//
          then: 'return measureReports'
        searchMReport.entry.get(0).getResource().resourceType.name() == 'MeasureReport'
        searchMReport.entry.get(0).getResource().id == mReport01
        searchMReport.entry.size() == 3

    }

}
