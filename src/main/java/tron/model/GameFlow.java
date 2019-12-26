package tron.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import tron.controller.router.Router;
import tron.model.entity.*;
import tron.model.helpers.PlayerHelper;
import tron.model.network.client.ClientSocketHandler;
import tron.model.network.messages.*;
import tron.model.network.threads.GameStartAwaiter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class GameFlow {
    private static GameFlow instance;
    private Player player;
    private Timeline gameLoop;
    private long speedFactor;
    private int noOfCellsInRow;
    private int noOfCellsInCol;

    // constantly updating
    private StringProperty timeText = new SimpleStringProperty("00:00");
    private StringProperty scoreText = new SimpleStringProperty("Ожидание других игроков");

    private AtomicBoolean onBreak = new AtomicBoolean(false);

    public String getTimeText() {
        return timeText.get();
    }

    public StringProperty timeTextProperty() {
        return timeText;
    }

    public String getScoreText() {
        return scoreText.get();
    }

    public StringProperty scoreTextProperty() {
        return scoreText;
    }

    public AtomicBoolean getOnBreak() {
        return onBreak;
    }

    public static GameFlow getInstance() {
        if (instance == null) {
            instance = new GameFlow();
        }
        return instance;
    }

    private GameFlow() {
        gameField = new Field(128, 60);
    }

    private Field gameField;

    public void setSpeedFactor(long speedFactor) {
        this.speedFactor = speedFactor;
    }

    public long getSpeedFactor() {
        return speedFactor;
    }

    public void setBounds(int rows, int cols) {
        noOfCellsInRow = rows;
        noOfCellsInCol = cols;
    }

    public void startGame() {
        setBounds(128, 60);
        setSpeedFactor(2);
        startGameLoop();
    }

    public void stopGame() {
        ClientSocketHandler.getConnection().writeObject(new GameExitRequest(player.getPlayerNumber()));
    }

    public void startGameLoop() {
        new Thread(() -> {
            Platform.runLater(() -> scoreText.setValue("Ожидание других игроков"));
            Platform.runLater(() -> timeText.setValue("00:00"));
            AtomicBoolean roundEnded = new AtomicBoolean(false);

            getPlayer().setDirection(PlayerHelper.getInitialDirection(getPlayer()));
            getPlayer().setLost(false);
            getPlayer().setCurrentPoint(PlayerHelper.getInitialPoint(getPlayer().getPlayerNumber()));

            Platform.runLater(() -> getGameField().setCell(getPlayer().getCurrentPoint().getX(), getPlayer().getCurrentPoint().getY(), CellType.valueOf(getPlayer().getName())));

            GameStartAwaiter gameStartWaiter = new GameStartAwaiter(); // thread that waits for the server to start the game

            gameStartWaiter.start();

            try {
                gameStartWaiter.join();
                if (!gameStartWaiter.getSuccessful().get()) {
                    return;
                }
                onBreak.set(false);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> scoreText.setValue("0:0:0:0"));
            Platform.runLater(() -> timeText.setValue("00:00"));

            gameLoop = new Timeline();
            gameLoop.setCycleCount(Timeline.INDEFINITE);

            AtomicLong timeStart = new AtomicLong(System.currentTimeMillis());
            AtomicLong loops = new AtomicLong(speedFactor);

            new Thread(() -> { // start getting data from server (during the game)
                while (true) {
                    DataChunk dataChunk;
                    Object serverData = ClientSocketHandler.getConnection().readObject();
                    if (serverData instanceof DataChunk) {
                        dataChunk = ((DataChunk) serverData);

                        if (!roundEnded.get()) {
                            getGameField().setCell(dataChunk.getX(), dataChunk.getY(), CellType.valueOf("PLAYER" + dataChunk.getPlayerId()));
                        }
                    } else if (serverData instanceof RoundEndMessage) {
                        roundEnded.set(true);
                        onBreak.set(true);

                        Platform.runLater(() -> timeText.setValue("Winner: " + ((RoundEndMessage) serverData).getWinner()));
                        Platform.runLater(() -> scoreText.setValue(((RoundEndMessage) serverData).getScores().stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(":"))));

                        getPlayer().setCurrentPoint(PlayerHelper.getInitialPoint(getPlayer().getPlayerNumber()));
                        getPlayer().setDirection(PlayerHelper.getInitialDirection(getPlayer()));
                        Platform.runLater(() -> getGameField().setCell(getPlayer().getCurrentPoint().getX(), getPlayer().getCurrentPoint().getY(), CellType.valueOf(getPlayer().getName())));
                    } else if (serverData instanceof RoundStartMessage) {

                        roundEnded.set(false);

                        getGameField().reset();

                        onBreak.set(false);
                        timeStart.set(System.currentTimeMillis());
                        getPlayer().setLost(false);
                    } else if (serverData instanceof IntroduceClientsMessage) {
                        ClientSocketHandler.getConnection().writeObject(new DataChunk(player.getPlayerNumber(), player.getCurrentPoint().getX(), player.getCurrentPoint().getY(), false));
                    } else if (serverData instanceof GameEndMessage) {
                        onBreak.set(true);


                        Platform.runLater(() -> scoreText.setValue(((GameEndMessage) serverData).getMessage()));
                        for (int i = 2; i > 0; i--) {
                            int finalI = i;
                            Platform.runLater(() -> timeText.setValue("Игра завершится через " + finalI + " секунд"));
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        gameLoop.stop();
                        ClientSocketHandler.closeConnection();
                        Platform.runLater(() -> Router.goTo("menu"));
                        break;
                    } else if (serverData instanceof GameExitResponse) {
                        ClientSocketHandler.closeConnection();
                        gameLoop.stop();
                        Platform.runLater(() -> Router.goTo("menu"));
                        break;
                    } else if (serverData instanceof PlayerLeftMessage) {
                        int rows = GameFlow.getInstance().getGameField().getField().length;
                        int cols = GameFlow.getInstance().getGameField().getField()[0].length;

                        Platform.runLater(() -> {
                            for (int i = 0; i < rows; i++) {
                                for (int j = 0; j < cols; j++) {
                                    if (GameFlow.getInstance().getGameField().getField()[i][j] == CellType.valueOf("PLAYER" + ((PlayerLeftMessage) serverData).getPlayerId())) {
                                        GameFlow.getInstance().getGameField().setCell(i, j, CellType.EMPTY);
                                    }
                                }
                            }
                        });
                    }
                }
            }).start();
            KeyFrame kf = new KeyFrame(Duration.seconds(0.017), // 60 FPS
                    ae -> {

                        if (!onBreak.get()) {
                            long millis = System.currentTimeMillis() - timeStart.get();
                            String time = String.format("%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes(millis),
                                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
                            );

                            Platform.runLater(() -> timeText.setValue(time));

                            if (!getPlayer().isLost()) {
                                if (loops.get() != 0) {
                                    loops.decrementAndGet();
                                } else {
                                    switch (getPlayer().getDirection()) {
                                        case DOWN:
                                            if (getPlayer().getCurrentPoint().getY() + 1 < noOfCellsInCol && getGameField().getField()[getPlayer().getCurrentPoint().getX()][getPlayer().getCurrentPoint().getY() + 1] == CellType.EMPTY) {
                                                getPlayer().setCurrentPoint(new Point(getPlayer().getCurrentPoint().getX(), getPlayer().getCurrentPoint().getY() + 1));
                                                Platform.runLater(() -> getGameField().setCell(getPlayer().getCurrentPoint().getX(), getPlayer().getCurrentPoint().getY(), CellType.valueOf("PLAYER" + getPlayer().getPlayerNumber())));
                                            } else {
                                                getPlayer().setLost(true);
                                            }
                                            break;
                                        case UP:
                                            if (getPlayer().getCurrentPoint().getY() - 1 > -1 && getGameField().getField()[getPlayer().getCurrentPoint().getX()][getPlayer().getCurrentPoint().getY() - 1] == CellType.EMPTY) {
                                                getPlayer().setCurrentPoint(new Point(getPlayer().getCurrentPoint().getX(), getPlayer().getCurrentPoint().getY() - 1));
                                                Platform.runLater(() -> getGameField().setCell(getPlayer().getCurrentPoint().getX(), getPlayer().getCurrentPoint().getY(), CellType.valueOf("PLAYER" + getPlayer().getPlayerNumber())));
                                            } else {
                                                getPlayer().setLost(true);
                                            }
                                            break;
                                        case LEFT:
                                            if (getPlayer().getCurrentPoint().getX() - 1 > -1 && getGameField().getField()[getPlayer().getCurrentPoint().getX() - 1][getPlayer().getCurrentPoint().getY()] == CellType.EMPTY) {
                                                getPlayer().setCurrentPoint(new Point(getPlayer().getCurrentPoint().getX() - 1, getPlayer().getCurrentPoint().getY()));
                                                Platform.runLater(() -> getGameField().setCell(getPlayer().getCurrentPoint().getX(), getPlayer().getCurrentPoint().getY(), CellType.valueOf("PLAYER" + getPlayer().getPlayerNumber())));
                                            } else {
                                                getPlayer().setLost(true);
                                            }
                                            break;
                                        case RIGHT:
                                            if (getPlayer().getCurrentPoint().getX() + 1 < noOfCellsInRow && getGameField().getField()[getPlayer().getCurrentPoint().getX() + 1][getPlayer().getCurrentPoint().getY()] == CellType.EMPTY) {
                                                getPlayer().setCurrentPoint(new Point(getPlayer().getCurrentPoint().getX() + 1, getPlayer().getCurrentPoint().getY()));
                                                Platform.runLater(() -> getGameField().setCell(getPlayer().getCurrentPoint().getX(), getPlayer().getCurrentPoint().getY(), CellType.valueOf("PLAYER" + getPlayer().getPlayerNumber())));
                                            } else {
                                                getPlayer().setLost(true);
                                            }
                                            break;
                                    }
                                    loops.set(speedFactor);
                                }
                            }

                            ClientSocketHandler.getConnection().writeObject(new DataChunk(getPlayer().getPlayerNumber(), getPlayer().getCurrentPoint().getX(), getPlayer().getCurrentPoint().getY(), getPlayer().isLost()));

                        }
                    }
            );

            gameLoop.getKeyFrames().add(kf);
            gameLoop.play();

        }).start();

    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Field getGameField() {
        return gameField;
    }

    public void setGameField(Field gameField) {
        this.gameField = gameField;
    }

    public void changePlayerDirection(KeyCode keyCode) {
        switch (keyCode) {
            case W:
                if (getPlayer().getDirection() != Direction.DOWN) {
                    getPlayer().setDirection(Direction.UP);
                }
                break;
            case A:
                if (getPlayer().getDirection() != Direction.RIGHT) {
                    getPlayer().setDirection(Direction.LEFT);
                }
                break;
            case D:
                if (getPlayer().getDirection() != Direction.LEFT) {
                    getPlayer().setDirection(Direction.RIGHT);
                }
                break;
            case S:
                if (getPlayer().getDirection() != Direction.UP) {
                    getPlayer().setDirection(Direction.DOWN);
                }
                break;
        }
    }
}
