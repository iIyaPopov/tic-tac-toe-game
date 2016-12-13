package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class FieldPanel extends JPanel {
	private Cell[][] cells;
	private GameProtocol gameProtocol;

	public FieldPanel(GameProtocol gameProtocol, String figure) {
		this.gameProtocol = gameProtocol;
		super.setBounds(2, 2, 300, 300);
		super.setLayout(new GridLayout(Constants.FIELD_HEIGHT, Constants.FIELD_WIDTH));
		cells = new Cell[Constants.FIELD_HEIGHT][Constants.FIELD_WIDTH];
		for (int i = 0; i < Constants.FIELD_HEIGHT; i++) {
			for (int j = 0; j < Constants.FIELD_WIDTH; j++) {
				cells[i][j] = new Cell(i, j, gameProtocol, figure);
				super.add(cells[i][j]);
			}
		}
		Thread checkMovies = new Thread(new Runnable() {
			@Override
			public void run() {
				for (;;) {
					try {
						Thread.sleep(200);
						int x = gameProtocol.getX();
						if (x != -1) {
							int y = gameProtocol.getY();
							cells[x][y].putOpponentMove();
						}
						if (gameProtocol.getIsWin() || gameProtocol.getOpponentIsWin() || gameProtocol.isOpponentDisconnect()) {
							break;
						} else if (gameProtocol.isTimeUp()) {
							JOptionPane.showMessageDialog(null, "TIE\nTime is over", "", JOptionPane.INFORMATION_MESSAGE);
							break;
						}
					} catch (IOException e) {
					} catch (InterruptedException e) {}
				}
			}
		});
		checkMovies.start();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}