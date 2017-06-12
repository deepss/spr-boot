package com.transcendinsights.dp.measure.report

import org.hl7.fhir.dstu3.model.*
import spock.lang.Unroll

import java.text.SimpleDateFormat

/**
 * @author sxl9349, dxl0190
 * @since 2017-06-04.
 */

@SuppressWarnings(['UnnecessaryGetter'])
class MonthlyMRSearchIntegrationSpec extends MeasureReportIntegrationSpec {
    @Unroll
    def 'fetch measureReports by orgId, measureId or date'() {

        given: 'mock objects for measure, organization, searchParameter, measureReport'

        List<String> id = ['MR-001', 'MR-002', 'MR-003', 'MR-004', 'MR-005', 'MR-006']
        List<String> date = ['2017-05-31', '2017-04-30', '2017-03-31', '2017-02-28', '2017-01-31', '2016-12-31']
        def measureId = mockMeasure('M01')
        def orgId = mockOrganization('ORG01')
        mockMeasureReports(measureId, orgId, id, date)

        when: 'MeasureReport is searched - POST http://localhost:port/fhir/MeasureReport/$monthlyMeasureReport'
        def searchMReport = performSearch(measureId, orgId, givenDate)

        then: 'return YTD month-end measureReports'
        searchMReport.entry.size() == expectedSize
        searchMReport.entry.get(0).getResource().resourceType.name() == 'MeasureReport'

        where:
        givenDate    | expectedSize
        null         | 5
        '2017-05-04' | 4
        '2017-03-20' | 2
        '2017-01-31' | 1
    }

    def mockMeasureReports(String measureId, String orgId, List<String> id, List<String> date) {
        def dateFormat = new SimpleDateFormat('yyyy-MM-dd', Locale.US)
        for (each in (0..id.size() - 1)) {
            mockMeasureReportWithoutPatient(measureId, orgId, id[each], dateFormat.parse(date[each]))
        }
    }

    Bundle performSearch(String measureId, String orgId, String date = null) {
        Parameters inParams = new Parameters()
        inParams.addParameter().setName('measureId').setValue(new StringType(measureId))
        inParams.addParameter().setName('orgId').setValue(new StringType(orgId))
        if (date) { inParams.addParameter().setName('date').setValue(new DateType(date)) }

        Parameters outParams = restClient
                .operation()
                .onType(MeasureReport)
                .named('$monthlyMeasureReport')
                .withParameters(inParams)
                .execute()

        outParams.parameterFirstRep.getResource() as Bundle
    }
}
