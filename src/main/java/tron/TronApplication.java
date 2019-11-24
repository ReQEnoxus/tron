package tron;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TronApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/startMenu.fxml"));
        primaryStage.setTitle("Tron");
        Scene menuScene = new Scene(root, 1280, 720);
        primaryStage.setScene(menuScene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
