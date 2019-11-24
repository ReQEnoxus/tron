package tron.model.entity;

import java.util.ArrayList;

public class GameState {
    private int currentRound;
    private ArrayList<String> playersActive;
    private ArrayList<Integer> scores;

    public GameState() {
        playersActive = new ArrayList<>();
        scores = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            scores.add(0);
        }

        playersActive.add("PLAYER1");
        playersActive.add("PLAYER2");
        playersActive.add("PLAYER3");
        playersActive.add("PLAYER4");

        currentRound = 1;
    }

    public void resetActivePlayers() {
        playersActive.clear();
        playersActive.add("PLAYER1");
        playersActive.add("PLAYER2");
        playersActive.add("PLAYER3");
        playersActive.add("PLAYER4");
    }

    public void endRound(String winner) {
        int winnerIndex = Integer.parseInt(winner.substring(winner.length() - 1)) - 1;
        scores.set(winnerIndex, scores.get(winnerIndex) + 1);
        resetActivePlayers();
        currentRound++;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public ArrayList<String> getPlayersActive() {
        return playersActive;
    }

    public void setPlayersActive(ArrayList<String> playersActive) {
        this.playersActive = playersActive;
    }

    public ArrayList<Integer> getScores() {
        return scores;
    }

    public void setScores(ArrayList<Integer> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "currentRound=" + currentRound +
                ", playersActive=" + playersActive +
                ", scores=" + scores +
                '}';
    }
}
