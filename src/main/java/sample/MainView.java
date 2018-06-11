package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Patient;

import java.util.ArrayList;
import java.util.List;

public class MainView extends Scene {
    private ListView PatientList;
    private TextField NameFilter;

    private List<Patient> allPatients;
    private List<Patient> currentPatients;



    public void ChoosePatient(){
        int index = PatientList.getSelectionModel().getSelectedIndex() >= 0 ? PatientList.getSelectionModel().getSelectedIndex() : 0;
        PatientTimeLineView patientTimeLineView = new PatientTimeLineView(600,500,currentPatients.get(index));
        Main.getInstance().setPatient(patientTimeLineView);
        Main.getInstance().setNewScene(patientTimeLineView);
    }

    public void ClearFilter(){
        NameFilter.clear();
    }

    private void SearchPatients(){
        String filterText = NameFilter.getText();
        List<Patient> patientList = new ArrayList<Patient>();
        for (Patient patient : allPatients) {
            for (HumanName name : patient.getName()) {
                if(name.getFamily().toLowerCase().contains(filterText.toLowerCase())){
                    patientList.add(patient);
                    break;
                }
            }
        }

        ApplyPatientsToList(patientList);
    }

    private void ApplyPatientsToList(List<Patient> patientList){
        ObservableList<String> items = FXCollections.observableArrayList ();
        for (Patient patient : patientList) {
            if(!patient.getName().isEmpty()) {
                String name = "";
                if( patient.getName().get(0) != null) {
                    name += Utils.CutLastNumbers(patient.getName().get(0).getFamily());
                }
                if( patient.getName().get(0) != null &&
                        !patient.getName().get(0).getGiven().isEmpty() &&
                        patient.getName().get(0).getGiven().get(0) != null) {
                    if (name.length() > 0){
                        name += " ";
                    }
                    name += Utils.CutLastNumbers(patient.getName().get(0).getGiven().get(0).toString());
                }
                items.add(name);
            }
        }
        PatientList.setItems(items);
        currentPatients = patientList;
    }

    MainView(int width, int height){
        super(new Pane(), width, height);
        try {
            VBox root = FXMLLoader.load(getClass().getClassLoader().getResource("MainView.fxml"));
            setRoot(root);
            PatientList = (ListView) lookup("#PatientList");
            NameFilter = (TextField) lookup("#NameFilter");

            NameFilter.textProperty().addListener(new ChangeListener<String>() {
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {

                    SearchPatients();
                }
            });


            allPatients = Main.getInstance().getDataContext().GetPatients();
            ApplyPatientsToList(allPatients);
        }
        catch (java.io.IOException exception){
            System.out.println(exception.toString());
        }
    }

}
