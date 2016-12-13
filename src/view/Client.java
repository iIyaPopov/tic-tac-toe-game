package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Client {
	private boolean isConnected;
	private Socket clientSocket;
	
	public Client() throws IOException, UnknownHostException {
		isConnected = false;
		
		for (;;) {
			try {
				JOptionPane ipOptionPane = new JOptionPane(null, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
				String ip = ipOptionPane.showInputDialog(null, "Enter server IP", Constants.DEFAULT_IP);
				
				/*подключение к серверу*/
				System.err.println("Client <try to connect to servet>");
				InetAddress inetAddress = InetAddress.getByName(ip);
				if (inetAddress.isReachable(1000)) {
					clientSocket = new Socket(inetAddress, Constants.PORT);
					if (clientSocket != null) {
						/*подключение удалось*/
						System.err.println("Client <connected>");
						isConnected = true;
						break;
					}
				}
			} catch (ConnectException e) {
				/*подключение не удалось*/
				System.err.println("Client <not connected>");
				int parametr = JOptionPane.showConfirmDialog(null,
													 "Connection error. Do you want try again?",
													 "",
													 JOptionPane.YES_NO_OPTION);
				if (parametr == JOptionPane.NO_OPTION) {
					System.exit(1);
				}
			}
		}
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public Socket getSocket() {
		return clientSocket;
	}
}