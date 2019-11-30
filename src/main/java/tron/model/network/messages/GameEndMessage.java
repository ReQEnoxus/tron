package tron.model.network.messages;

import java.io.Serializable;

public class GameEndMessage implements Serializable {
    private String message;

    public GameEndMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "GameEndMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
