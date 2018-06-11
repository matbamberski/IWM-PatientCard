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
import javafx.scene.layout.*;
import org.hl7.fhir.dstu3.model.Observation;

import java.util.List;

public class Graph extends Scene {

    final List<Observation> observation;

    public Graph(int width, int height, final List<Observation> observation) {
        super(new Pane(), width, height);
        this.observation = observation;
        try {
            HBox root = FXMLLoader.load(getClass().getClassLoader().getResource("Graph.fxml"));
            setRoot(root);

            NumberAxis yAxis = new NumberAxis();
            CategoryAxis xAxis = new CategoryAxis();
            LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
            final GraphHelper graphHelper = new GraphHelper(lineChart);


            ChoiceBox choiceObservation = (ChoiceBox) lookup("#observationChoice");
            final ObservableList<String> availableObservations = graphHelper.checkAvailableObservations(observation);
            choiceObservation.setItems(availableObservations);

            choiceObservation.getSelectionModel().selectedIndexProperty().addListener(
                    new ChangeListener<Number>() {
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            graphHelper.plotLine(observation, availableObservations.get(newValue.intValue()));
                        }
                    }
            );
            graphHelper.plotLine(observation, availableObservations.get(0));
            choiceObservation.getSelectionModel().select(0);

            HBox.setHgrow(lineChart, Priority.ALWAYS);
            root.getChildren().add(lineChart);
        } catch (java.io.IOException exception) {
            System.out.println(exception.toString());
        }
    }
}