package sample;

import javafx.scene.chart.XYChart;
import org.hl7.fhir.dstu3.model.Observation;

import java.util.Date;
import java.util.List;

public class Graph {
    private XYChart<Date, Double> graph;
    private Date startDate;
    private Date endDate;
    private List<ca.uhn.fhir.model.dstu2.resource.Observation.Component> components;


    public Graph(XYChart<Date, Double> graph) {
        this.graph = graph;
    }

    public void plotLine(Observation observation) {
        try {
            if (observation.hasEffective() && observation.hasComponent()) {
                final XYChart.Series<Date, Double> series = new XYChart.Series<Date, Double>();

                for (Observation.ObservationComponentComponent component : observation.getComponent())
                    if (observation.hasEffectiveDateTimeType() && observation.getComponent().size()==1) {
                    series.getData().add(new XYChart.Data<Date, Double>
                            (observation.getEffectiveDateTimeType().getValue(), component.getValueQuantity().getValue().doubleValue()));
                } else if (observation.hasEffectivePeriod()) {
                    series.getData().add(new XYChart.Data<Date, Double>(observation.getEffectivePeriod().getStartElement().getValue(),
                            1.0));
                    series.getData().add(new XYChart.Data<Date, Double>(observation.getEffectivePeriod().getEndElement().getValue(),
                            2.0));
                }
                graph.getData().add(series);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void prepareData(Observation observation){

    }

    private void clear() {
        graph.getData().clear();
    }
}
