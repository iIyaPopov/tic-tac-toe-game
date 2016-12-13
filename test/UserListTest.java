package test;

import java.io.*;
import model.*;
import org.junit.*;
import static org.junit.Assert.*;

public class UserListTest {
    private UserList userList;
    
    @Before
    public void init() {
        this.userList = new UserList();
    }
    
    @After
    public void done() {
        this.userList = null;
    }
	
	@Test
	public void testAdd() {
		boolean ref;
		ref = this.userList.add(new User("Vova", "VovaPsw"));
		assertTrue(ref);
		ref = this.userList.add(null);
		assertFalse(ref);
	}
	
	@Test
	public void testRemove() {
		boolean ref;
		this.userList.add(new User("Vova", "VovaPsw"));
		ref = this.userList.remove(null);
		assertFalse(ref);
		ref = this.userList.remove(new User("Kolya", "KolyaPsw"));
		assertFalse(ref);
		ref = this.userList.remove(new User("Vova", "VovaPsw"));
		assertTrue(ref);
		
	}
	
	@Test
	public void testIsContainsNickname() {
		boolean ref;
		this.userList.add(new User("Vova", "VovaPsw"));
		ref = this.userList.isContainsNickname("");
		assertFalse(ref);
		ref = this.userList.isContainsNickname(null);
		assertFalse(ref);
		ref = this.userList.isContainsNickname("Vova");
		assertTrue(ref);
		ref = this.userList.isContainsNickname("VOVA");
		assertFalse(ref);
	}
	
	@Test
	public void testFind() {
		User ref;
		this.userList.add(new User("Bob", "BobPsw"));
		ref = this.userList.find("Bob");
		assertNotNull(ref);
		ref = this.userList.find(null);
		assertNull(ref);
		ref = this.userList.find("");
		assertNull(ref);
	}
	
	@Test
	public void testIsContains() {
		User ref;
		this.userList.add(new User("Vova", "VovaPsw"));
		ref = this.userList.isContains(new User("", ""));
		assertNull(ref);
		ref = this.userList.isContains(null);
		assertNull(ref);
		ref = this.userList.isContains(new User("Vova", "VovaPsw"));
		assertNotNull(ref);
		ref = this.userList.isContains(new User("VOVA", "VovaPsw"));
		assertNull(ref);
	}
	
    @Test
    public void testLoadCorrectFile() {
		try {
			boolean ref = this.userList.load("./test/correct_user_list.txt");
			assertTrue(ref);
		} catch (FileNotFoundException e) { e.printStackTrace(); }
	}
    
    @Test
    public void testLoadIncorrectFile() {
		try {
			boolean ref = this.userList.load("./test/incorrect_user_list.txt");
			assertFalse(ref);
		} catch (FileNotFoundException e) { e.printStackTrace(); }
	}
}
