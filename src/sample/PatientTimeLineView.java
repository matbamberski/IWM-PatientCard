package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class PatientTimeLineView extends Scene {
    private ListView TimeLine;
    private Label Name;
    private Label NameText;
    private Label LastName;
    private Label LastNameText;
    private Label BirthDate;
    private Label BirthDateText;
    private Button BackButton;

    public PatientTimeLineView(int width, int height, Object userData){
        super(new Pane(), width, height);
        try {
            VBox root = FXMLLoader.load(getClass().getResource("PatientTimeLineView.fxml"));
            setRoot(root);

            TimeLine = (ListView) lookup("#TimeLine");
            Name = (Label) lookup("#Name");
            NameText = (Label) lookup("#NameText");
            LastName = (Label) lookup("#LastName");
            LastNameText = (Label) lookup("#LastNameText");
            BirthDate = (Label) lookup("#BirthDate");
            BirthDateText = (Label) lookup("#BirthDateText");
            BackButton = (Button) lookup("#BackButton");
        }
        catch (java.io.IOException exception){
            System.out.println(exception.toString());
        }
    }
}
