package sample;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataContext {

    private FhirContext fhirContext;
    private IGenericClient client;

    DataContext(){
        fhirContext = FhirContext.forDstu3();
        fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
//        String serverBase = "http://fhirtest.uhn.ca/baseDstu2";
        String serverBase = "http://localhost:8080/baseDstu3";
        client = fhirContext.newRestfulGenericClient(serverBase);
    }

    private void addInitialUrlsToSet(Bundle theBundle, Set<String> theResourcesAlreadyAdded) {
        for (BundleEntryComponent entry : theBundle.getEntry()) {
            theResourcesAlreadyAdded.add(entry.getFullUrl());
        }
    }

    private void addAnyResourcesNotAlreadyPresentToBundle(Bundle theAggregatedBundle, Bundle thePartialBundle, Set<String> theResourcesAlreadyAdded) {
        for (BundleEntryComponent entry : thePartialBundle.getEntry()) {
            if (!theResourcesAlreadyAdded.contains(entry.getFullUrl())) {
                theResourcesAlreadyAdded.add(entry.getFullUrl());
                theAggregatedBundle.getEntry().add(entry);
            }
        }
    }

    public List<Observation> GetPatientObservations(Patient patient){
        Bundle results = client.search()
                .forResource(Observation.class)
                .where(Observation.PATIENT.hasId(patient.getId()))
                .returnBundle(Bundle.class)
                .execute();
        List<Observation> resultsList = new ArrayList<Observation>();
        List<BundleEntryComponent> entries = results.getEntry();
        for (BundleEntryComponent entryComponent : entries) {
            Observation observation = (Observation) entryComponent.getResource();
            resultsList.add(observation);
        }
        return resultsList;
    }

    public List<MedicationRequest> GetPatientMedicationStatement(Patient patient){
        Set<String> resourcesAlreadyAdded = new HashSet<String>();

        Bundle results = client.search()
                .forResource(MedicationRequest.class)
                .where(MedicationRequest.PATIENT.hasId(patient.getId()))
                .returnBundle(Bundle.class)
                .execute();

        addInitialUrlsToSet(results, resourcesAlreadyAdded);
        Bundle partialBundle = results;

        while (partialBundle.getLink(IBaseBundle.LINK_NEXT) != null){
            partialBundle = client.loadPage().next(partialBundle).execute();
            addAnyResourcesNotAlreadyPresentToBundle(results, partialBundle, resourcesAlreadyAdded);
        }

        List<MedicationRequest> resultsList = new ArrayList<MedicationRequest>();
        List<BundleEntryComponent> entries = results.getEntry();
        for(int i = 0; i < entries.size();i++){
            BundleEntryComponent x = entries.get(i);
            Resource y = x.getResource();
            if(y.getClass().equals(MedicationRequest.class)) {
                MedicationRequest medicationStatement = (MedicationRequest) y;
                resultsList.add(medicationStatement);
            }
        }
        return resultsList;
    }

//    public Medication GetMedicationData(MedicationStatement medicationStatement){
//        Bundle results = client.search()
//                .forResource(Medication.class)
//                .where(medicationStatement.getMedication().)
//                .returnBundle(Bundle.class)
//                .execute();
//    }

    public List<Patient> GetPatients(){

        List<Patient> result = new ArrayList<Patient>();
        Set<String> resourcesAlreadyAdded = new HashSet<String>();

        Bundle results = client
                .search()
                .forResource(Patient.class)
                .returnBundle(Bundle.class)
                .execute();

        addInitialUrlsToSet(results, resourcesAlreadyAdded);

        Bundle partialBundle = results;

        while (partialBundle.getLink(IBaseBundle.LINK_NEXT) != null){
            partialBundle = client.loadPage().next(partialBundle).execute();
            addAnyResourcesNotAlreadyPresentToBundle(results, partialBundle, resourcesAlreadyAdded);
        }

        results.getLink().clear();

        if (results.getTotal() != results.getEntry().size()) {
            System.out.println("Counts didn't match! Expected " + results.getTotal() + " but bundle only had " + results.getEntry().size() + " entries!");
        }

        List<BundleEntryComponent> entries = results.getEntry();
        for (BundleEntryComponent entryComponent : entries) {
            Patient patient = (Patient) entryComponent.getResource();
            result.add(patient);
        }
        return result;
    }
}
