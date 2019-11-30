package tron.controller.router;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;

public class Router {
    private HashMap<String, String> routes;
    private static Stage mainStage;

    private Router() {
        routes = new HashMap<>();
        mainStage = new Stage();
    }

    private static Router instance = new Router();

    public static void bind(String name, String path) {
        instance.routes.put(name, path);
    }

    public static void goTo(String destination) {
        try {
            Parent root = FXMLLoader.load(Router.class.getResource(instance.routes.get(destination)));
            mainStage.setTitle("Tron - " + destination);
            Scene scene = new Scene(root, 1280, 720);
            mainStage.setScene(scene);
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void goTo(String destination, Consumer<Scene> additionalSetup) {
        try {
            Parent root = FXMLLoader.load(Router.class.getResource(instance.routes.get(destination)));
            mainStage.setTitle("Tron - " + destination);
            Scene scene = new Scene(root, 1280, 720);
            additionalSetup.accept(scene);
            mainStage.setScene(scene);
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
