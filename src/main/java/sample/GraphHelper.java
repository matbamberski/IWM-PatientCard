
package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.exceptions.FHIRException;

import java.text.SimpleDateFormat;
import java.util.*;

public class GraphHelper {
    private XYChart<String, Number> graph;


    public GraphHelper(XYChart<String, Number> graph) {
        this.graph = graph;
    }

    public void plotLine(List<Observation> observations, String observationName) {
        clear();
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        series.setName(observationName);
        List<Pair<Date, Number>> list = prepareData(observations, observationName);
        for (int i = 0; i < list.size(); i++) {
            series.getData().add(new XYChart.Data<String, Number>(
                    new SimpleDateFormat("dd.MM.yyyy").format(list.get(i).getKey()), list.get(i).getValue()));
        }
        graph.getData().add(series);
    }

    public ObservableList<String> checkAvailableObservations(List<Observation> observations) {
        //ObservableSet<String> availableObservations = FXCollections.observableSet();
        HashSet<String> availableObservations = new HashSet<String>();
        for (Observation observation : observations) {
            if (observation != null && observation.hasCode() && observation.hasEffective() && observation.getCode().hasText()) {
                availableObservations.add(observation.getCode().getText());
            }
        }
        ObservableList<String> result = FXCollections.observableArrayList(availableObservations);
        return result;
    }

    private List<Pair<Date, Number>> prepareData(List<Observation> observations, String observationName) {
        List<Pair<Date, Number>> results = new ArrayList<Pair<Date, Number>>();
        try {
            for (Observation observation : observations) {
                int index = 0;
                double x = 0;
                Date date = new Date();
                if (observation != null && observation.hasCode() && observation.getCode().hasText() && observation.getCode().getText().toLowerCase().contains(observationName.toLowerCase())) {
                    if (observation.hasIssued()) {
                        date = observation.getIssued();
                    }
                    if (observation.hasValueQuantity()) {
                        x = observation.getValueQuantity().getValue().doubleValue();
                    }
                    results.add(index, new Pair<Date, Number>(date, x));
                    index++;
                }
            }
        } catch (FHIRException e) {}
        Collections.sort(results, new Comparator<Pair<Date, Number>>()
        {
            public int compare(Pair<Date, Number> o1, Pair<Date, Number> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return results;
    }

    public XYChart<String, Number> getGraph() {
        return graph;
    }

    private void clear() {
        graph.getData().clear();
    }
}
