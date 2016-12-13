package model;

import java.net.*;
import java.io.*;

public class ConnectListener extends Thread {
	private boolean serverIsWork;
	private ServerSocket serverSocket;
	private Socket newClientSocket;
	private UserList userList;
	private GameList gameList;
	
	public ConnectListener(ServerSocket serverSocket, UserList userList, GameList gameList) {
		this.userList = userList;
		this.gameList = gameList;
		this.serverIsWork = true;
		this.serverSocket = serverSocket;
	}
		
	@Override
	public void run() {
		try {
			while (serverIsWork) {
				//try { Thread.sleep(100); } catch (InterruptedException e) {}
				System.err.println("ConnectListener <waiting...>");
				this.newClientSocket = serverSocket.accept();
				this.startUser(newClientSocket);
				System.err.println("ConnectListener <client connected>");
			}
		} catch (IOException e) {
			System.err.println("Server <IOException>");
			e.printStackTrace();
		}
	}
	
	public void startUser(Socket clientSocket) throws IOException {
		LoginProtocol loginProtocol = new LoginProtocol(clientSocket, this.userList, this.gameList);
		loginProtocol.start();
	}
	
	public void stopWork() throws IOException {
		serverIsWork = false;
		serverSocket.close();
		//this.interrupt();
	}
}