package sample;

import ca.uhn.fhir.model.dstu2.resource.Patient;
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

import java.util.List;

public class MainView extends Scene {
    private ListView PatientList;
    private Button ChooseButton;
    private Label Title;
    private TextField NameFilter;
    private ImageView ClearFilter;
    private DataContext dataContext;

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
            dataContext = new DataContext();
            List<Patient> patients = dataContext.GetPatients();

            ObservableList<String> items = FXCollections.observableArrayList ();
            for (Patient patient : patients) {
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
        }
        catch (java.io.IOException exception){
            System.out.println(exception.toString());
        }
    }
}
