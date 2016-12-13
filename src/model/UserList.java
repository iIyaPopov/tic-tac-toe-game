package model;

import java.util.*;
import java.io.*;

public class UserList {
	private ArrayList<User> list;
	
	public UserList() {
		this.list = new ArrayList<User>();
	}
	
	public synchronized boolean add(User user) {
		if (user != null) {
			return this.list.add(user);
		} else {
			return false;
		}
	}
	
	public synchronized boolean remove(User user) {
		if (user != null) {
			return list.remove(user);
		} else {
			return false;
		}
	}
	
	public boolean isContainsNickname(String nickname) {
		if (nickname == null || "".equals(nickname)) {
			return false;
		}
		for (int i = 0; i < this.list.size(); i++) {
			if (nickname.equals(this.list.get(i).getNickname())) {
				return true;
			}
		}
		return false;
	}
	
	public User find(String nickname) {
		if (nickname == null || "".equals(nickname)) {
			return null;
		}
		Iterator iterator = this.list.iterator();
		while (iterator.hasNext()) {
			User user = (User) iterator.next();
			if (user.getNickname().equals(nickname)) {
				return user;
			}
		}
		return null;
	}
	
	public String getRatingList() {
		Iterator iterator = this.list.iterator();
		StringBuilder sb = new StringBuilder();
		while (iterator.hasNext()) {
			User user = (User) iterator.next();
			sb.append(user.getNickname() + ": ");
			sb.append(user.getGamesCount() + "/");
			sb.append(user.getRating() + "\n");
		}
		return sb.toString();
	}
	
	public User isContains(User user) {
		if (user == null) {
			return null;
		}
		int index = this.list.indexOf(user);
		if (index != -1) {
			return list.get(index);
		} else {
			return null;
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator iterator = this.list.iterator();
		while (iterator.hasNext()) {
			User u = (User)iterator.next();
			sb.append(u.toString());
		}
		return sb.toString();
	}
	
	public boolean load(String filename) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(filename));
		String nickname = null;
		String password = null;
		int rating = 0;
		int gamesCount = 0;
		while (sc.hasNextLine()) {
			nickname = sc.nextLine();
			password = sc.nextLine();
			try {
				rating = Integer.parseInt(sc.nextLine());
				gamesCount = Integer.parseInt(sc.nextLine());
			} catch (NumberFormatException e) {
				return false;
			}
			if (nickname != "" && nickname != null && password != "" && password != null && gamesCount >= 0) {
				this.list.add(new User(nickname, password, rating, gamesCount));
			} else {
				return false;
			}
		}
		sc.close();
		return true;
	}
	
	public synchronized void save(String filename) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(filename));
		pw.print(this.toString());
		pw.close();
	}
}