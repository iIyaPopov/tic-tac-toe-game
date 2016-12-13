package model;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameModel extends Thread {
	private static int id = 1;
	private int gameID;
	private User creator;
	private User connector;
	private boolean isCreatorMove;
	private boolean isConnectorMove;
	private Socket creatorSocket;
	private Socket connectorSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private int[][] field;
	private boolean isInterruptWaiting;
	private Thread opponentWaiting;
	private boolean isEnded;
	private GameProtocol gameProtocol;
	
	public GameModel(User creator, Socket creatorSocket) throws IOException, InterruptedException {
		this.gameID = id++;
		this.field = new int[Constants.FIELD_WIDTH][Constants.FIELD_HEIGHT];
		this.isEnded = false;
		
		this.isCreatorMove = false;
		this.isConnectorMove = false;
		
		if (creator != null && creatorSocket != null) {
			this.creator = creator;
			this.creatorSocket = creatorSocket;
			this.in = new DataInputStream(creatorSocket.getInputStream());
			this.out = new DataOutputStream(creatorSocket.getOutputStream());
		}
	}
	
	protected boolean getWaitingResult() throws InterruptedException {
		this.isInterruptWaiting = false;
		
		Thread interruptWaiting = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(isInterruptWaiting);
					isInterruptWaiting = in.readBoolean();
				} catch (IOException e) {
					//закрытие окна ожидания
					creator.setOnline(false);
					isInterruptWaiting = true;
				}
			}
		});
		
		opponentWaiting = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					/*ожидание противника*/
					System.err.println("GameModel <opponent waiting>");
					for (;;) {
						Thread.sleep(100);
						if (canStart()) {
							System.err.println("GameModel <opponent connected>");
							out.writeBoolean(true);
							gameProtocol = new GameProtocol(creator, creatorSocket,
															connector, connectorSocket);
							ratingRecount();
							System.err.println("GameModel <end>");
							if (!creator.isOnline() || !connector.isOnline()) {
								isInterruptWaiting = true;
							}
							break;
						}
						if (isInterruptWaiting) {
							/*подтвердить об отмене*/
							System.err.println("GameModel <stop opponent waiting>");
							out.writeBoolean(false);
							break;
						}
					}
					isEnded = true;
				} catch (IOException e) {
					System.err.println("GameModel <IOException opponent waiting>");
					e.printStackTrace();
					isInterruptWaiting = true;
					isEnded = true;
				} catch (InterruptedException e) {
					System.err.println("GameModel <InterruptedException opponent waiting>");
					e.printStackTrace();
					isInterruptWaiting = true;
					isEnded = true;
				}
			}
		});
		
		interruptWaiting.start();
		opponentWaiting.start();
		opponentWaiting.join();
		return !this.isInterruptWaiting;
	}
	
	protected void ratingRecount() {
		int newCreatorRating = creator.getRating();
		int newConnectorRating = connector.getRating();
		if (gameProtocol.isCreatorWin()) {
			newCreatorRating = EloRating.toCount(this.creator.getRating(), this.connector.getRating(),
												 EloRating.WIN, this.creator.getGamesCount());
			newConnectorRating = EloRating.toCount(this.connector.getRating(), this.creator.getRating(),
												   EloRating.LOSS, this.connector.getGamesCount());
		} else if (gameProtocol.isConnectorWin()) {
			newCreatorRating = EloRating.toCount(this.creator.getRating(), this.connector.getRating(),
												 EloRating.LOSS, this.creator.getGamesCount());
			newConnectorRating = EloRating.toCount(this.connector.getRating(), this.creator.getRating(),
												   EloRating.WIN, this.connector.getGamesCount());
		} else if (gameProtocol.isTie()) {
			newCreatorRating = EloRating.toCount(this.creator.getRating(), this.connector.getRating(),
												 EloRating.TIE, this.creator.getGamesCount());
			newConnectorRating = EloRating.toCount(this.connector.getRating(), this.creator.getRating(),
												   EloRating.TIE, this.connector.getGamesCount());
		}
		this.creator.setRating(newCreatorRating);
		this.connector.setRating(newConnectorRating);
	}
	
	public Socket getCreatorSocket() {
		return this.creatorSocket;
	}
	
	public GameModel getGameModel() {
		return this;
	}
	
	public boolean canStart() {
		if (this.connector != null && !this.isInterruptWaiting) {
			return true;
		}
		return false;
	}
	
	public Socket getConnectorSocket() {
		return this.connectorSocket;
	}
	
	public int getGameID() {
		return this.gameID;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof GameModel) {
			if (((GameModel) o).gameID == this.gameID) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public void connectToGame(User connector, Socket connectorSocket) {
		if (connector != null && connectorSocket != null) {
			this.connectorSocket = connectorSocket;
			this.connector = connector;
			for (;;) {
				try { Thread.sleep(100); } catch (InterruptedException e) {}
				if (this.isEnded) {
					break;
				}
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.gameID + ": ");
		sb.append(this.creator.getNickname());
		sb.append(" vs ");
		if (this.connector == null) {
			sb.append("<free>");
		} else {
			sb.append(this.connector.getNickname());
		}
		return sb.toString();
	}
}