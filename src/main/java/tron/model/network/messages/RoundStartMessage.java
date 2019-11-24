package tron.model.network.messages;

import java.io.Serializable;

public class RoundStartMessage implements Serializable {
    private int roundNumber;

    public RoundStartMessage(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }
}
