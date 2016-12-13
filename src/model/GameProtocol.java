package model;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameProtocol {
	private UserListener creatorListener;
	private UserListener connectorListener;
	private int[][] field;
	private int freeCells;
	private int time;
	private boolean isCreatorMove;
	private boolean isConnectorMove;
	private boolean tie;
	private boolean creatorWin;
	private boolean connectorWin;
	private Thread thread;
	
	public GameProtocol(User creator, Socket creatorSocket, User connector, Socket connectorSocket) throws IOException, InterruptedException {
	
		DataInputStream creatorIn = new DataInputStream(creatorSocket.getInputStream());
		DataOutputStream creatorOut = new DataOutputStream(creatorSocket.getOutputStream());
		DataInputStream connectorIn = new DataInputStream(connectorSocket.getInputStream());
		DataOutputStream connectorOut = new DataOutputStream(connectorSocket.getOutputStream());;
		
		isCreatorMove = true;
		isConnectorMove = false;
		tie = false;
		creatorWin = false;
		connectorWin = false;
			
		Random rand = new Random();
		/*int res = rand.nextInt(2);
		if (res == 1) {
			isCreatorMove = true;
		} else {
			isConnectorMove = true;
		}*/
		creatorListener = new UserListener(creatorIn, creatorOut, connectorIn, connectorOut, 1, isCreatorMove, creator);
		connectorListener = new UserListener(connectorIn, connectorOut, creatorIn, creatorOut, 2, isConnectorMove, connector);
		
		field = new int[10][10];
		freeCells = 100;
		time = 600;
		
		creatorListener.start();
		connectorListener.start();
		
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (;;) {
						Thread.sleep(1000);
						time--;
						if (time <= 0) {
							creatorOut.writeUTF("timeIsOver");
							connectorOut.writeUTF("timeIsOver");
							creatorListener.interrupt();
							connectorListener.interrupt();
							break;
						} else if (creatorWin || connectorWin) {
							creatorListener.interrupt();
							connectorListener.interrupt();
							break;
						} else if (freeCells == 0) {
							tie = true;
							creatorOut.writeUTF("tie");
							connectorOut.writeUTF("tie");
							creatorListener.interrupt();
							connectorListener.interrupt();
							break;
						} else if (!creator.isOnline()) {
							connectorOut.writeUTF("opponent disconnected");
							//creatorListener.interrupt();
							//connectorListener.interrupt();
							break;
						} else if (!connector.isOnline()) {
							creatorOut.writeUTF("opponent disconnected");
							//creatorListener.interrupt();
							//connectorListener.interrupt();
							break;
						}
					}
				} catch (InterruptedException e) { e.printStackTrace();
				} catch (IOException e) { e.printStackTrace(); }
			}
		});
		thread.start();
		thread.join();
		System.err.println("GameProtocol <stop>");
	}
	
	public boolean isCreatorWin() {
		return creatorWin;
	}
	
	public boolean isConnectorWin() {
		return connectorWin;
	}
	
	public boolean isTie() {
		return tie;
	}
	
	public int getFreeCells() {
		return freeCells;
	}
	
	private class UserListener extends Thread {
		private DataInputStream inPlayer;
		private DataOutputStream outPlayer;
		private DataInputStream inOpponent;
		private DataOutputStream outOpponent;
		private int number;
		private User user;
		
		public UserListener(DataInputStream inPlayer,
							DataOutputStream outPlayer,
							DataInputStream inOpponent,
							DataOutputStream outOpponent,
						   	int number,
							boolean isMove,
						   	User user) throws IOException {
			this.user = user;
			this.inPlayer = inPlayer;
			this.outPlayer = outPlayer;
			this.inOpponent = inOpponent;
			this.outOpponent = outOpponent;
			this.number = number;
			this.outPlayer.writeUTF("START GAME");
			this.outOpponent.writeUTF("OPPONENT IS READY");
			//outPlayer.writeBoolean(isMove);
		}
		
		@Override
		public void run() {
			try {
				for (;;) {
					String mode = inPlayer.readUTF();
					System.out.println(mode);
					if ("break".equals(mode)) {
						break;
					}
					if ("message".equals(mode)) {
						String message = inPlayer.readUTF();
						outOpponent.writeUTF(mode);
						outOpponent.writeUTF(message);
					} else if ("coordinate".equals(mode)) {
						int x = inPlayer.readInt();
						int y = inPlayer.readInt();
						System.err.println("GameProtocol <" + number + " move (" + x + "," + y + ")");
						outPlayer.writeUTF("answerToCanPut");
						if (number == 1 && isCreatorMove) {
							if (field[x][y] == 0) {
								field[x][y] = 1;
								freeCells--;
								isCreatorMove = false;
								isConnectorMove = true;
								outPlayer.writeBoolean(true);
								boolean isWin = winCheck(number, x, y);
								if (isWin) {
									creatorWin = true;
								}
								outPlayer.writeBoolean(isWin);
								outOpponent.writeUTF("coordinate");
								outOpponent.writeInt(x);
								outOpponent.writeInt(y);
								outOpponent.writeBoolean(isWin);
								if (creatorWin || connectorWin) {
									break;
								}
							} else {
								outPlayer.writeBoolean(false);
							}
						} else if (number == 2 && isConnectorMove) {
							if (field[x][y] == 0) {
								field[x][y] = 2;
								freeCells--;
								isConnectorMove = false;
								isCreatorMove = true;
								outPlayer.writeBoolean(true);
								boolean isWin = winCheck(number, x, y);
								if (isWin) {
									connectorWin = true;
								}
								outPlayer.writeBoolean(isWin);
								outOpponent.writeUTF("coordinate");
								outOpponent.writeInt(x);
								outOpponent.writeInt(y);
								outOpponent.writeBoolean(isWin);
								if (creatorWin || connectorWin) {
									break;
								}
							} else {
								outPlayer.writeBoolean(false);
							}
						} else {
							outPlayer.writeBoolean(false);
						}
					}
				}
			} catch (IOException e) {
				System.err.println("GameProtocol <IOException> " + user.getNickname());
				user.setOnline(false);
				e.printStackTrace();
			}
		}
	}
	
	private boolean winCheck(int num, int x, int y) {
		int max = 5;
		int up = 0;
		int down = 0;
		int left = 0;
		int right = 0;
		int up_left = 0;
		int up_right = 0;
		int down_left = 0;
		int down_right = 0;
		for (int i = 1; i < max; i++) {
			if (y - i >= 0 && field[x][y-i] == num) up++;
			else break;
		}
		for (int i = 1; i < max; i++) {
			if (y + i < Constants.FIELD_HEIGHT && field[x][y+i] == num) down++;
			else break;
		}
		for (int i = 1; i < max; i++) {
			if (x - i >= 0 && field[x-i][y] == num) left++;
			else break;
		}
		for (int i = 1; i < max; i++) {
			if (x + i < Constants.FIELD_WIDTH && field[x+i][y] == num) right ++;
			else break;
		}
		for (int i = 1; i < max; i++) {
			if (x - i >= 0 && y - i >= 0 && field[x-i][y-i] == num) up_left++;
			else break;
		}
		for (int i = 1; i < max; i++) {
			if (y - i >= 0 && x + i < Constants.FIELD_WIDTH && field[x+i][y-i] == num) up_right++;
			else break;
		}
		for (int i = 1; i < max; i++) {
			if (x - i >= 0 && y + i < Constants.FIELD_HEIGHT && field[x-i][y+i] == num) down_left++;
			else break;
		}
		for (int i = 1; i < max; i++) {
			if (x + i < Constants.FIELD_WIDTH && y + i < Constants.FIELD_HEIGHT && field[x+i][y+i] == num) down_right++;
			else break;
		}
		if (up + down + 1 >= max)
			return true;
		if (left + right + 1 >= max)
			return true;
		if (up_left + down_right + 1 >= max)
			return true;
		if (up_right + down_left + 1 >= max)
			return true;
		return false;
	}
}