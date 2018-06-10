package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.exceptions.FHIRException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class PatientTimeLineView extends Scene {

    PatientTimeLineView(int width, int height, Patient patientData){
        super(new Pane(), width, height);
        try {
            VBox root = FXMLLoader.load(getClass().getClassLoader().getResource("PatientTimeLineView.fxml"));
            setRoot(root);

            Label name = (Label) lookup("#Name");
            Label lastName = (Label) lookup("#LastName");
            Label birthDate = (Label) lookup("#BirthDate");
            TableView<Resource> patientDataTable = new TableView<Resource>();
            patientDataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            if( patientData.getName().get(0) != null &&
                    patientData.getName().get(0).getFamily() != null) {
                lastName.setText(patientData.getName().get(0).getFamily());
            }

            if( patientData.getName().get(0) != null &&
                    !patientData.getName().get(0).getGiven().isEmpty() &&
                    patientData.getName().get(0).getGiven().get(0) != null) {
                name.setText(patientData.getName().get(0).getGiven().get(0).toString());
            }

            if(patientData.getBirthDate() != null) {
                DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                birthDate.setText(df.format(patientData.getBirthDate()));
            }

            List<Observation> observations = Main.getInstance().getDataContext().GetPatientObservations(patientData);
            List<MedicationRequest> medicationStatements = Main.getInstance().getDataContext().GetPatientMedicationStatement(patientData);
            ObservableList<Resource> allData = FXCollections.observableArrayList();


            for (Observation observation:observations) {
                String desc = "";
                try {
                    if(observation.hasValueCodeableConcept())
                        desc += observation.getValueCodeableConcept().getText();
                    if(observation.hasValueQuantity())
                        desc += observation.getValueQuantity().getValue().toString().substring(0, 5) + " " + observation.getValueQuantity().getUnit();
                }
                catch (FHIRException ignore){

                }
                allData.add(new Resource(Resource.Type.Observation, observation.getCode().getText(), observation.getIssued(), desc));
            }

            for (MedicationRequest medicationStatment:medicationStatements) {
                allData.add(new Resource(Resource.Type.MedicationStatement, medicationStatment.getNote().toString(), medicationStatment.getAuthoredOn(), medicationStatment.getDosageInstruction().toString()));
            }

            TableColumn<Resource, String> typeColumn = new TableColumn<Resource, String>("Typ");
            TableColumn<Resource, String> titleColumn = new TableColumn<Resource, String>("Nazwa");
            TableColumn<Resource, String> timeColumn = new TableColumn<Resource, String>("Godzina");
            TableColumn<Resource, String> dateColumn = new TableColumn<Resource, String>("Data");
            TableColumn<Resource, String> descriptionColumn = new TableColumn<Resource, String>("Opis");
            typeColumn.setCellValueFactory(new PropertyValueFactory<Resource,String>("type"));
            titleColumn.setCellValueFactory(new PropertyValueFactory<Resource,String>("title"));
            timeColumn.setCellValueFactory(new PropertyValueFactory<Resource,String>("time"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<Resource,String>("date"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<Resource,String>("description"));


            patientDataTable.getColumns().addAll(typeColumn, titleColumn, timeColumn, dateColumn, descriptionColumn);
            patientDataTable.setItems(allData);
            root.getChildren().add(patientDataTable);
            VBox.setVgrow(patientDataTable, Priority.ALWAYS);

        }
        catch (java.io.IOException exception){
            System.out.println(exception.toString());
        }
    }
}
