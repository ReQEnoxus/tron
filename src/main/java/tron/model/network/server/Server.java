package tron.model.network.server;

import tron.model.entity.GameState;
import tron.model.entity.Player;
import tron.model.helpers.PlayerHelper;
import tron.model.network.messages.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server extends Thread {
    private final int PORT = 55555;
    private List<ClientHandler> clients;
    private GameState state;
    ServerSocket serverSocket;

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        state = new GameState();
        this.start();
    }

    private void introduceClients() {
        IntroduceClientsMessage introduceClientsMessage = new IntroduceClientsMessage();
        for (ClientHandler client : clients) {
            try {
                synchronized (client.out) {
                    client.out.writeObject(introduceClientsMessage);
                    client.out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void introduceNewClient(Player player) {
        DataChunk dataChunk = new DataChunk(player.getPlayerNumber(), PlayerHelper.getInitialPoint(player.getPlayerNumber()).getX(), PlayerHelper.getInitialPoint(player.getPlayerNumber()).getY(), false);
        for (ClientHandler client : clients) {
            try {
                synchronized (client.out) {
                    client.out.writeObject(dataChunk);
                    client.out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendGameStartingMessage() {
        for (int i = 2; i >= 0; i--) {
            GameStartMessage gsm = new GameStartMessage(i);
            for (ClientHandler client : clients) {
                try {
                    synchronized (client.out) {
                        client.out.writeObject(gsm);
                        client.out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendRoundEndMessage(String winner) {
        RoundEndMessage roundEndMessage = new RoundEndMessage(winner, state.getScores());

        for (ClientHandler client : clients) {
            synchronized (client.out) {
                try {
                    client.out.writeObject(roundEndMessage);
                    client.out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendRoundStartMessage() {
        RoundStartMessage roundStartMessage = new RoundStartMessage(state.getCurrentRound());
        for (ClientHandler client : clients) {
            synchronized (client.out) {
                try {
                    client.out.writeObject(roundStartMessage);
                    client.out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendGameEndMessage(boolean forced) {
        String message;
        if (!forced) {
            message = "Player" + (state.getScores().indexOf(state.getScores().stream().mapToInt(e -> e).max().getAsInt()) + 1) + " победил!";
        } else {
            message = "Хост завершил игру";
        }

        GameEndMessage gameEndMessage = new GameEndMessage(message);

        for (ClientHandler client : clients) {
            synchronized (client.out) {
                try {
                    client.out.writeObject(gameEndMessage);
                    client.out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        clients.clear();
        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void sendPlayerLeftMessage(int playerId) {
        PlayerLeftMessage playerLeftMessage = new PlayerLeftMessage(playerId);

        for (ClientHandler client : clients) {
            synchronized (client.out) {
                try {
                    client.out.writeObject(playerLeftMessage);
                    client.out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void run() {
        serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (!serverSocket.isClosed()) {
                    new ClientHandler(serverSocket.accept()).start();
                } else if (serverSocket.isClosed()) {
                    break;
                }
            } catch (IOException e) {

                //e.printStackTrace();
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private boolean authSuccess = true;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;

            Player player = new Player("PLAYER" + (clients.size() + 1));
            player.setPlayerNumber(clients.size() + 1);

            try {
                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();
                if (clients.size() < 4 && !state.getGameStarted().get()) {
                    out.writeObject(new LoginResponse(true, player));
                } else if (clients.size() >= 4) {
                    LoginResponse response = new LoginResponse(false, null);
                    response.setMessage("Сервер переполнен");
                    out.writeObject(response);
                    authSuccess = false;
                    return;
                } else if (state.getGameStarted().get()) {
                    LoginResponse response = new LoginResponse(false, null);
                    response.setMessage("Игра уже идет");
                    out.writeObject(response);
                    authSuccess = false;
                    return;
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            clients.add(this);


            introduceClients();


            introduceNewClient(player);


            // Game has started at this point

            if (clients.size() == 4) {
                state.getGameStarted().set(true);
                sendGameStartingMessage();
            }
        }

        @Override
        public void run() {
            if (!authSuccess) {
                return;
            }
            try {
                while (true) {

                    Object serverData;
                    DataChunk dataChunk = null;
                    synchronized (in) {
                        try {
                            serverData = in.readObject();
                            if (serverData instanceof DataChunk) {
                                dataChunk = ((DataChunk) serverData);
                            } else if (serverData instanceof GameExitRequest) {
                                if (((GameExitRequest) serverData).getPlayerId() != 1) {
                                    synchronized (out) {
                                        out.writeObject(new GameExitResponse(((GameExitRequest) serverData).getPlayerId()));
                                        out.flush();
                                    }
                                    clients.remove(this);
                                    state.getPlayers().remove("PLAYER" + ((GameExitRequest) serverData).getPlayerId());
                                    state.getPlayersActive().remove("PLAYER" + ((GameExitRequest) serverData).getPlayerId());
                                    sendPlayerLeftMessage(((GameExitRequest) serverData).getPlayerId());
                                    break;
                                } else {
                                    state.getGameStarted().set(false);
                                    sendGameEndMessage(true);
                                    break;
                                }
                            }
                        } catch (EOFException e) {
                            break;
                        }
                    }
                    if (dataChunk.isPlayerLost()) {
                        state.getPlayersActive().remove("PLAYER" + dataChunk.getPlayerId());
                    }

                    for (ClientHandler client : clients) {
                        synchronized (client.out) {
                            client.out.writeObject(dataChunk);
                            client.out.flush();
                        }
                    }

                    if (state.getPlayersActive().size() == 1) {
                        String winner = state.getPlayersActive().get(0);
                        state.endRound(winner);

                        sendRoundEndMessage(winner);

                        introduceClients();


                        Thread.sleep(2000);

                        if (state.getScores().stream().mapToInt(e -> e).max().getAsInt() == 10) {
                            state.getGameStarted().set(false);
                            sendGameEndMessage(false);
                            break;
                        } else {
                            sendRoundStartMessage();
                        }
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
