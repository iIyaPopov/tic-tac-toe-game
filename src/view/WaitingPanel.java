package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class WaitingPanel extends JPanel {
	private JLabel message;
	private JLabel timeLabel;
	private JButton cancelButton;
	private int time;
	private int answer;
	private DataInputStream in;
	private DataOutputStream out;
	
	public WaitingPanel(Socket socket) throws IOException {		
		super.setBounds(2, 2, 505, 305);
		super.setLayout(new BorderLayout());
		
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		
		cancelButton = new JButton("Stop waiting");
		cancelButton.setBounds(180, 200, 150, 40);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					/*отправить серверу отмену ожидания*/
					out.writeBoolean(true);
				} catch (IOException err) {
					err.printStackTrace();
				}
			}
		});
		
		Font font = new Font(null, Font.BOLD, 40);
		
		message = new JLabel("Opponent waiting...");
		message.setFont(font);
		message.setBounds(0, 0, 505, 100);
		message.setVerticalAlignment(JLabel.CENTER);
        message.setHorizontalAlignment(JLabel.CENTER);
		
		timeLabel = new JLabel();
		timeLabel.setBounds(200, 100, 200, 100);
		timeLabel.setFont(font);
		
		super.setLayout(null);
		super.add(message);
		super.add(cancelButton);
		super.add(timeLabel);
	}
	
	public int getParametr() throws IOException {
		time = 0;
		
		/*счетчик на панели*/
		Thread timer = new Thread(new Runnable() {
			@Override
			public void run() {
				timeLabel.setText("0:00");
				updateUI();
				while (answer == Constants.NOTHING) {
					try { Thread.sleep(1000); } catch (InterruptedException e) {}
					int min = time / 60;
					int sec = time % 60;
					if (sec < 10) {
						timeLabel.setText(min + ":0" + sec);
					} else {
						timeLabel.setText(min + ":" + sec);
					}
					time++;
				}
			}
		});
		timer.start();
		
		answer = Constants.NOTHING;
		/*ожидание ответа с сервера*/
		for(;;) {
			try { Thread.sleep(100); } catch (InterruptedException e) {}
			boolean answer = in.readBoolean();
			if (answer == true) {
				System.err.println("WaitingPanel <opponent found>");
				out.writeBoolean(false);
				return Constants.OPPONENT_FOUND;
			} else {
				System.err.println("WaitingPanel <stop waiting>");
				return Constants.STOP_WAITING;
			}
		}
	}
	
	@Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(Constants.BACKGROUND_IMAGE, 0, 0, this);
    }
}