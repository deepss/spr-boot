package com.transcendinsights.dp.measure.report.fhir.operations.search

import ca.uhn.fhir.jpa.dao.IFhirResourceDao
import ca.uhn.fhir.jpa.dao.SearchParameterMap
import ca.uhn.fhir.rest.param.DateParam
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.server.IBundleProvider
import ca.uhn.fhir.rest.server.SimpleBundleProvider
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException
import groovy.util.logging.Slf4j
import org.hl7.fhir.dstu3.model.MeasureReport
import org.hl7.fhir.instance.model.api.IBaseResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.text.SimpleDateFormat

/**
 * Created by dxl0190 on 5/12/17
 * Implementation of extended operation called in TIMeasureReportResourceProvider
 */
@Slf4j
@Service
@SuppressWarnings(['DuplicateStringLiteral'])
class DateSearchOperationServiceImpl implements DateSearchOperationService {

    @Autowired
    IFhirResourceDao<MeasureReport> newMeasureReportDao

    @Override
    IBundleProvider searchMonthlyMeasureReports(String givenDate, String orgId, String measureId) {
        def dateList = []
        List<IBaseResource> resources = []
        SimpleDateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd', Locale.US)
        dateList = listDates(givenDate)
        for (each in dateList) {
            def mr = searchByMR(dateFormat.format(each), orgId, measureId)
            if (mr != null) {
                resources.addAll(mr)
            }
        }
        new SimpleBundleProvider(resources)
    }

    /**
     * method to calculate month-end date back to January
     * @param dateStr
     * @return list of month-end dates
     */
    def listDates(String dateStr) {
        List<String> twelveDates = []
        SimpleDateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd', Locale.US)
        Calendar cal = Calendar.instance
        if (dateStr != null) {
            Date knownDt = dateFormat.parse(dateStr)
            cal.setTime(knownDt)
            def givenDateLastDay = new SimpleDateFormat('yyyy-MM-', Locale.US).format(cal.time) +
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

    def searchByMR(String mrDate, String orgId, String measureId) {
        def foundMR
        def mrResources
        SearchParameterMap searchMap = new SearchParameterMap()
        searchMap.add('reportingOrganization', new ReferenceParam(orgId))
        searchMap.add('measure', new ReferenceParam(measureId))
        searchMap.add('date', new DateParam(mrDate))
        try {
            foundMR = newMeasureReportDao.search(searchMap)
            if (foundMR.size() > 0) {
                mrResources = foundMR.getResources(0, foundMR.size()) as List<IBaseResource>
            }
        }catch (ResourceNotFoundException r) {
            log.debug("resource not found for $mrDate, $orgId, $measureId")
        }
        mrResources
    }
}
