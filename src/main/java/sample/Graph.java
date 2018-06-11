package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.hl7.fhir.dstu3.model.Observation;

public class Graph extends Scene {

    public Graph(int width, int height, Observation observation) {
        super(new Pane(), width, height);
        try {
            AnchorPane root = FXMLLoader.load(getClass().getClassLoader().getResource("Graph.fxml"));
            setRoot(root);

            LineChart lineChart = (LineChart) lookup("#lineChart");
            GraphHelper graphHelper = new GraphHelper(lineChart);
            graphHelper.plotLine(observation);


        } catch (java.io.IOException exception) {
            System.out.println(exception.toString());
        }
    }
}