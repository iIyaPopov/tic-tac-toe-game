package view;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class TickTackToe {
	
	public static void main(String[] args) {
		try {
			System.setErr(new PrintStream(new File(Constants.LOG_FILE_DIR)));
			System.err.println("TickTackToe <started>");
			
			Constants.init();
			
			System.err.println("TickTackToe <new Client>");
			
			Client client = new Client();
		
			System.err.println("TickTackToe <new GameFrame>");
			
			GameFrame gameFrame = new GameFrame(client.getSocket());
			gameFrame.start();
			gameFrame.join();
			
			System.err.println("TickTackToe <ended>");

		} catch (FileNotFoundException e) {
			System.err.println("TickTackToe <FileNotFoundException>");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("TickTackToe <IOException>");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.err.println("TickTackToe <InterruptedException>");
			e.printStackTrace();
		}
	}
}