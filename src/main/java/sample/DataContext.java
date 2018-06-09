package sample;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.*;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Entry;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataContext {

    private FhirContext fhirContext;
    private IGenericClient client;

    public DataContext(){
        fhirContext = FhirContext.forDstu2();
        fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        String serverBase = "http://fhirtest.uhn.ca/baseDstu2";
        client = fhirContext.newRestfulGenericClient(serverBase);
    }

    private void addInitialUrlsToSet(Bundle theBundle, Set<String> theResourcesAlreadyAdded) {
        for (Bundle.Entry entry : theBundle.getEntry()) {
            theResourcesAlreadyAdded.add(entry.getFullUrl());
        }
    }

    private void addAnyResourcesNotAlreadyPresentToBundle(Bundle theAggregatedBundle, Bundle thePartialBundle, Set<String> theResourcesAlreadyAdded) {
        for (Bundle.Entry entry : thePartialBundle.getEntry()) {
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
        List<Entry> entries = results.getEntry();
        for(int i = 0; i < entries.size();i++){
            Entry x = entries.get(i);
            IResource y = x.getResource();
            Observation observation = (Observation)y;
            resultsList.add(observation);
        }
        return resultsList;
    }

    public List<MedicationStatement> GetPatientMedicationStatement(Patient patient){
        Set<String> resourcesAlreadyAdded = new HashSet<String>();

        Bundle results = client.search()
                .forResource(MedicationStatement.class)
                .where(MedicationStatement.PATIENT.hasId(patient.getId()))
                .returnBundle(Bundle.class)
                .execute();

        addInitialUrlsToSet(results, resourcesAlreadyAdded);
        Bundle partialBundle = results;

        while (partialBundle.getLink(IBaseBundle.LINK_NEXT) != null){
            partialBundle = client.loadPage().next(partialBundle).execute();
            addAnyResourcesNotAlreadyPresentToBundle(results, partialBundle, resourcesAlreadyAdded);
        }

        List<MedicationStatement> resultsList = new ArrayList<MedicationStatement>();
        List<Entry> entries = results.getEntry();
        for(int i = 0; i < entries.size();i++){
            Entry x = entries.get(i);
            IResource y = x.getResource();
            if(y.getClass().equals(MedicationStatement.class)) {
                MedicationStatement medicationStatement = (MedicationStatement) y;
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

        List<Entry> entries = results.getEntry();
        for(int i = 0; i < entries.size();i++){
            Entry x = entries.get(i);
            IResource y = x.getResource();
            Patient patient = (Patient)y;
            result.add(patient);
        }
        return result;
    }
}
