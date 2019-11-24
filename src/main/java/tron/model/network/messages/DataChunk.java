package tron.model.network.messages;

import java.io.Serializable;

public class DataChunk implements Serializable {
    private int playerId;
    private int x;
    private int y;
    private boolean playerLost;

    public DataChunk(int playerId, int x, int y, boolean playerLost) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.playerLost = playerLost;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isPlayerLost() {
        return playerLost;
    }

    public void setPlayerLost(boolean playerLost) {
        this.playerLost = playerLost;
    }

    @Override
    public String toString() {
        return "DataChunk{" +
                "playerId=" + playerId +
                ", x=" + x +
                ", y=" + y +
                ", playerLost=" + playerLost +
                '}';
    }
}
