package view;

import java.io.*;
import java.net.*;
import javax.swing.*;

public class GameProtocol {
	private DataInputStream in;
	private DataOutputStream out;
	private Thread listener;
	private String msg;
	private boolean canPut;
	private boolean isWin;
	private boolean opponentIsWin;
	private boolean opponentDisconnect;
	private boolean timeUp;
	private int xCoord;
	private int yCoord;
	
	public GameProtocol(Socket socket) throws IOException {
		this.xCoord = -1;
		this.yCoord = -1;
		this.isWin = false;
		this.opponentIsWin = false;
		this.timeUp = false;
		this.opponentDisconnect = false;
		this.in = new DataInputStream(socket.getInputStream());
		this.out = new DataOutputStream(socket.getOutputStream());
		System.out.println(in.readUTF());
		System.out.println(in.readUTF());
		/*boolean isFirstMove = in.readBoolean();
		if (isFirstMove) {
			JOptionPane.showMessageDialog(null, "You move", "", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "Opponent move", "", JOptionPane.INFORMATION_MESSAGE);
		}*/
	}
	
	public void sendMessage(String message) throws IOException {
		System.err.println("GameProtocol <send message>");
		this.out.writeUTF("message");
		this.out.writeUTF(message);
	}
	
	public void sendCoordinate(int x, int y) throws IOException {
		System.err.println("GameProtocol <send coordinate>");
		this.out.writeUTF("coordinate");
		this.out.writeInt(x);
		this.out.writeInt(y);
	}
	
	public boolean getIsWin() {
		return this.isWin;
	}
	
	public boolean getOpponentIsWin() {
		return this.opponentIsWin;
	}
	
	public boolean isOpponentDisconnect() {
		return this.opponentDisconnect;
	}
	
	public String getMsg() {
		String result = msg;
		msg = null;
		return result;
	}
	
	public boolean isCanPut() {
		if (this.canPut) {
			this.canPut = false;
			return true;
		}
		return false;
	}
	
	public boolean isTimeUp() {
		return this.timeUp;
	}
	
	public int getX() {
		int result = this.xCoord;
		this.xCoord = -1;
		return result;
	}
	
	public int getY() {
		int result = this.yCoord;
		this.yCoord = -1;
		return result;
	}
	
	public void start() throws InterruptedException {
		listener = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (;;) {
						String mode = in.readUTF();
						System.out.println("listener: " + mode);
						if ("message".equals(mode)) {
							System.err.println("GameProtocol <get message>");
							msg = "Opponent: " + in.readUTF();
						} else if ("coordinate".equals(mode)) {
							System.err.println("GameProtocol <get coordinate>");
							xCoord = in.readInt();
							yCoord = in.readInt();
							opponentIsWin = in.readBoolean();
							if (opponentIsWin) {
								System.err.println("GameProtocol <opponent win>");
								out.writeUTF("break");
								break;
							}
						} else if ("answerToCanPut".equals(mode)) {
							System.err.println("GameProtocol <get answer to can put>");
							canPut = in.readBoolean();
							if (canPut) {
								isWin = in.readBoolean();
								if (isWin) {
									System.err.println("GameProtocol <win>");
									break;
								}
								System.err.println("GameProtocol <can't to put>");
							}
						} else if ("timeIsOver".equals(mode)) {
							timeUp = true;
							break;
						} else if ("opponent disconnected".equals(mode)) {
							out.writeUTF("break");
							opponentDisconnect = true;
							JOptionPane.showMessageDialog(null, "Opponent disconnected", "", JOptionPane.INFORMATION_MESSAGE);
							break;
						} else if ("tie".equals(mode)) {
							JOptionPane.showMessageDialog(null, "Tie", "", JOptionPane.INFORMATION_MESSAGE);
							break;
						}
					}
					System.err.println("GameProtocol <stop>");
				} catch (IOException e) {
					System.err.println("GameProtocol <IOException start()>");
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Server crushed", "", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		listener.start();
		listener.join();
	}
	
	public void stop() {
		listener.interrupt();
	}
}