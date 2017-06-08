package com.transcendinsights.dp.measure.report.fhir.operations.search

import ca.uhn.fhir.jpa.dao.IFhirResourceDao
import ca.uhn.fhir.jpa.dao.SearchParameterMap
import ca.uhn.fhir.rest.param.DateParam
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.StringParam
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
@SuppressWarnings(['SimpleDateFormatMissingLocale', 'DuplicateStringLiteral'])
class DateSearchOperationServiceImpl implements DateSearchOperationService {

    @Autowired
    IFhirResourceDao<MeasureReport> newMeasureReportDao

    /*@Override
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
    }*/

    @Override
    IBundleProvider searchMonthlyMeasureReports(String givenDate, String orgId, String measureId) {
        def dateList = []
        List<IBaseResource> resources = []
        SimpleDateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd')
        dateList = listDates(givenDate)
        for (each in dateList) {
//            def eachId= dateFormat.format(each) + "-$orgId-$measureId"
            MeasureReport mr = searchByMR(dateFormat.format(each), orgId, measureId)
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
        if (dateStr != null) {
            Date knownDt = dateFormat.parse(dateStr)
            cal.setTime(knownDt)
            def givenDateLastDay = new SimpleDateFormat('yyyy-MM-').format(cal.time) +
                    cal.getActualMaximum(Calendar.DATE)
            if (dateStr == givenDateLastDay) {
                twelveDates.add(cal.time)
            }
//            year-to-date
            if (cal.get(Calendar.MONTH) != 0) {      //check if the month is already Jan
                (1..12).find {
                    cal.add(Calendar.MONTH, -1)
                    int prevMonth = cal.get(Calendar.MONTH)
                    cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE))
                    Date lastDateOfPreviousMonth = cal.time
                    twelveDates.add(lastDateOfPreviousMonth)
                    prevMonth == 0
                }
            }
        }
        twelveDates
    }

    MeasureReport searchByMR(String mrDate, String orgId, String measureId) {
        //log.error "Search params are: mrDate:, orgId: ${orgId}, measureId: ${measureId}"
        MeasureReport foundMR
        SearchParameterMap searchMap = new SearchParameterMap()
        searchMap.add('reportingOrganization', new ReferenceParam(orgId))
        searchMap.add('measure', new ReferenceParam(measureId))
        searchMap.add('date', new DateParam(mrDate))
        //log.error "!!!SearchMap are:  orgId: ${searchMap.get('reportingOrganization')}, measureId: ${searchMap.get('measure')}"
        IBundleProvider ibundleP = newMeasureReportDao.search(searchMap)
        int size = ibundleP.size()
        //log.error "size: ${size}"
        List<IBaseResource> resources = ibundleP.getResources(0, size)
        if (size != 0) {
            foundMR = resources?.get(0)
        }
        foundMR
    }

    MeasureReport searchByMRId(String mrId) {
        IdType idType = new IdType()
        idType.id = mrId
        idType.value = mrId
        MeasureReport foundMR
        try {
            foundMR = newMeasureReportDao.read(idType)
            //foundMR = newMeasureReportDao.search(orgid, measureId, date)
        }catch (ResourceNotFoundException r) {
            log.debug("resource not found $mrId")
        }
        foundMR
    }
}
