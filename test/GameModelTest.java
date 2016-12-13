package test;

import java.io.*;
import java.net.*;
import model.*;
import org.junit.*;
import static org.junit.Assert.*;

public class GameModelTest {
    private GameModel gameModel;
    private ServerSocket server;
    private Socket creator;
    private Socket connector;
    
    @Before
    public void init() {
        try {
            server = new ServerSocket(10000);
            creator = new Socket(InetAddress.getByName("127.0.0.1"), 10000);
            connector = new Socket(InetAddress.getByName("127.0.0.1"), 10000);
        } catch (UnknownHostException e) { e.printStackTrace();
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    @After
    public void done() {
        this.gameModel = null;
        this.creator = null;
        this.connector = null;
    }
    
    @Test
    public void testCanStart() {
        try {
            boolean ref;
            this.gameModel = new GameModel(new User("Bob", "BobPsw"), creator);
            ref = this.gameModel.canStart();
            assertFalse(ref);
			this.gameModel.connectToGame(null, connector);
			assertFalse(ref);
			this.gameModel.connectToGame(new User("Alisa", "AlisaPsw"), null);
			assertFalse(ref);
			this.gameModel.connectToGame(null, null);
			assertFalse(ref);
        } catch (IOException e) { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }
}
