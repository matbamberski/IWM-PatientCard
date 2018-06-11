package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.hl7.fhir.dstu3.model.Observation;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class Graph extends Scene {

    final List<Observation> observations;
    final GraphHelper graphHelper;
    private String currentObservation;
    private DatePicker startDate;
    private DatePicker endDate;
    private final String pattern = "dd.MM.yyyy";



    public Graph(int width, int height, final List<Observation> observations) {
        super(new Pane(), width, height);
        this.observations = observations;
        NumberAxis yAxis = new NumberAxis();
        CategoryAxis xAxis = new CategoryAxis();
        LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
        graphHelper = new GraphHelper(lineChart);
        try {
            HBox root = FXMLLoader.load(getClass().getClassLoader().getResource("Graph.fxml"));
            setRoot(root);

            ChoiceBox choiceObservation = (ChoiceBox) lookup("#observationChoice");
            startDate = (DatePicker) lookup("#startDate");
            endDate = (DatePicker) lookup("#endDate");

            convertDatePicker(startDate);
            convertDatePicker(endDate);

            final ObservableList<String> availableObservations = graphHelper.checkAvailableObservations(observations);
            choiceObservation.setItems(availableObservations);

            choiceObservation.getSelectionModel().selectedIndexProperty().addListener(
                    new ChangeListener<Number>() {
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            currentObservation = availableObservations.get(newValue.intValue());
                            graphHelper.plotLine(observations, currentObservation);
                        }
                    }
            );
            currentObservation = availableObservations.get(0);
            graphHelper.plotLine(observations, currentObservation);
            choiceObservation.getSelectionModel().select(0);

            HBox.setHgrow(lineChart, Priority.ALWAYS);
            root.getChildren().add(lineChart);

        } catch (java.io.IOException exception) {
            System.out.println(exception.toString());
        }
    }

    public void setDateFilter(boolean toFilter){
        if (startDate.getValue() != null && endDate.getValue() != null){
            if (toFilter){
                if (startDate.getValue().isBefore(endDate.getValue())
                        || startDate.getValue().equals(endDate.getValue())){
                    Instant start = Instant.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()));
                    Instant end = Instant.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()));
                    Date firstDate = Date.from(start);
                    Date secondDate = Date.from(end);
                    graphHelper.setFilterTrue(observations, currentObservation, firstDate, secondDate);
                }
            } else
                graphHelper.setFilterFalse(observations, currentObservation);
        }
    }

    private void convertDatePicker(DatePicker picker){
        StringConverter converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter =
                    DateTimeFormatter.ofPattern(pattern);
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        picker.setConverter(converter);
        picker.setPromptText(pattern.toLowerCase());
    }
}