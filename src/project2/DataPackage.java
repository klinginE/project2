package project2;

import java.io.Serializable;

public class DataPackage implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String MSG_100 = "All Good";
	public static final String MSG_300 = "Client is diconnecting";
	public static final String MSG_400 = "Server is diconnecting";
	public static final String MSG_500 = "Server is shutting down";

	private String username = "";
	private int state = 0;
	private String message = "";
	//private Gamestate gameState = null;

	public DataPackage() {
		super();
	}
	public DataPackage(String name, int state, String msg) {
		super();
		username = name;
		this.state = state;
		setMessage(msg);
	}

	/*public void setGameState(GameState gs) {
		gameState = gs;
	}

	public GameState getGameState() {
		return gameState;
	}*/

	public String getUsername() {
		return username;
	}

	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}