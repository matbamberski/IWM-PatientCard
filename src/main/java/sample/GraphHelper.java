
package sample;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.exceptions.FHIRException;

import java.util.*;

public class GraphHelper {
    private XYChart<Date, Double> graph;
    private Date startDate;
    private Date endDate;
    private List<Map<String, String>> components;
    private CategoryAxis xAxis;


    public GraphHelper(XYChart<Date, Double> graph) {
        this.graph = graph;
        components = new ArrayList<Map<String, String>>();
    }

    public void plotLine(Observation observation) {
        prepareData(observation);
        final XYChart.Series<Date, Double> series = new XYChart.Series<Date, Double>();
        series.getData().add(new XYChart.Data<Date, Double>(startDate, 1.0));
        series.getData().add(new XYChart.Data<Date, Double>(endDate, 2.0));
        graph.getData().add(series);
    }

    private void prepareData(Observation observation){
        try {
            if (observation.hasEffective()){
                if (observation.hasEffectivePeriod()){
                    startDate = observation.getEffectivePeriod().getStartElement().getValue();
                    endDate = observation.getEffectivePeriod().getEndElement().getValue();
                }
            }

            if (observation.hasComponent()){
                for (Observation.ObservationComponentComponent component : observation.getComponent()){
                    if (component.hasValueQuantity()){
                        Map<String, String> entry = new HashMap<String, String>();
                        entry.put("desc", component.getCode().getCodingFirstRep().getDisplay());
                        entry.put("val", component.getValueQuantity().getValue().toPlainString() + " " + component.getValueQuantity().getUnit());
                        components.add(entry);
                    }
                }
            }

        } catch (FHIRException e){

        }

    }

    public XYChart<Date, Double> getGraph(){
        return graph;
    }

    private void clear() {
        graph.getData().clear();
    }
}
