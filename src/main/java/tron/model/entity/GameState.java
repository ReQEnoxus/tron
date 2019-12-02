package tron.model.entity;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameState {
    private int currentRound;
    private ArrayList<String> players;
    private ArrayList<String> playersActive;
    private ArrayList<Integer> scores;
    private AtomicBoolean gameStarted = new AtomicBoolean(false);

    public AtomicBoolean getGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(AtomicBoolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public GameState() {
        players = new ArrayList<>();
        playersActive = new ArrayList<>();
        scores = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            scores.add(0);
        }

        players.add("PLAYER1");
        players.add("PLAYER2");
        players.add("PLAYER3");
        players.add("PLAYER4");

        playersActive = new ArrayList<>(players);

        currentRound = 1;
    }

    public void resetActivePlayers() {
        playersActive = new ArrayList<>(players);
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

    public ArrayList<String> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<String> players) {
        this.players = players;
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
