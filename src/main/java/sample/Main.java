package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private MainView MainScene;
    private Stage Window;
    private DataContext dataContext;

    public DataContext getDataContext() {
        return dataContext;
    }

    public MainView getMainScene() {
        return MainScene;
    }

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
        dataContext = new DataContext();
        instance = this;
        Window = primaryStage;

        MainScene = new MainView(600,500);
//        primaryStage.setMaximized(true);
        Window.setTitle("Patient Card");
        Window.setScene(MainScene);
        Window.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
