package tron.model.network.threads;

import javafx.application.Platform;
import tron.controller.router.Router;
import tron.model.GameFlow;
import tron.model.entity.CellType;
import tron.model.network.client.ClientSocketHandler;
import tron.model.network.messages.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameStartAwaiter extends Thread {
    private AtomicBoolean successful = new AtomicBoolean(true);

    public AtomicBoolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(AtomicBoolean successful) {
        this.successful = successful;
    }

    @Override
    public void run() {
        while (true) {
            Object serverData = ClientSocketHandler.getConnection().readObject();
            if (serverData instanceof DataChunk) {
                DataChunk dataChunk = ((DataChunk) serverData);
                GameFlow.getInstance().getGameField().setCell(dataChunk.getX(), dataChunk.getY(), CellType.valueOf("PLAYER" + dataChunk.getPlayerId()));
            } else if (serverData instanceof GameStartMessage) {
                if (((GameStartMessage) serverData).getTimeOut() == 0) {
                    break;
                } else {
                    Platform.runLater(() -> GameFlow.getInstance().scoreTextProperty().setValue("Игра начнется через " + ((GameStartMessage) serverData).getTimeOut() + " секунд"));
                }
            } else if (serverData instanceof IntroduceClientsMessage) {
                ClientSocketHandler.getConnection().writeObject(new DataChunk(GameFlow.getInstance().getPlayer().getPlayerNumber(), GameFlow.getInstance().getPlayer().getCurrentPoint().getX(), GameFlow.getInstance().getPlayer().getCurrentPoint().getY(), false));
            } else if (serverData instanceof PlayerLeftMessage) {
                int rows = GameFlow.getInstance().getGameField().getField().length;
                int cols = GameFlow.getInstance().getGameField().getField()[0].length;

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        if (GameFlow.getInstance().getGameField().getField()[i][j] == CellType.valueOf("PLAYER" + ((PlayerLeftMessage) serverData).getPlayerId())) {
                            GameFlow.getInstance().getGameField().setCell(i, j, CellType.EMPTY);
                        }
                    }
                }
            } else if (serverData instanceof GameExitResponse || serverData instanceof GameEndMessage) {
                successful.set(false);
                ClientSocketHandler.closeConnection();
                Platform.runLater(() -> Router.goTo("menu"));
                break;
            }
        }
    }
}
