package view;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Cell extends JPanel {
	private final int X;
	private final int Y;
	private final Color BACKGROUND_COLOR = new Color(255, 255, 255);
	private final Color BORDER_COLOR = new Color(17, 6, 117);
	private final int BORDER_WIDTH = 1;
	private final int WIN_BORDER_WIDTH = 3;
	private Image image;
	private GameProtocol gameProtocol;
	private String figure;
	private String opponentFigure;

	public Cell(int x, int y, GameProtocol gameProtocol, String figure) {
		this.X = x;
		this.Y = y;
		this.figure = figure;
		if (this.figure == Constants.CIRCLE_DIR) {
			this.opponentFigure = Constants.CROSS_DIR;
		} else {
			this.opponentFigure = Constants.CIRCLE_DIR;
		}
		this.gameProtocol = gameProtocol;
		super.setBackground(this.BACKGROUND_COLOR);
		super.setBorder(BorderFactory.createLineBorder(this.BORDER_COLOR, this.BORDER_WIDTH));
		super.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					gameProtocol.sendCoordinate(x, y);
					try { Thread.sleep(500); } catch (InterruptedException err) {}
					if (gameProtocol.isCanPut()) {
						System.err.println("Cell <put move (" + X + ", " + Y + ")>");
						image = ImageIO.read(new File(figure));
					}
					repaint();
				} catch (IOException err) {
					err.printStackTrace();
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				setBorder(BorderFactory.createLineBorder(Color.ORANGE, BORDER_WIDTH));
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				setBorder(BorderFactory.createLineBorder(BORDER_COLOR, BORDER_WIDTH));
			}
		});
	}
	
	public void putOpponentMove() throws IOException {
		System.err.println("Cell <put opponent (" + X + ", " + Y + ")>");
		File file = new File(opponentFigure);
		this.image = ImageIO.read(file);
		super.setBorder(BorderFactory.createLineBorder(Color.RED, BORDER_WIDTH + 1));
		super.updateUI();
		try { Thread.sleep(2000); } catch (InterruptedException err) {}
		setBorder(BorderFactory.createLineBorder(BORDER_COLOR, BORDER_WIDTH));
		super.updateUI();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null);
	}
}