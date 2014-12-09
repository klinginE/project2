package project2;

import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

public class GameState {

	public HashMap<String, Cart> playerCarts = null;
	public HashMap<String, GameContainer> containers = null;
	public HashMap<String, StateBasedGame> games = null;
	public HashMap<String, Integer> deltas = null;
	public long timer = 0;

	public GameState() {

		super();
		playerCarts = new HashMap<String, Cart>();
		containers = new HashMap<String, GameContainer>();
		games = new HashMap<String, StateBasedGame>();
		deltas = new HashMap<String, Integer>();

	}

	public void addGame(String username, Cart cart, GameContainer container, StateBasedGame game, int delta) {

		playerCarts.put(username, cart);
		containers.put(username, container);
		games.put(username, game);
		deltas.put(username, new Integer(delta));

	}

}