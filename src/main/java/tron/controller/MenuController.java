package tron.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import tron.controller.router.Router;
import tron.model.GameFlow;
import tron.model.UserSettings;
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
    public CheckBox bloomEnabledCheckBox;

    public void initialize() {
        contentPane.setStyle("-fx-background-color: #202020");
        logoImageView.setImage(new Image("/images/logo.png"));
        logoImageView.setFitHeight(360);
        logoImageView.setFitWidth(640);
        logoImageView.setX(-165);
        addressTextField.setVisible(false);
        enterButton.setVisible(false);
        errorLabel.setVisible(false);

        bloomEnabledCheckBox.setSelected(true);
        bloomEnabledCheckBox.setOnAction(evt -> UserSettings.bloomEnabled = bloomEnabledCheckBox.isSelected());
    }

    public void hostButtonPressed(ActionEvent actionEvent) throws IOException {
        Server server = new Server();
        ClientSocketHandler.openConnection("localhost", 55555);
        LoginResponse loginResponse = (LoginResponse) ClientSocketHandler.getConnection().readObject();
        GameFlow.getInstance().setPlayer(loginResponse.getPlayer());
        Router.goTo("game", scene -> scene.setOnKeyPressed(keyEvent -> GameFlow.getInstance().changePlayerDirection(keyEvent.getCode())));
        GameFlow.getInstance().startGame();
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
            GameFlow.getInstance().setPlayer(loginResponse.getPlayer());
            Router.goTo("game", scene -> scene.setOnKeyPressed(keyEvent -> GameFlow.getInstance().changePlayerDirection(keyEvent.getCode())));
            GameFlow.getInstance().startGame();
        } else {
            errorLabel.setText(loginResponse.getMessage());
            errorLabel.setVisible(true);
        }
    }
}
