package tron.model.network.messages;

import java.io.Serializable;
import java.util.ArrayList;

public class RoundEndMessage implements Serializable {
    private String winner;
    private ArrayList<Integer> scores;

    private static final long serialVersionUID = 1L;

    public RoundEndMessage(String winner, ArrayList<Integer> scores) {
        this.winner = winner;
        this.scores = new ArrayList<>(scores);
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public ArrayList<Integer> getScores() {
        return scores;
    }

    public void setScores(ArrayList<Integer> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "RoundEndMessage{" +
                "winner='" + winner + '\'' +
                ", scores=" + scores +
                '}';
    }
}
