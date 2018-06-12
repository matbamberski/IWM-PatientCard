package sample;

public class PatientTimeLineViewController {
    public void BackToPreviousView(){
        Main.getInstance().BackToMainView();
    }

    public void clearFilterClicked(){
        Main.getInstance().getPatientTimeLineView().FilterResources("");
    }

    public void chooseNewGraph() { Main.getInstance().getPatientTimeLineView().getObservationChart();}
}
