package com.transcendinsights.dp.measure.report.fhir.operations.search

import ca.uhn.fhir.jpa.dao.IFhirResourceDao
import ca.uhn.fhir.rest.server.IBundleProvider
import ca.uhn.fhir.rest.server.SimpleBundleProvider
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException
import org.hl7.fhir.dstu3.model.IdType
import org.hl7.fhir.dstu3.model.MeasureReport
import org.hl7.fhir.instance.model.api.IBaseResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.text.SimpleDateFormat

/**
 * Created by dxl0190 on 5/12/17.
 */
@Service
class DateSearchOperationServiceImpl implements DateSearchOperationService {

    @Autowired
    IFhirResourceDao<MeasureReport> newMeasureReportDao

    @Override
    @SuppressWarnings(['SimpleDateFormatMissingLocale', 'EmptyCatchBlock'])
    IBundleProvider searchMonthlyMeasureReports(String givenDate) {
        def get12Dates = []
        List<IBaseResource> resources = []
        SimpleDateFormat dateFormat = new SimpleDateFormat('yyyyMMdd')
        get12Dates = list12Dates(givenDate)
        def count = 1
        for (each in get12Dates) {
            def eachId= dateFormat.format(each) + "-ORG$count-M$count"
            IdType idType = new IdType()
            idType.id = eachId
            idType.value = eachId
            MeasureReport mr
            try {
                mr = newMeasureReportDao.read(idType)

            }catch (ResourceNotFoundException r) {
//                print "resource not found $eachId"
            }
            if (mr != null) {
                resources.add(mr)
            }
            count++
        }

        new SimpleBundleProvider(resources)
    }

    @SuppressWarnings(['EmptyIfStatement'])
    def list12Dates(String knownDate) {
        List<String> twelveDates = []
        Calendar cal = Calendar.instance
        if (knownDate != null) {
            12.times {
                cal.add(Calendar.MONTH, -1)
                cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE))
                Date lastDateOfPreviousMonth = cal.time
                twelveDates.add(lastDateOfPreviousMonth)
            }
        }
        twelveDates
    }
}
