package sample;

public class MainViewController {
    public void clearFilterClicked(){
        Main.getInstance().getMainScene().ClearFilter();
    }

    public void chooseButtonClicked(){
        Main.getInstance().getMainScene().ChoosePatient();
    }
}
