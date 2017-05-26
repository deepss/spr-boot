package com.transcendinsights.dp.measure.report.fhir.operations.search

import ca.uhn.fhir.jpa.dao.IFhirResourceDao
import ca.uhn.fhir.rest.server.IBundleProvider
import ca.uhn.fhir.rest.server.SimpleBundleProvider
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException
import groovy.util.logging.Slf4j
import org.hl7.fhir.dstu3.model.IdType
import org.hl7.fhir.dstu3.model.MeasureReport
import org.hl7.fhir.instance.model.api.IBaseResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.text.SimpleDateFormat

/**
 * Created by dxl0190 on 5/12/17.
 */
@Slf4j
@Service
@SuppressWarnings(['SimpleDateFormatMissingLocale'])
class DateSearchOperationServiceImpl implements DateSearchOperationService {

    @Autowired
    IFhirResourceDao<MeasureReport> newMeasureReportDao

    @Override
    IBundleProvider searchMonthlyMeasureReports(String givenDate, String orgId, String measureId) {
        def dateList = []
        List<IBaseResource> resources = []
        SimpleDateFormat dateFormat = new SimpleDateFormat('yyyyMMdd')
        dateList = listDates(givenDate)
        for (each in dateList) {
            def eachId= dateFormat.format(each) + "-$orgId-$measureId"
            MeasureReport mr = searchByMRId(eachId)
            if (mr != null) {
                resources.add(mr)
            }
        }
        new SimpleBundleProvider(resources)
    }

    def listDates(String dateStr) {
        List<String> twelveDates = []
        SimpleDateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd')
        Calendar cal = Calendar.instance
        Date knownDt = dateFormat.parse(dateStr)
        cal.setTime(knownDt)
        if (dateStr != null) {
//            year-to-date
            for (every in 1..12) {
                cal.add(Calendar.MONTH, -1)
                cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE))
                Date lastDateOfPreviousMonth = cal.time
                twelveDates.add(lastDateOfPreviousMonth)
                int prevMonth = cal.get(Calendar.MONTH)
                if (prevMonth == 0) {        //if Jan, exit
                    break
                }
            }
        }
        twelveDates
    }

    MeasureReport searchByMRId(String mrId) {
        IdType idType = new IdType()
        idType.id = mrId
        idType.value = mrId
        MeasureReport foundMR
        try {
            foundMR = newMeasureReportDao.read(idType)
        }catch (ResourceNotFoundException r) {
            log.debug("resource not found $mrId")
        }
        foundMR
    }
}
