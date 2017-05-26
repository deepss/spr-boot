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

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by dxl0190 on 5/12/17.
 */
class TIMeasureReportResourceProvider extends MeasureReportResourceProvider {

    @Autowired
    DateSearchOperationService dateSearchOperationService

    @SuppressWarnings(['SimpleDateFormatMissingLocale'])
    @Operation(name = '$monthlyMeasureReport', bundleType = BundleTypeEnum.SEARCHSET)
    IBundleProvider searchMonthlyMeasureReports(
            @OperationParam(name = 'orgId') StringType orgId,
            @OperationParam(name = 'measureId') StringType measureId,
            @OperationParam(name = 'date') DateType givenDate) {
//        ex : http://localhost:8090/fhir/MeasureReport/$monthlyMeasureReport
//        add request body as parameters resource with orgId, measureId, date
        DateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd')
        def dateParam
        if (givenDate != null) {
            dateParam = dateFormat.format(givenDate.value)
        }else {
            dateParam = dateFormat.format(new Date())
        }
        dateSearchOperationService.searchMonthlyMeasureReports(dateParam, orgId.toString(), measureId.toString())
    }
}
