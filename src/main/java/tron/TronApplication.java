package tron;

import javafx.application.Application;
import javafx.stage.Stage;
import tron.controller.router.Router;

public class TronApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Router.init(primaryStage);
        Router.bind("menu", "/fxml/startMenu.fxml");
        Router.bind("game", "/fxml/main.fxml");
        Router.goTo("menu");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
