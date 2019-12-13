import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import tron.model.network.client.ClientSocketHandler;
import tron.model.network.messages.IntroduceClientsMessage;
import tron.model.network.messages.LoginResponse;
import tron.model.network.server.Server;

public class ServerTests {
    private static Object response;

    @BeforeClass
    public static void openConnection() {
        new Server();
        ClientSocketHandler.openConnection("localhost", 55555);
        response = ClientSocketHandler.getConnection().readObject();
    }

    @Test
    public void serverShouldReturnLoginResponseOnConnect() {
        Assert.assertTrue(response instanceof LoginResponse);
    }

    @Test
    public void firstPlayerShouldBeAbleToConnect() {
        Assert.assertTrue(((LoginResponse) response).isSuccess());
    }

    @Test
    public void playerShouldObtainFirstNumberIfConnectedFirst() {
        Assert.assertEquals(((LoginResponse) response).getPlayer().getPlayerNumber(), 1);
    }

    @Test
    public void playerShouldReceiveIntroduceClientsMessageAfterLogin() {
        Assert.assertTrue(ClientSocketHandler.getConnection().readObject() instanceof IntroduceClientsMessage);
    }


    @AfterClass
    public static void closeConnection() {
        ClientSocketHandler.closeConnection();

    }
}
