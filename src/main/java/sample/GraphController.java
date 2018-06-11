package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class GraphController {

    @FXML
    public void backToPatientLineView(final ActionEvent event){
        Main.getInstance().BackToPatientTimeLineView();
    }

    @FXML
    public void changeToDefault(final ActionEvent event){
        Main.getInstance().getPatientTimeLineView().getGraph().setDateFilter(false);
    }

    @FXML
    public void redrawChart(final ActionEvent event){
        Main.getInstance().getPatientTimeLineView().getGraph().setDateFilter(true);
    }
}