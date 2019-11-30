package tron.model.network.messages;

import java.io.Serializable;

public class PlayerLeftMessage implements Serializable {
    private int playerId;

    public PlayerLeftMessage(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
