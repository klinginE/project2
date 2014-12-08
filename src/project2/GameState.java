package project2;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

public class GameState {

	public ArrayList<Cart> playerCarts = null;
	public long timmer = 0;
	public ArrayList<GameContainer> containers = null;
	public ArrayList<StateBasedGame> games = null;
	public ArrayList<Integer> deltas = null;

	public void addCart(Cart c) {

		if (playerCarts == null)
			playerCarts = new ArrayList<Cart>();
		playerCarts.add(c);

	}

}