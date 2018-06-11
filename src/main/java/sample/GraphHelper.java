
package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.exceptions.FHIRException;

import java.text.SimpleDateFormat;
import java.util.*;

public class GraphHelper {
    private XYChart<String, Number> graph;
    private boolean daysFilter;
    private Date start;
    private Date end;


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
        if (daysFilter)
            return filteredDates(results);
        return results;
    }

    public void setFilterTrue(List<Observation> observations, String observationName, Date start, Date end){
        this.daysFilter = true;
        this.start = start;
        this.end = end;
        plotLine(observations, observationName);
    }

    public void setFilterFalse(List<Observation> observations, String observationName){
        this.daysFilter = false;
        plotLine(observations, observationName);
    }

    private List<Pair<Date, Number>> filteredDates(List<Pair<Date, Number>> listBeforeFilter){
        List<Pair<Date, Number>> results = new ArrayList<Pair<Date, Number>>();

        for (int i=0; i<listBeforeFilter.size(); i++){
            if ((listBeforeFilter.get(i).getKey().before(end) && listBeforeFilter.get(i).getKey().after(start)) ||
                    (listBeforeFilter.get(i).getKey().equals(end) || listBeforeFilter.get(i).getKey().equals(start)))
                results.add(listBeforeFilter.get(i));
        }
        return results;
    }

    private void clear() {
        graph.getData().clear();
    }
}
