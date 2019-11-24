package tron.model.network.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class ClientSocketHandler {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private static ClientSocketHandler connection;

    public static ClientSocketHandler getConnection() {
        return connection;
    }

    private ClientSocketHandler(Socket socket) {
        this.socket = socket;

        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.flush();

            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openConnection(String host, int port) {
        if (connection == null) {
            try {
                connection = new ClientSocketHandler(new Socket(host, port));
            } catch (IOException e) {
                System.out.println("Connection exception happened");
            }
        } else {
            throw new IllegalStateException("Connection is already opened");
        }
    }

    public void writeObject(Serializable object) {
        try {
            synchronized (objectOutputStream) {
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
            }
        } catch (IOException e) {
            throw new IllegalStateException("IOException was thrown: " + e.getMessage());
        }
    }

    public Object readObject() {
        try {
            return objectInputStream.readObject();
        } catch (IOException e) {
            throw new IllegalStateException("IOException was thrown: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("ClassNotFoundException was thrown: " + e.getMessage());
        }
    }

}
