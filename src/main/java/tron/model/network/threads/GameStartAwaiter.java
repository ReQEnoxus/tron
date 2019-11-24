package tron.model.network.threads;

import tron.model.GameFlow;
import tron.model.entity.CellType;
import tron.model.network.client.ClientSocketHandler;
import tron.model.network.messages.DataChunk;
import tron.model.network.messages.GameStartMessage;
import tron.model.network.messages.IntroduceClientsMessage;

public class GameStartAwaiter extends Thread {
    @Override
    public void run() {
        while (true) {
            Object serverData = ClientSocketHandler.getConnection().readObject();
            if (serverData instanceof DataChunk) {
                DataChunk dataChunk = ((DataChunk) serverData);
                GameFlow.getInstance().getGameField().setCell(dataChunk.getX(), dataChunk.getY(), CellType.valueOf("PLAYER" + dataChunk.getPlayerId()));
            } else if (serverData instanceof GameStartMessage) {
                break;
            } else if (serverData instanceof IntroduceClientsMessage) {
                ClientSocketHandler.getConnection().writeObject(new DataChunk(GameFlow.getInstance().getPlayer().getPlayerNumber(), GameFlow.getInstance().getPlayer().getCurrentPoint().getX(), GameFlow.getInstance().getPlayer().getCurrentPoint().getY(), false));
            }
        }
    }
}
