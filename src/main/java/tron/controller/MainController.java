package tron.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import tron.model.GameFlow;
import tron.model.entity.Field;

import java.io.IOException;

public class MainController {
    public Pane contentPane;
    public GridPane gameGridPane;
    public ImageView logoImageView;
    public Label timeLabel;
    public Label scoreLabel;
    public AnchorPane anchorPane;
    public Button exitButton;

    private GameFlow gameFlow = GameFlow.getInstance();

    private int noOfCellsInRow;
    private int noOfCellsInCol;

    public void initialize() {
        gameGridPane.setPadding(new Insets(0));
        gameGridPane.setHgap(0);
        gameGridPane.setVgap(0);

        gameGridPane.setMinSize(1280, 600);
        gameGridPane.setGridLinesVisible(false);

        noOfCellsInRow = (int) gameGridPane.getMinWidth() / 10;
        noOfCellsInCol = (int) gameGridPane.getMinHeight() / 10;

        contentPane.setStyle("-fx-background-color: #202020");
        gameFlow.setGameField(new Field(noOfCellsInRow, noOfCellsInRow));


        for (int row = 0; row < noOfCellsInRow; row++) {
            //ColumnConstraints colConst = new ColumnConstraints(gameGridPane.getMinWidth() / noOfCellsInRow);
            //gameGridPane.getColumnConstraints().add(colConst);

            for (int col = 0; col < noOfCellsInCol; col++) {
                //RowConstraints rowConst = new RowConstraints(gameGridPane.getMinHeight() / noOfCellsInCol);
                //gameGridPane.getRowConstraints().add(rowConst);

                Rectangle rect = new Rectangle();
                rect.setHeight(10);
                rect.setWidth(10);
                rect.fillProperty().bind(gameFlow.getGameField().getPaintProperties().get(row).get(col));

                gameGridPane.add(rect, row, col);
            }
        }

        timeLabel.setStyle("color: #FFF;");

        timeLabel.setText("00:00");
        scoreLabel.setText("Ожидание других игроков");
    }

    public void startGameLoop() {
        gameFlow.setBounds(noOfCellsInRow, noOfCellsInCol);
        gameFlow.setTimeLabel(timeLabel);
        gameFlow.setScoreLabel(scoreLabel);
        gameFlow.setSpeedFactor(2);

        gameFlow.startGameLoop();
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        gameFlow.changePlayerDirection(keyEvent.getCode());
    }

    public void exitButtonPressed(ActionEvent actionEvent) throws IOException {
        // TODO: 24.11.2019 exit from game
        //gameLoop.stop();
        Stage stage = ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/startMenu.fxml"));
        Parent newScene = loader.load();

        Scene scene = new Scene(newScene);
        stage.setScene(scene);
        stage.show();
    }
}
