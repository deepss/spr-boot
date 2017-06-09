package com.transcendinsights.dp.measure.report.fhir.operations

import ca.uhn.fhir.jpa.rp.dstu3.MeasureReportResourceProvider
import ca.uhn.fhir.model.valueset.BundleTypeEnum
import ca.uhn.fhir.rest.annotation.Operation
import ca.uhn.fhir.rest.annotation.OperationParam
import ca.uhn.fhir.rest.server.IBundleProvider
import com.transcendinsights.dp.measure.report.fhir.operations.search.DateSearchOperationService
import org.hl7.fhir.dstu3.model.DateType
import org.hl7.fhir.dstu3.model.StringType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by dxl0190 on 5/12/17.
 * adding extension to default measureReportResourceProvider
 */
class TIMeasureReportResourceProvider extends MeasureReportResourceProvider {

    @Autowired
    DateSearchOperationService dateSearchOperationService

    /**
     * ex : http://localhost:8090/fhir/MeasureReport/$monthlyMeasureReport
     * with request body as parameters resource with orgId, measureId, date
     * @param orgId
     * @param measureId
     * @param givenDate
     * @return bundle with matched resources with YTD month-end measureReports
     */
    @SuppressWarnings(['SimpleDateFormatMissingLocale'])
    @Operation(name = '$monthlyMeasureReport', bundleType = BundleTypeEnum.SEARCHSET)
    IBundleProvider searchMonthlyMeasureReports(
            @OperationParam(name = 'orgId') StringType orgId,
            @OperationParam(name = 'measureId') StringType measureId,
            @OperationParam(name = 'date') DateType givenDate) {
        DateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd', Locale.US)
        def dateParam
        dateParam = givenDate ? dateFormat.format(givenDate.value) : dateFormat.format(new Date())
        dateSearchOperationService.searchMonthlyMeasureReports(dateParam, orgId.toString(), measureId.toString())
    }
}
