package tron.model.network.server;

import tron.model.entity.GameState;
import tron.model.entity.Player;
import tron.model.helpers.PlayerHelper;
import tron.model.network.messages.*;

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

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        this.start();
    }

    private void introduceClients() {
        IntroduceClientsMessage introduceClientsMessage = new IntroduceClientsMessage();
        for (ClientHandler client : clients) {
            try {
                synchronized (client.out) {
                    client.out.writeObject(introduceClientsMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void introduceNewClient(Player player) {
        DataChunk dataChunk = new DataChunk(player.getPlayerNumber(), PlayerHelper.getInitialPoint(player).getX(), PlayerHelper.getInitialPoint(player).getY(), false);
        for (ClientHandler client : clients) {
            try {
                synchronized (client.out) {
                    client.out.writeObject(dataChunk);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendGameStartingMessage() {
        GameStartMessage gsm = new GameStartMessage();
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
    }

    private void sendRoundEndMessage(String winner) {
        RoundEndMessage roundEndMessage = new RoundEndMessage(winner, state.getScores());
        System.out.println("SENT TO CLIENTS: " + roundEndMessage);
        for (ClientHandler client : clients) {
            synchronized (client.out) {
                try {
                    client.out.writeObject(roundEndMessage);
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void run() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Started game server on port: " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (clients.size() < 4) {
                    new ClientHandler(serverSocket.accept()).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;

            Player player = new Player("PLAYER" + (clients.size() + 1));
            player.setPlayerNumber(clients.size() + 1);

            try {
                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();
                out.writeObject(new LoginResponse(true, player));
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            clients.add(this);
            System.out.println("Client" + clients.size() + " connected");

            introduceClients();
            System.out.println("Clients introduced");

            introduceNewClient(player);
            System.out.println("New connected player introduced");

            // Game has started at this point

            state = new GameState();

            if (clients.size() == 4) {
                sendGameStartingMessage();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    DataChunk dataChunk;
                    synchronized (in) {
                        dataChunk = ((DataChunk) in.readObject());
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

                        // send everyone round winning message and increment round


                        sendRoundEndMessage(winner);

                        introduceClients();

                        System.out.println("Round end message sent");

                        //introduceClients();

                        Thread.sleep(2000);

                        sendRoundStartMessage();
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
