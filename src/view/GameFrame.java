package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class GameFrame extends Thread {
	private Socket socket;
	private JFrame frame;
	private WaitingPanel waitingPanel;
	private FieldPanel fieldPanel;
	private ChatPanel chatPanel;
	private LoginPanel loginPanel;
	private GameStartPanel gameStartPanel;
	private GameProtocol gameProtocol;
	private String figure;
	private String enemyFigure;
	private String ip;
	
	public GameFrame(Socket socket) throws IOException {
		this.socket = socket;
		loginPanel = new LoginPanel(this.socket);
	}
	
	@Override
	public void run() {
		try {
			frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(null);
			//frame.setPreferredSize(new Dimension(505, 305));
			frame.setBounds(500, 100, Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
			frame.setResizable(true);
			frame.setVisible(true);
			System.err.println("GameFrame <start game>");

			for (;;) {
				/*авторизация*/
				System.err.println("GameFrame <open login panel>");
				frame.getContentPane().removeAll();
				frame.getContentPane().add(loginPanel);
				frame.update(frame.getGraphics());

				String[] loginParametres = loginPanel.getLoginParametres();
				String nickname = loginParametres[0];
				String password = loginParametres[1];
				String mode = loginParametres[2];
				if (mode.equals(Constants.EXIT)) {
					break;
				}
				
				for (;;) {
					/*главное меню*/
					System.err.println("GameFrame <open main menu>");
					gameStartPanel = new GameStartPanel(socket, nickname);
					frame.getContentPane().removeAll();
					frame.getContentPane().add(gameStartPanel);
					frame.update(frame.getGraphics());

					int menuParametr = mainMenu();
					if (menuParametr == Constants.SIGN_OUT) {
						/*разлогиниться*/
						System.err.println("GameFrame <logout>");
						break;
					} else if (menuParametr == Constants.NEW_GAME) {
						/*ожидание подключения игрока*/
						System.err.println("GameFrame <open opponent waiting panel>");
						waitingPanel = new WaitingPanel(socket);
						frame.getContentPane().removeAll();
						frame.getContentPane().add(waitingPanel);
						frame.update(frame.getGraphics());

						int waitParametr = opponentWaiting();
						if (waitParametr == Constants.STOP_WAITING) {
							/*перестать ждать противника*/
							System.err.println("GameFrame <close opponent waiting panel>");
						} else if (waitParametr == Constants.OPPONENT_FOUND) {
							/*противник найден*/
							System.err.println("GameFrame <opponent found>");
							frame.setVisible(false);
							String figure = figureChoice();
							System.err.println("GameFrame <open figure choice panel>");
							frame.setVisible(true);
							gameProtocol = new GameProtocol(socket);
							chatPanel = new ChatPanel(gameProtocol);
							fieldPanel = new FieldPanel(gameProtocol, figure);
							frame.setVisible(false);
							frame.getContentPane().removeAll();
							frame.getContentPane().add(fieldPanel);
							frame.getContentPane().add(chatPanel);
							frame.setVisible(true);
							chatPanel.chatStart();
							winnerCheck();
							System.err.println("GameFrame <start game protocol>");
							gameProtocol.start();
							chatPanel.chatStop();
						}
					} else if (menuParametr == Constants.CONNECT_TO_GAME) {
						/*подключиться к игре*/
						System.err.println("GameFrame <connect to game>");
						frame.setVisible(false);
						String figure = figureChoice();
						System.err.println("GameFrame <open figure choice panel>");
						frame.setVisible(true);
						gameProtocol = new GameProtocol(socket);
						chatPanel = new ChatPanel(gameProtocol);
						fieldPanel = new FieldPanel(gameProtocol, figure);
						frame.setVisible(false);
						frame.getContentPane().removeAll();
						frame.getContentPane().add(fieldPanel);
						frame.getContentPane().add(chatPanel);
						frame.setVisible(true);
						chatPanel.chatStart();
						winnerCheck();
						System.err.println("GameFrame <start game protocol>");
						gameProtocol.start();
						chatPanel.chatStop();
					} else if (menuParametr == Constants.GET_RATING_LIST) {
						System.err.println("GameFrame <rating>");
					}
				}
			}
		} catch (IOException e) {
			System.err.println("GameFrame <IOException>");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.err.println("GameFrame <InterruptedException>");
			e.printStackTrace();
		}
	}
	
	private String[] login() {
		System.err.println("GameFrame <login()>");
		String[] parametres = null;
		for (;;) {
			parametres = loginPanel.getLoginParametres();
			if (parametres[0] != null && parametres[1] != null) {
				if (parametres[0].length() < 5 || parametres[1].length() < 5) {
					JOptionPane.showMessageDialog(null, "Nickname or password length error", "WARNING", JOptionPane.WARNING_MESSAGE);
				} else {
					return parametres;
				}
			}
		}
	}
	
	private void winnerCheck() {
		System.err.println("GameFrame <winnerCheck()>");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				for (;;) {
					try { Thread.sleep(50); } catch (InterruptedException e) {}
					if (gameProtocol.getIsWin() || gameProtocol.getOpponentIsWin()) {
						if (gameProtocol.getIsWin()) {
							JOptionPane.showMessageDialog(null, "YOU WIN");
						} else {
							JOptionPane.showMessageDialog(null, "YOU LOSE");
						}
						System.err.println("GameFrame <winner found>");
						System.err.println("GameFrame <stop game protocol>");
						gameProtocol.stop();
						break;
					} else if (gameProtocol.isOpponentDisconnect()) {
						System.out.println("winner check (disconnected)");
						break;
					}
				}
			}
		});
		thread.start();
	}
	
	private int mainMenu() {
		int parametr = gameStartPanel.getParametr();
		gameStartPanel.resetAnswer();
		return parametr;
	}
	
	private int opponentWaiting() throws IOException {
		int parametr = waitingPanel.getParametr();
		return parametr;
	}
	
	public String getFigure() {
		return figure;	
	}
	
	public String getEnemyFigure() {
		return enemyFigure;
	}
	
	private String figureChoice() {
		figure = Constants.NOT_FIGURE;
		JFrame frameChoice = new JFrame("Choice of figure");
		frameChoice.setResizable(false);
		frameChoice.setBounds(frame.getX() + 200, frame.getY() + 50, 200, 60);
		//frameChoice.setPreferredSize(new Dimension(200, 60));
		
		JButton circleButton = new JButton();
		circleButton.setBounds(130, 15, 32, 32);
		ImageIcon circleIcon = new ImageIcon(Constants.CIRCLE_DIR);
		circleButton.setIcon(circleIcon);
		circleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				figure = Constants.CIRCLE_DIR;
			}
		});
		
		JButton crossButton = new JButton();
		crossButton.setBounds(30, 15, 32, 32);
		ImageIcon crossIcon = new ImageIcon(Constants.CROSS_DIR);
		crossButton.setIcon(crossIcon);
		crossButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				figure = Constants.CROSS_DIR;
			}
		});
		
		frameChoice.setLayout(null);
		frameChoice.add(circleButton);
		frameChoice.add(crossButton);
		//frameChoice.pack();
		frameChoice.setVisible(true);
		
		while (figure.equals(Constants.NOT_FIGURE)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		frameChoice.setVisible(false);
		return figure;
	}
}