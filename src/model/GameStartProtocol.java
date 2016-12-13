package model;

import java.io.*;
import java.net.*;

public class GameStartProtocol {
	private DataInputStream in;
	private DataOutputStream out;
	private GameList gameList;
	private UserList userList;
	private User user;
	private Socket socket;
	
	public GameStartProtocol(Socket socket, GameList gameList, User user, UserList userList) throws IOException {
		this.in = new DataInputStream(socket.getInputStream());
		this.out = new DataOutputStream(socket.getOutputStream());
		this.gameList = gameList;
		this.userList = userList;
		this.user = user;
		this.socket = socket;
	}
	
	protected void getRequest() throws IOException, InterruptedException {
		for (;;) {
			System.err.println("GameStartProtocol <" + this.user.getNickname() + " get request>");
			System.out.print("getRequest = ");
			String mode = this.in.readUTF();
			System.out.println(mode);
			if ("new game".equals(mode)) {
				this.newGame();
			} else if ("connect to game".equals(mode)) {
				this.connectToGame();
			} else if ("sign out".equals(mode)) {
				this.signOut();
				break;
			} else if ("get rating".equals(mode)) {
				this.getRating();
			} else {
				System.out.println(mode);
			}
		}
	}
	
	protected void newGame() throws IOException, InterruptedException {
		System.err.println("GameStartProtocol <new game>");
		GameModel gameModel = new GameModel(this.user, this.socket);
		this.gameList.add(gameModel);
		this.out.writeBoolean(true);
		boolean isStarted = gameModel.getWaitingResult();
		System.out.println("isStarted = " + isStarted);
		if (!isStarted) {
			gameList.remove(gameModel);
			gameModel = null;
		}
	}
	
	protected void connectToGame() throws IOException {
		System.err.println("GameStartProtocol <connect to game>");
		out.writeInt(this.gameList.getCount());
		for (int i = 0; i < this.gameList.getCount(); i++) {
			this.out.writeUTF(gameList.toString(i));
		}
		boolean isChoice = in.readBoolean();
		if (isChoice) {
			int id = this.in.readInt();
			System.err.println("GameStartProtocol <game id: " + id + ">");
			GameModel gameModel2 = gameList.getGameModel(id);
			if (gameModel2 != null) {
				System.err.println("GameStartProtocol <game started>");
				this.out.writeBoolean(true);
				gameModel2.connectToGame(this.user, this.socket);
				this.gameList.remove(gameModel2);
			} else {
				System.err.println("GameStartProtocol <impossible to connect>");
				this.out.writeBoolean(false);
			}
		}
	}
	
	protected void signOut() throws IOException {
		System.err.println("GameStartProtocol <sign out>");
		this.user.setOnline(false);
	}
	
	protected void getRating() throws IOException  {
		System.err.println("GameStartProtocol <get rating>");
		String ratingList = this.userList.getRatingList();
		this.out.writeUTF(ratingList);
	}
	
}