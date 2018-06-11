package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class GraphController {

    @FXML
    public void changeToDay(final ActionEvent event){

    }
    @FXML
    public void changeToMonth(final ActionEvent event){

    }
    @FXML
    public void changeToWeek(final ActionEvent event){

    }
    @FXML
    public void changeToYear(final ActionEvent event){

    }
    @FXML
    public void backToPatientLineView(final ActionEvent event){
        Main.getInstance().BackToPatientTimeLineView();
    }
}