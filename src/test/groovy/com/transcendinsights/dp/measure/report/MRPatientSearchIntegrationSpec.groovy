package com.transcendinsights.dp.measure.report

import org.hl7.fhir.dstu3.model.Bundle

/**
 * @author Kurt Kremer, dxl0190
 * @since 2017-05-08.
 */
class MRPatientSearchIntegrationSpec extends MeasureReportIntegrationSpec {

    @SuppressWarnings(['UnnecessaryGetter'])
    def 'can fetch a patient by patientId within measureReport'() {

        given: 'create a measureReport with patient and measure'
        def meaCreatedId = mockMeasure()
        def patId = mockPatient()
        def mReportId = mockMeasureReport(meaCreatedId, patId)

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
