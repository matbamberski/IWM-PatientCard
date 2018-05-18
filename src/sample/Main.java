package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        Scene scene = new Scene(root, 600, 800);
        primaryStage.setTitle("Patient Card");
        primaryStage.setScene(scene);
        primaryStage.show();

//        ListView PatientList = (ListView) scene.lookup("#PatientList");
//        Button ChooseButton = (Button) scene.lookup("#ChooseButton");
//        Label Title = (Label) scene.lookup("#Title");
//        TextField NameFilter = (TextField) scene.lookup("#NameFilter");
//        ImageView ClearFilter = (ImageView) scene.lookup("#ClearFilter");

    }


    public static void main(String[] args) {
        launch(args);
    }
}
