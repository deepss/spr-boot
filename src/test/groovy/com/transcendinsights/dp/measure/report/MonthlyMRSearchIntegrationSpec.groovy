package com.transcendinsights.dp.measure.report

import org.hl7.fhir.dstu3.model.*

import java.text.SimpleDateFormat

/**
 * @author sxl9349, dxl0190
 * @since 2017-05-08.
 */
@SuppressWarnings(['UnnecessaryGetter'])
class MonthlyMRSearchIntegrationSpec extends MeasureReportIntegrationSpec {

    def 'fetch measureReports by orgId, measureId and specific date'() {

        given: ' mock objects for measure, organization, searchParameter, measureReport'

        List<String> id = ['MR-001', 'MR-002', 'MR-003', 'MR-004', 'MR-005', 'MR-006']
        List<String> date = ['2017-05-31', '2017-04-30', '2017-03-31',
                     '2017-02-28', '2017-01-31', '2016-12-31', ]
        def measureId = mockMeasure('M01')
        def orgId = mockOrganization('ORG01')
        mockMeasureReports(measureId, orgId, id, date)

        when: 'MeasureReport is searched for a given date'
        def searchMReport = performSearch(measureId, orgId, '2017-05-02')

        then: 'returns 5 YTD month-end measureReports'
        searchMReport.entry.get(0).getResource().resourceType.name() == 'MeasureReport'
        searchMReport.entry.size() == 4
    }

    def mockMeasureReports(String measureId, String orgId, List<String> id, List<String> date) {
        def dateFormat = new SimpleDateFormat('yyyy-MM-dd', Locale.US)
        for (each in (0..id.size() - 1)) {
            mockMeasureReportWithoutPatient(measureId, orgId, id[each], dateFormat.parse(date[each]))
        }
    }

    Bundle performSearch(String measureId, String orgId, String date) {
        Parameters inParams = new Parameters()
        inParams.addParameter().setName('measureId').setValue(new StringType(measureId))
        inParams.addParameter().setName('orgId').setValue(new StringType(orgId))
        inParams.addParameter().setName('date').setValue(new DateType(date))

        Parameters outParams = restClient
                .operation()
                .onType(MeasureReport)
                .named('$monthlyMeasureReport')
                .withParameters(inParams)
                .execute()

        outParams.parameterFirstRep.getResource() as Bundle
    }
}
