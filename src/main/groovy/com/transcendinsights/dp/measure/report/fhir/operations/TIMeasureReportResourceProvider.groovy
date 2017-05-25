package com.transcendinsights.dp.measure.report.fhir.operations

import ca.uhn.fhir.jpa.rp.dstu3.MeasureReportResourceProvider
import ca.uhn.fhir.model.valueset.BundleTypeEnum
import ca.uhn.fhir.rest.annotation.Operation
import ca.uhn.fhir.rest.annotation.OperationParam
import ca.uhn.fhir.rest.annotation.OptionalParam
import ca.uhn.fhir.rest.server.IBundleProvider
import com.transcendinsights.dp.measure.report.fhir.operations.search.DateSearchOperationService
import org.hl7.fhir.dstu3.model.DateType
import org.springframework.beans.factory.annotation.Autowired

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by dxl0190 on 5/12/17.
 */
class TIMeasureReportResourceProvider extends MeasureReportResourceProvider {

    @Autowired
    DateSearchOperationService dateSearchOperationService

    @Operation(name = '$searchByDate', bundleType = BundleTypeEnum.SEARCHSET)
    IBundleProvider searchMeasureReportsByDate(@OperationParam(name='searchByDate',
            type = DateType) DateType theDate ) {
        dateSearchOperationService.searchByDate(theDate)
    }

    @SuppressWarnings(['SimpleDateFormatMissingLocale'])
    @Operation(name = '$monthlyMeasureReport', bundleType = BundleTypeEnum.SEARCHSET)
    IBundleProvider searchMonthlyMeasureReports(@OptionalParam(name='date') DateType givenDate ) {
//        ex : http://localhost:8090/fhir/MeasureReport/$monthlyMeasureReport
        DateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd')
        if (givenDate != null) {
            dateSearchOperationService.searchMonthlyMeasureReports(dateFormat.parse(givenDate.value))
        }else {
            dateSearchOperationService.searchMonthlyMeasureReports(dateFormat.format(new Date()))
        }
    }
}
