package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;

public class GameStartPanel extends JPanel {	
	private JButton createButton;
	private JButton connectButton;
	private JButton signOutButton;
	private JButton ratingButton;
	private JLabel nicknameLabel;
	private Image image;
	private String nickname;
	private int answer;
	private GameStartProtocol gameStartProtocol;
	private JPanel panel;
	
	public GameStartPanel(Socket socket, String nickname) throws IOException {
		panel = this;
		answer = Constants.NOTHING;
		image = ImageIO.read(new File(Constants.BACKGROUND_DIR));
		gameStartProtocol = new GameStartProtocol(socket);
		
		super.setBounds(0, 0, Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
		
		nicknameLabel = new JLabel();
		nicknameLabel.setText("You are signed in as " + nickname);
		nicknameLabel.setBounds(0, 2, Constants.FRAME_WIDTH, 15);
		nicknameLabel.setForeground(new Color(153, 153, 255));
		nicknameLabel.setHorizontalAlignment(JLabel.RIGHT);
		
		createButton = new JButton("Create new game");
		createButton.setBounds(130, 50, 240, 40);
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					gameStartProtocol.send("new game");
					answer = Constants.NEW_GAME;
				} catch (IOException err) {
					System.err.println("GameStartProtocol <createButton>");
					err.printStackTrace();
				}
			}
		});
		
		connectButton = new JButton("Connect to game");
		connectButton.setBounds(130, 105, 240, 40);
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String isSend = gameStartProtocol.send("connect to game");
					if ("true".equals(isSend)) {
						answer = Constants.CONNECT_TO_GAME;
					}
				} catch (IOException err) {
					System.err.println("GameStartProtocol <connectButton>");
					err.printStackTrace();
				}
			}
		});
		
		signOutButton = new JButton("Sign out");
		signOutButton.setBounds(130, 160, 240, 40);
		signOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					gameStartProtocol.send("sign out");
					answer = Constants.SIGN_OUT;
				} catch (IOException err) {
					System.err.println("GameStartProtocol <signOutButton>");
					err.printStackTrace();
				}
			}
		});
		
		ratingButton = new JButton("Rating");
		ratingButton.setBounds(130, 215, 240, 40);
		ratingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String ratingList = gameStartProtocol.send("get rating");
					
					JLabel info = new JLabel("<Nickname>: <games count>/<rating>");
					info.setBounds(2, 2, 300, 20);
					
					JTextArea ratingArea = new JTextArea(ratingList);
					ratingArea.setEditable(false);
					ratingArea.setLineWrap(true);
        			ratingArea.setWrapStyleWord(true);
					
					JScrollPane ratingScrollPane = new JScrollPane(ratingArea);
					ratingScrollPane.setBounds(5, 25, 260, 280);
					ratingScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					ratingScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
					
					JButton closeButton = new JButton("Go to back");
					closeButton.setBounds(380, 2, 120, 25);
					closeButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							answer = Constants.GET_RATING_LIST;
						}
					});
					
					panel.removeAll();
					panel.add(info);
					panel.add(ratingScrollPane);
					panel.add(closeButton);
					updateUI();
				} catch (IOException err) {
					System.err.println("GameStartProtocol <ratingButton>");
					err.printStackTrace();
				}
			}
		});
		
		super.setLayout(null);
		super.add(createButton);
		super.add(connectButton);
		super.add(signOutButton);
		super.add(ratingButton);
		super.add(nicknameLabel);
	}
	
	public int getParametr() {
		for(;;) {
			try { Thread.sleep(100); } catch (InterruptedException e) {}
			if (answer != Constants.NOTHING) {
				return answer;
			}
		}
	}
	
	public void resetAnswer() {
		answer = Constants.NOTHING;
	}
	
	@Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}