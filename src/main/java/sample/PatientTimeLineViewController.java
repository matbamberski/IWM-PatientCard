package sample;

public class PatientTimeLineViewController {
    public void BackToPreviousView(){
        Main.getInstance().BackToMainView();
    }

    public void chooseNewGraph() { Main.getInstance().getPatientTimeLineView().getObservationChart();}
}
