package com.transcendinsights.dp.measure.report.fhir.operations.search

import ca.uhn.fhir.rest.server.IBundleProvider

/**
 * Created by dxl0190 on 5/12/17.
 * interface referred from TIMeasureReportResourceProvider
 */
interface DateSearchOperationService {

    IBundleProvider searchMonthlyMeasureReports(String givenDate, String orgId, String measureId)
}
