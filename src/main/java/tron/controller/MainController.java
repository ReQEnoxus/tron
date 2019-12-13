package tron.controller;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import tron.model.GameFlow;
import tron.model.UserSettings;
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
        gameGridPane.setScaleX(1.01);

        noOfCellsInRow = (int) gameGridPane.getMinWidth() / 10;
        noOfCellsInCol = (int) gameGridPane.getMinHeight() / 10;

        contentPane.setStyle("-fx-background-color: #232323");
        gameFlow.setGameField(new Field(noOfCellsInRow, noOfCellsInRow));

        Bloom bloom = new Bloom(0.2);
        if (UserSettings.bloomEnabled.get()) {
            gameGridPane.setEffect(bloom);
        }

        logoImageView.setImage(new Image("/images/logo_anim.gif"));

        for (int row = 0; row < noOfCellsInRow; row++) {
            for (int col = 0; col < noOfCellsInCol; col++) {
                Rectangle rect = new Rectangle();
                rect.setHeight(10);
                rect.setWidth(10);
                rect.fillProperty().bind(gameFlow.getGameField().getPaintProperties().get(row).get(col));

                gameGridPane.add(rect, row, col);
            }
        }

        timeLabel.setStyle("color: #FFF;");

        timeLabel.textProperty().bind(GameFlow.getInstance().timeTextProperty());
        scoreLabel.textProperty().bind(GameFlow.getInstance().scoreTextProperty());
    }

    public void exitButtonPressed(ActionEvent actionEvent) throws IOException {
        GameFlow.getInstance().stopGame();
    }
}
