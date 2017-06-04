package com.transcendinsights.dp.measure.report

import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.Enumerations
import org.hl7.fhir.dstu3.model.MeasureReport

/**
 * @author Kurt Kremer, dxl0190
 * @since 2017-05-08.
 */
class MRPatientSearchIntegrationSpec extends MeasureReportIntegrationSpec {

    @SuppressWarnings(['UnnecessaryGetter'])
    def 'can fetch a patient by patientId within measureReport'() {

        given: 'create a measureReport with patient and measure'
        def meaCreatedId = mockMeasure('MEA001', ' M01')
        def patId = mockPatient('PAT001', 'P01', 'New', 'York',
                new Date(1980, 8, 1),
                Enumerations.AdministrativeGender.FEMALE)
        def orgId = mockOrganization('ORG001', 'ORG01')
        def mReportId = mockMeasureReport(meaCreatedId, patId, orgId, 'MR001_PAT001', 'MR01',
                MeasureReport.MeasureReportStatus.COMPLETE, MeasureReport.MeasureReportType.INDIVIDUAL,
                new Date(2016, 1, 1), new Date(2016, 2, 1),
                new Date(2017, 05, 12))

        when: 'patient is searched like- localhost:port/MeasureReport?patient=id'
        def searchUrl = "MeasureReport?patient=$patId"
        Bundle searchMReport = restClient.search()
                .byUrl(searchUrl)
                .returnBundle(Bundle)
                .execute()

        then: 'the search returns measureReport for the patId'
        searchMReport.entry.get(0).getResource().resourceType.name() == 'MeasureReport'
        searchMReport.entry.get(0).getResource().id == mReportId
    }

}
