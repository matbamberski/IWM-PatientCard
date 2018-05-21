package sample;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Entry;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;

import java.util.ArrayList;
import java.util.List;

public class DataContext {

    private FhirContext fhirContext;
    private IGenericClient client;

    public DataContext(){
        fhirContext = FhirContext.forDstu2();
        fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        String serverBase = "http://fhirtest.uhn.ca/baseDstu2";
        client = fhirContext.newRestfulGenericClient(serverBase);
    }

    public List<Patient> GetPatients(){
        List<Patient> result = new ArrayList<Patient>();
        Bundle results = client
                .search()
                .forResource(Patient.class)
                .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                .execute();
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
