package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.net.*;

public class LoginPanel extends JPanel {
	private String nickname;
	private String password;
	private JLabel messageLabel;
	private boolean isLogin;
	private boolean isRegistration;
	private Image image;
	private LoginProtocol loginProtocol;
	
	public LoginPanel(Socket socket) throws IOException {
		super.setBounds(2, 2, Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
		super.setLayout(null);
		
		nickname = null;
		password = null;
		isLogin = false;
		isRegistration = false;
		loginProtocol = new LoginProtocol(socket);
		image = ImageIO.read(new File(Constants.BACKGROUND_DIR));
		
		Font font = new Font(null, Font.BOLD, 30);
		Font labelFont = new Font(null, Font.PLAIN, 20);
		
		messageLabel = new JLabel();
		messageLabel.setFont(font);
		messageLabel.setText("Welcome to TIC TAC TOE");
		messageLabel.setForeground(new Color(0, 51, 102));
		messageLabel.setBounds(2, 2, 500, 50);
		
		JLabel nicknameLabel = new JLabel("Nickname:");
		nicknameLabel.setBounds(2, 55, 120, 20);
		nicknameLabel.setFont(labelFont);
		
		JTextField nicknameTextField = new JTextField();
		nicknameTextField.setBounds(130, 55, 130, 20);
		nicknameTextField.setText("Nickname");
		
		JLabel nicknameNote = new JLabel("(5-8 chars without spaces)");
		nicknameNote.setForeground(new Color(153, 153, 255));
		nicknameNote.setBounds(270, 55, 220, 20);
		
		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setBounds(2, 75, 120, 20);
		passwordLabel.setFont(labelFont);
		
		JPasswordField passwordTextField = new JPasswordField();
		passwordTextField.setBounds(130, 77, 130, 20);
		passwordTextField.setText("Password");
		
		JLabel passwordNote = new JLabel("(5-8 chars without spaces)");
		passwordNote.setForeground(new Color(153, 153, 255));
		passwordNote.setBounds(270, 75, 220, 20);
		
		JButton loginButton = new JButton("Sign in");
		loginButton.setBounds(150, 110, 110, 25);
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.err.println("LoginPanel <login button pressed>");
					nickname = nicknameTextField.getText();
					//System.out.println(nickname);
					nicknameTextField.setText("");
					password = passwordTextField.getText();
					//System.out.println(password);
					passwordTextField.setText("");
					if (!inputIsCorrect(nickname, password)) {
						/*некорректный ввод*/
						System.err.println("LoginPanel <incorrect input>");
						JOptionPane.showMessageDialog(null, "Login or password incorrect", "WARNING", JOptionPane.WARNING_MESSAGE);
					} else {
						/*запрос на сервер*/
						isLogin = loginProtocol.send("login", nickname, password);
						System.err.println("LoginPanel <request to server = " + isLogin + ">");
					}
				} catch (IOException err) {
					System.err.println("LoginPanel <logButton.IOException>");
					err.printStackTrace();
				}
			}
		});
		
		JButton registrationButton = new JButton("Registration");
		registrationButton.setBounds(2, 110, 130, 25);
		registrationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.err.println("LoginPanel <registration button pressed>");
					nickname = nicknameTextField.getText();
					nicknameTextField.setText("");
					password = passwordTextField.getText();
					passwordTextField.setText("");
					if (!inputIsCorrect(nickname, password)) {
						/*некорректный ввод*/
						System.err.println("LoginPanel <incorrect input>");
						JOptionPane.showMessageDialog(null, "Login or password incorrect", "WARNING", JOptionPane.WARNING_MESSAGE);
					} else {
						/*запрос на сервер*/
						System.err.println("LoginPanel <request to server>");
						isRegistration = loginProtocol.send("registration", nickname, password);
						if (!isRegistration) {
							System.err.println("LoginPanel <imposible registration>");
							//JOptionPane.showMessageDialog(null, "User with a same nickname already exists", "WARNING", JOptionPane.WARNING_MESSAGE);
						} else {
							System.err.println("LoginPanel <successful registration>");
						}
					}
				} catch (IOException err) {
					System.err.println("LoginPanel <regButton.IOException>");
					err.printStackTrace();
				}
			}
		});
		
		super.add(messageLabel);
		super.add(nicknameLabel);
		super.add(nicknameTextField);
		super.add(nicknameNote);
		super.add(passwordLabel);
		super.add(passwordTextField);
		super.add(passwordNote);
		super.add(loginButton);
		super.add(registrationButton);
	}
	
	public boolean inputIsCorrect(String nickname, String password) {
		if (nickname.length() <= 5 || nickname.length() > 8 || password.length() <= 5 || password.length() > 8) {
			return false;
		}
		if (nickname.contains(" ")) {
			return false;
		}
		return true;
	}
	
	public String[] getLoginParametres() {
		String[] parametres = new String[3];
		for(;;) {
			try { Thread.sleep(100); } catch (InterruptedException e) {}
			if (isLogin || isRegistration) {
				parametres[0] = nickname;
				parametres[1] = password;
				if (isLogin) {
					parametres[2] = "sign in";
				}
				if (isRegistration) {
					parametres[2] = "registration";
				}
				isLogin = false;
				isRegistration = false;
				return parametres;
			}
		}
	}
	
	@Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(Constants.BACKGROUND_IMAGE, 0, 0, this);
    }
}