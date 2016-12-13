package view;

import java.io.*;
import javax.imageio.ImageIO;
import java.awt.*;

public class Constants {
	public static final int GAME_LIMIT			= 300;
	public static final int FIELD_W_SIZE 		= 300;
	public static final int FIELD_H_SIZE 		= 300;
	public static final int FRAME_WIDTH 		= 510;
	public static final int FRAME_HEIGHT	 	= 310;
	public static final int FIELD_HEIGHT 		= 10;
	public static final int FIELD_WIDTH 		= 10;
	public static final int PORT		  		= 10000;
	public static final String DEFAULT_IP 		= "127.0.0.1";
	public static 		String DIR 				= "";
	public static final String CIRCLE_DIR 		= DIR + "./client_data/circle.jpg";
	public static final String CROSS_DIR 		= DIR + "./client_data/cross.jpg";
	public static final String BACKGROUND_DIR	= DIR + "./client_data/background.jpg";
	public static final String LOG_FILE_DIR		= DIR + "./client_log.txt";
	public static final String NOT_FIGURE		= "";
	public static final int SIGN_OUT			= 0;
	public static final int NEW_GAME			= 1;
	public static final int CONNECT_TO_GAME		= 2;
	public static final int NOTHING				= 3;
	public static final int CIRCLE 				= 4;
	public static final int CROSS 				= 5;
	public static final int GO_TO_BACK			= 6;
	public static final int STOP_WAITING	 	= 7;
	public static final int OVER_TIME	 		= 8;
	public static final int OPPONENT_FOUND 		= 9;
	public static final int GET_RATING_LIST		= 10;
	public static final String EXIT				= "exit";
	public static Image BACKGROUND_IMAGE;
	
	public static void init() throws IOException {
		BACKGROUND_IMAGE = ImageIO.read(new File(Constants.BACKGROUND_DIR));
	}
}
