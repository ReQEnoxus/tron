package tron.model.network.messages;

import java.io.Serializable;

public class GameStartMessage implements Serializable {
    private int timeOut;

    public GameStartMessage(int timeOut) {
        this.timeOut = timeOut;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }
}
