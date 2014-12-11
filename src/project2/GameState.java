package project2;

import java.io.Serializable;
import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import project2.Cart.CartState;

public class GameState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6976615829378092809L;
	public HashMap<String, CartState> playerCarts = null;
	public HashMap<String, Integer> containers = null;
	public HashMap<String, Integer> deltas = null;
	public long timer = 0;
	public long frame = 0;

	public GameState() {

		super();
		playerCarts = new HashMap<String, CartState>();
		containers = new HashMap<String, Integer>();
		deltas = new HashMap<String, Integer>();

	}

	public void addGame(String username, Cart cart, GameContainer container, StateBasedGame game, int delta) {

		playerCarts.put(username, new CartState(cart.getX(), cart.getY(), cart.getCoarseGrainedWidth(), cart.getCoarseGrainedHeight(), cart.getNumSpeedUps(), cart.getCurrentSpeed(), cart.getWorldX(), cart.getWorldY(), cart.getJumpPoint(), cart.getImageString()));
		Input i = container.getInput();
		Integer intInput = null;
		intInput = null;
		if (i.isKeyDown(Input.KEY_LEFT))
			intInput = new Integer(Input.KEY_LEFT);
		else if (i.isKeyDown(Input.KEY_RIGHT))
			intInput = new Integer(Input.KEY_RIGHT); 
		containers.put(username, intInput);
		deltas.put(username, new Integer(delta));

	}

}