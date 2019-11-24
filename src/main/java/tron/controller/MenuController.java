package tron.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tron.model.GameFlow;
import tron.model.network.client.ClientSocketHandler;
import tron.model.network.messages.LoginResponse;
import tron.model.network.server.Server;

import java.io.IOException;

public class MenuController {
    public Pane contentPane;
    public ImageView logoImageView;
    public Button hostGameButton;
    public Button connectButton;
    public TextField addressTextField;
    public Button enterButton;
    public Label errorLabel;

    public void initialize() {
        contentPane.setStyle("-fx-background-color: #202020");
        addressTextField.setVisible(false);
        enterButton.setVisible(false);
        errorLabel.setVisible(false);
    }

    public void hostButtonPressed(ActionEvent actionEvent) throws IOException {
        Server server = new Server();
        Stage stage = ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent newScene = loader.load();
        Scene scene = new Scene(newScene);
        stage.setScene(scene);
        MainController controller = loader.getController();
        System.out.println("here");

        ClientSocketHandler.openConnection("localhost", 55555);
        LoginResponse loginResponse = (LoginResponse) ClientSocketHandler.getConnection().readObject();

        GameFlow.getInstance().setPlayer(loginResponse.getPlayer());

        scene.setOnKeyPressed(controller::handleKeyPressed);
        stage.show();
        controller.startGameLoop();
        //GameFlow.getInstance().startNewGame();
    }

    public void connectButtonPressed(ActionEvent actionEvent) {
        addressTextField.setVisible(true);
        enterButton.setVisible(true);
    }

    public void enterButtonPressed(ActionEvent actionEvent) {
        String destination = addressTextField.getText();

        ClientSocketHandler.openConnection(destination, 55555);
        LoginResponse loginResponse = null;

        try {
            loginResponse = (LoginResponse) ClientSocketHandler.getConnection().readObject();
        } catch (NullPointerException e) {
            errorLabel.setVisible(true);
            System.out.println("cannot connect to server");
            return;
        }

        if (loginResponse.isSuccess()) {
            Stage stage = ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent newScene = null;
            try {
                newScene = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(newScene);
            stage.setScene(scene);
            MainController controller = loader.getController();
            GameFlow.getInstance().setPlayer(loginResponse.getPlayer());
            scene.setOnKeyPressed(controller::handleKeyPressed);
            stage.show();
            controller.startGameLoop();
        } else {
            // refused
        }

    }
}
