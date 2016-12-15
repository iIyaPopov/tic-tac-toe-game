package model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Server {
	private ServerSocket serverSocket;
	private ConnectListener connectListener;
	private UserList userList;
	private GameList gameList;
	private JFrame frame;
	private JButton stopServerButton;
	
	public Server() {
		this.gameList = new GameList();
		this.frame = new JFrame("Server");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setBounds(500, 200, 230, 100);
		this.frame.setLayout(null);
		this.frame.setResizable(false);
		
		this.stopServerButton = new JButton("Stop server");
		this.stopServerButton.setBounds(30, 20, 130, 30);
		this.stopServerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					stop();
					frame.setVisible(false);
					System.exit(1);
				} catch (IOException err) { err.printStackTrace(); }
			}
		});
		
		this.frame.getContentPane().add(this.stopServerButton);
	}
	
	public boolean loadUserList(String filename) throws FileNotFoundException {
		this.userList = new UserList();
		boolean ref = this.userList.load(filename);
		return ref;
	}
	
	public void start(int port) throws IOException, InterruptedException {
		this.serverSocket = new ServerSocket(port);
		this.connectListener = new ConnectListener(serverSocket, userList, gameList);
		System.err.println("Server <start connect listener>");
		this.connectListener.start();
		this.frame.setVisible(true);
		this.connectListener.join();
	}
	
	public void stop() throws IOException {
		this.connectListener.stopWork();
	}
	
	public UserList getUserList() {
		return this.userList;
	}
	
	public static void main(String[] args) {
		try {
			System.setErr(new PrintStream(new File(Constants.LOG_FILE_DIR)));
			System.err.println("Server <start>");
			
			Server server = new Server();
			System.err.println("Server <load user list from: " + Constants.USER_LIST_DIR + ">");
			server.loadUserList(Constants.USER_LIST_DIR);
			server.start(Constants.PORT);
			
			
			System.err.println("Server <stop>");
		} catch (IOException e) {
			System.err.println("Server <IOException>");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.err.println("Server <InterruptedException>");
			e.printStackTrace();
		}
	}
}