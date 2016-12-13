package test;

import java.io.*;
import model.*;
import org.junit.*;
import static org.junit.Assert.*;

public class ServerTest {
    private Server server;
    
    @Before
    public void init() {
        this.server = new Server();
    }
    
    @After
    public void done() {
        this.server = null;
    }
	
    @Test
    public void testLoadCorrectUserList() {
		try {
			boolean ref = this.server.loadUserList("./test/correct_user_list.txt");
			assertTrue(ref);
		} catch (FileNotFoundException e) { e.printStackTrace(); }
	}
    
    @Test
    public void testLoadIncorrectUserList() {
		try {
			boolean ref = this.server.loadUserList("./test/incorrect_user_list.txt");
			assertFalse(ref);
		} catch (FileNotFoundException e) { e.printStackTrace(); }
	}
}
