package sample;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.StringDt;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class MainView extends Scene {
    private ListView PatientList;
    private Button ChooseButton;
    private Label Title;
    private TextField NameFilter;
    private ImageView ClearFilter;
    private DataContext dataContext;

    private List<Patient> allPatients;
    private List<Patient> currentPatients;

    public void ChoosePatient(){
        int index = PatientList.getSelectionModel().getSelectedIndex() >= 0 ? PatientList.getSelectionModel().getSelectedIndex() : 0;
        PatientTimeLineView patientTimeLineView = new PatientTimeLineView(600,800,currentPatients.get(index));
        Main.getInstance().setNewScene(patientTimeLineView);
    }

    public void ClearFilter(){
        NameFilter.clear();
    }

    public void SearchPatients(){
        String filterText = NameFilter.getText();
        List<Patient> patientList = new ArrayList<Patient>();
        for (Patient patient : allPatients) {
            boolean isOnList = false;
            for (HumanNameDt name : patient.getName()) {
                for (StringDt family : name.getFamily()) {
                    if(family.toString().toLowerCase().contains(filterText.toLowerCase())){
                        patientList.add(patient);
                        isOnList = true;
                        break;
                    }
                }
                if(isOnList){
                    break;
                }
            }
        }

        ApplyPatientsToList(patientList);
    }

    public void ApplyPatientsToList(List<Patient> patientList){
        ObservableList<String> items = FXCollections.observableArrayList ();
        for (Patient patient : patientList) {
            if(!patient.getName().isEmpty()) {
                String name = "";
                if( patient.getName().get(0) != null &&
                        !patient.getName().get(0).getFamily().isEmpty() &&
                        patient.getName().get(0).getFamily().get(0) != null) {
                    name += patient.getName().get(0).getFamily().get(0).toString();
                }
                if( patient.getName().get(0) != null &&
                        !patient.getName().get(0).getGiven().isEmpty() &&
                        patient.getName().get(0).getGiven().get(0) != null) {
                    if (name.length() > 0){
                        name += " ";
                    }
                    name += patient.getName().get(0).getGiven().get(0).toString();
                }
                items.add(name);
            }
        }
        PatientList.setItems(items);
        currentPatients = patientList;
    }

    public MainView(int width, int height){
        super(new Pane(), width, height);
        try {
            VBox root = FXMLLoader.load(getClass().getClassLoader().getResource("MainView.fxml"));
            setRoot(root);
            PatientList = (ListView) lookup("#PatientList");
            ChooseButton = (Button) lookup("#ChooseButton");
            Title = (Label) lookup("#Title");
            NameFilter = (TextField) lookup("#NameFilter");
            ClearFilter = (ImageView) lookup("#ClearFilter");

            NameFilter.textProperty().addListener(new ChangeListener<String>() {
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {

                    SearchPatients();
                }
            });

            dataContext = new DataContext();
            allPatients = dataContext.GetPatients();
            ApplyPatientsToList(allPatients);
        }
        catch (java.io.IOException exception){
            System.out.println(exception.toString());
        }
    }

}
