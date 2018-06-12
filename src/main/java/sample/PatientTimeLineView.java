package sample;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.exceptions.FHIRException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

class PatientTimeLineView extends Scene {

    private List<Observation> observations;
    private Graph graph;
    private ObservableList<Resource> allData;
    private TableView<Resource> patientDataTable;
    private TableColumn<Resource, String> timeColumn;
    private TableColumn<Resource, String> dateColumn;

    PatientTimeLineView(int width, int height, Patient patientData){
        super(new Pane(), width, height);
        try {
            VBox root = FXMLLoader.load(getClass().getClassLoader().getResource("PatientTimeLineView.fxml"));
            setRoot(root);

            Label name = (Label) lookup("#Name");
            Label lastName = (Label) lookup("#LastName");
            Label birthDate = (Label) lookup("#BirthDate");
            TextField dateFilter = (TextField) lookup("#DateFilter");
            patientDataTable = new TableView<Resource>();
            patientDataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            if( patientData.getName().get(0) != null &&
                    patientData.getName().get(0).getFamily() != null) {
                lastName.setText(Utils.CutLastNumbers(patientData.getName().get(0).getFamily()));
            }

            if( patientData.getName().get(0) != null &&
                    !patientData.getName().get(0).getGiven().isEmpty() &&
                    patientData.getName().get(0).getGiven().get(0) != null) {
                name.setText(Utils.CutLastNumbers(patientData.getName().get(0).getGiven().get(0).toString()));
            }

            if(patientData.getBirthDate() != null) {
                DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                birthDate.setText(df.format(patientData.getBirthDate()));
            }

            observations = Main.getInstance().getDataContext().GetPatientObservations(patientData);
            List<MedicationRequest> medicationStatements = Main.getInstance().getDataContext().GetPatientMedicationStatement(patientData);
            allData = FXCollections.observableArrayList();


            for (Observation observation:observations) {
                String desc = "";
                try {
                    if(observation.hasValueCodeableConcept())
                        desc += observation.getValueCodeableConcept().getText();
                    if(observation.hasValueQuantity()) {
                        String quantity;
                        if (observation.getValueQuantity().getValue().toString().length() > 5) {
                            quantity = observation.getValueQuantity().getValue().toString().substring(0, 5);
                        } else {
                            quantity = observation.getValueQuantity().getValue().toString();
                        }
                        desc += quantity + " " + observation.getValueQuantity().getUnit();
                    }
                    if(observation.hasValueSampledData())
                        desc += observation.getValueSampledData().getData();
                    if(observation.hasValueRatio())
                        desc += observation.getValueRatio().getNumerator() + "/" + observation.getValueRatio().getDenominator();
                    if(observation.hasValueRange())
                        desc += observation.getValueRange().getLow() + " - " + observation.getValueRange().getHigh();
                    if(observation.hasValuePeriod())
                        desc += new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(observation.getValuePeriod().getStart()) + " - " + new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(observation.getValuePeriod().getEnd());
                }
                catch (FHIRException ignore){

                }
                allData.add(new Resource(Resource.Type.Observation, observation.getCode().getText(), observation.getIssued(), desc));
            }

            for (MedicationRequest medicationStatement:medicationStatements) {
                String title = "";
                String desc = "";
                try{
                    if(medicationStatement.hasMedicationCodeableConcept())
                        title += medicationStatement.getMedicationCodeableConcept().getText();
                    if(medicationStatement.hasNote())
                        desc += medicationStatement.getNote();
                    if (medicationStatement.hasDosageInstruction())
                        desc += medicationStatement.getDosageInstruction().get(0).getSequence();
                    if(medicationStatement.hasDispenseRequest()) {
                        if(medicationStatement.getDispenseRequest().hasQuantity())
                            desc += medicationStatement.getDispenseRequest().getQuantity();
                    }
                }catch (FHIRException ignore){

                }
                allData.add(new Resource(Resource.Type.MedicationStatement, title, medicationStatement.getAuthoredOn(), desc));
            }

            dateFilter.textProperty().addListener(new ChangeListener<String>() {
                public void changed(ObservableValue<? extends String> observable,
                                    String oldValue, String newValue) {

                    FilterResources(newValue);
                }
            });

            TableColumn<Resource, String> typeColumn = new TableColumn<Resource, String>("Typ");
            TableColumn<Resource, String> titleColumn = new TableColumn<Resource, String>("Nazwa");
            timeColumn = new TableColumn<Resource, String>("Godzina");
            dateColumn = new TableColumn<Resource, String>("Data");
            TableColumn<Resource, String> descriptionColumn = new TableColumn<Resource, String>("Opis");
            typeColumn.setCellValueFactory(new PropertyValueFactory<Resource,String>("type"));
            titleColumn.setCellValueFactory(new PropertyValueFactory<Resource,String>("title"));
            timeColumn.setCellValueFactory(new PropertyValueFactory<Resource,String>("time"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<Resource,String>("date"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<Resource,String>("description"));

            dateColumn.setSortType(TableColumn.SortType.DESCENDING);
            timeColumn.setSortType(TableColumn.SortType.DESCENDING);

            timeColumn.setMaxWidth(60);
            timeColumn.setMinWidth(60);
            dateColumn.setMaxWidth(80);
            dateColumn.setMinWidth(80);


            patientDataTable.setItems(allData);
            patientDataTable.getColumns().addAll(typeColumn, titleColumn, descriptionColumn, timeColumn, dateColumn);
            patientDataTable.getSortOrder().addAll(dateColumn, timeColumn);
            root.getChildren().add(patientDataTable);
            VBox.setVgrow(patientDataTable, Priority.ALWAYS);

        }
        catch (java.io.IOException exception){
            System.out.println(exception.toString());
        }
    }

    public void FilterResources(String data){
        if(data.length() > 0) {
            ObservableList<Resource> filterData = FXCollections.observableArrayList();

            for (Resource resource : allData) {
                if(resource.getDate().startsWith(data)){
                    filterData.add(resource);
                }
            }

            patientDataTable.setItems(filterData);
        }
        else{
            patientDataTable.setItems(allData);
        }
        patientDataTable.getSortOrder().addAll(dateColumn, timeColumn);
    }

    public void getObservationChart(){
        graph = new Graph(600,500, observations);
        Main.getInstance().setNewScene(graph);
    }

    public Graph getGraph(){
        return graph;
    }
}
