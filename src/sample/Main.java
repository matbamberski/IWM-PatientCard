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

    private MainView MainScene;
    private Stage Window;

    public void BackToMainView(){
        Window.setScene(MainScene);
    }

    public void setNewScene(Scene newScene){
        Window.setScene(newScene);
    }

    private static Main instance;

    public static Main getInstance(){
        return instance;
    }

    @Override
    public void start(Stage primaryStage){
        instance = this;
        Window = primaryStage;

        MainScene = new MainView(600,800);
//        primaryStage.setMaximized(true);
        Window.setTitle("Patient Card");
        Window.setScene(MainScene);
        Window.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}