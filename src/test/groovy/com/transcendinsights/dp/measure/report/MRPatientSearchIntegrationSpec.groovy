package com.transcendinsights.dp.measure.report

import ca.uhn.fhir.rest.api.MethodOutcome
import org.hl7.fhir.dstu3.model.*

/**
 * @author Kurt Kremer, dxl0190
 * @since 2017-05-08.
 */
class MRPatientSearchIntegrationSpec extends MeasureReportIntegrationSpec {

    @SuppressWarnings(['UnnecessaryObjectReferences', 'UnnecessaryGetter'])
    def 'can fetch a patient by patientId within measureReport'() {
        given: 'create a measureReport with patient and measure'
        //create a measure
        Measure newMeasure = mockMeasure()
        MethodOutcome measureCreated = restClient.create().resource(newMeasure).execute()
        def meaCreatedId = measureCreated.id

        //create a patient
        Patient patient = mockPatient()
        MethodOutcome patCreated = restClient.create().resource(patient).execute()
        def patId = patCreated.id

        //create a measureReport having measure & patient (setting only the required fields)
        MeasureReport measureReport = new MeasureReport()
        measureReport.setId('MR001_PAT001')
        measureReport.setIdentifier(new Identifier().with {
          type = new CodeableConcept().addCoding(
                  new Coding('http://hl7.org/fhir/v2/0203', 'MR', 'MR number'))
          system = 'urn:oid:1.2.36.146.595.217.0.1'
          value = 'MR001_PAT001'
          it
        })
        measureReport.setStatus(MeasureReport.MeasureReportStatus.COMPLETE)
        measureReport.setType(MeasureReport.MeasureReportType.INDIVIDUAL)
        measureReport.setMeasure(new Reference(meaCreatedId))
        measureReport.setPatient(new Reference(patId))
        measureReport.setPeriod(new Period().
                setStart(new Date(2016, 1, 1)).setEnd(new Date(2016, 2, 1)))
        MethodOutcome mReportCreated = restClient.create().resource(measureReport).execute()
        def mReportId = mReportCreated.id

        when: 'patient is searched like- localhost:port/MeasureReport?patient=id'
        def searchUrl = "MeasureReport?patient=$patId"
        Bundle searchMReport = restClient.search()
                .byUrl(searchUrl)
                .returnBundle(Bundle)
                .execute()

        then: 'the search returns measureReport for the patId'
        searchMReport.entry.get(0).getResource().resourceType.name() == 'MeasureReport'
        searchMReport.entry.get(0).getResource().id == mReportId.value
    }

}
