package sample;

public class MainViewController {
    public void clearFilterClicked(){
        System.out.println("Filter Cleared");
    }

    public void chooseButtonClicked(){
        PatientTimeLineView patientTimeLineView = new PatientTimeLineView(600,800,new Object());
        Main.getInstance().setNewScene(patientTimeLineView);
    }
}
