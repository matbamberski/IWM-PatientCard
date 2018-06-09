package sample;

import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class PatientTimeLineView extends Scene {
    private ListView TimeLine;
    private Label Name;
    private Label NameText;
    private Label LastName;
    private Label LastNameText;
    private Label BirthDate;
    private Label BirthDateText;
    private Button BackButton;

    public PatientTimeLineView(int width, int height, Patient patientData){
        super(new Pane(), width, height);
        try {
            VBox root = FXMLLoader.load(getClass().getClassLoader().getResource("PatientTimeLineView.fxml"));
            setRoot(root);

            TimeLine = (ListView) lookup("#TimeLine");
            Name = (Label) lookup("#Name");
            NameText = (Label) lookup("#NameText");
            LastName = (Label) lookup("#LastName");
            LastNameText = (Label) lookup("#LastNameText");
            BirthDate = (Label) lookup("#BirthDate");
            BirthDateText = (Label) lookup("#BirthDateText");
            BackButton = (Button) lookup("#BackButton");

            if( patientData.getName().get(0) != null &&
                    !patientData.getName().get(0).getFamily().isEmpty() &&
                    patientData.getName().get(0).getFamily().get(0) != null) {
                LastName.setText(patientData.getName().get(0).getFamily().get(0).toString());
            }

            if( patientData.getName().get(0) != null &&
                    !patientData.getName().get(0).getGiven().isEmpty() &&
                    patientData.getName().get(0).getGiven().get(0) != null) {
                Name.setText(patientData.getName().get(0).getGiven().get(0).toString());
            }

            if(patientData.getBirthDate() != null) {
                DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                BirthDate.setText(df.format(patientData.getBirthDate()));
            }

            List<Observation> observations = Main.getInstance().getDataContext().GetPatientObservations(patientData);
            List<MedicationStatement> medicationStatements = Main.getInstance().getDataContext().GetPatientMedicationStatement(patientData);

        }
        catch (java.io.IOException exception){
            System.out.println(exception.toString());
        }
    }
}
