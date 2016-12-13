package view;

import java.io.*;
import java.net.*;
import javax.swing.*;

public class GameStartProtocol {
	private DataInputStream in;
	private DataOutputStream out;
	
	public GameStartProtocol(Socket socket) throws IOException {
		this.in = new DataInputStream(socket.getInputStream());
		this.out = new DataOutputStream(socket.getOutputStream());
	}
	
	public String send(String mode) throws IOException {
		this.out.writeUTF(mode);
		if ("new game".equals(mode)) {
			System.err.println("GameStartProtocol <new game>");
			this.in.readBoolean();
		} else if ("connect to game".equals(mode)) {
			System.err.println("GameStartProtocol <connect to game>");
			int gameCreatedCount = this.in.readInt();
			if (gameCreatedCount > 0) {
				String[] createdGames = new String[gameCreatedCount];
				for (int i = 0; i < gameCreatedCount; i++) {
					createdGames[i] = this.in.readUTF();
				}
				String choiceGame = (String) JOptionPane.showInputDialog(null,
																		"Choice a game",
																		"",
																		JOptionPane.OK_CANCEL_OPTION,
																		null,
																		createdGames,
																		"");
				if (choiceGame != null) {
					/*отправка id игры*/
					String[] split = choiceGame.split(":");
					int id = Integer.parseInt(split[0]);
					this.out.writeBoolean(true);
					this.out.writeInt(id);
					boolean isConnected = this.in.readBoolean();
					if (isConnected) {
						return "true";
					} else {
						JOptionPane.showMessageDialog(null, "Impossible to connect", "WARNING", JOptionPane.WARNING_MESSAGE);
						return "false";
					}
				} else {
					this.out.writeBoolean(false);
					return "false";
				}
			} else {
				this.out.writeBoolean(false);
				JOptionPane.showMessageDialog(null, "Games not found", "WARNING", JOptionPane.WARNING_MESSAGE);
				return "false";
			}
		} else if ("sign out".equals(mode)) {
			System.err.println("GameStartProtocol <logout>");
		} else if ("get rating".equals(mode)) {
			System.err.println("GameStartProtocol <get rating>");
			String ratingList = this.in.readUTF();
			return ratingList;
		}
		return null;
	}
}