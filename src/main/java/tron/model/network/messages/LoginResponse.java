package tron.model.network.messages;

import tron.model.entity.Player;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private boolean success;
    private Player player;
    private String message;

    public LoginResponse(boolean success, Player player) {
        this.success = success;
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
