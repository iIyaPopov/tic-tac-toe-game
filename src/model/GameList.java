package model;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameList {
	private ArrayList<GameModel> list;
	
	public GameList() {
		this.list = new ArrayList<GameModel>();
	}
	
	public boolean add(GameModel gameModel) {
		return this.list.add(gameModel);
	}
	
	public boolean remove(GameModel gameModel) {
		return this.list.remove(gameModel);
	}
	
	public int getCount() {
		return this.list.size();
	}
	
	public GameModel getGameModel(int id) {
		Iterator iterator = this.list.iterator();
		if (list.size() == 0) {
			return null;
		}
		while (iterator.hasNext()) {
			GameModel gameModel = (GameModel) iterator.next();
			if (gameModel.getGameID() == id) {
				return gameModel;
			}
		}
		return null;
	}
	
	public String toString(int index) {
		return this.list.get(index).toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator iterator = this.list.iterator();
		while (iterator.hasNext()) {
			sb.append(iterator.next().toString() + "\n");
		}
		return sb.toString();
	}
}