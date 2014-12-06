package project2;

import java.util.ArrayList;

import jig.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class MultiPlayerGameState extends BasicGameState {

	private ArrayList<Player> players = null;
	private float windowX = 0.0f;

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {

		players = new ArrayList<Player>();
		players.add(new Player());
		players.get(0).connectToServer();

	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {

		float screenHeight = (float)BlackFridayBlitz.MAX_WINDOW_HEIGHT;
		Image background = ResourceManager.getImage(BlackFridayBlitz.BACKGROUND_PNG);
		float scaleY = screenHeight / (float)background.getHeight();
		windowX = players.get(0).getPlayerCart().getWorldX();
		g.translate(-1.0f * (windowX - players.get(0).getPlayerCart().MIN_SCREEN_X), 0.0f);
		g.scale(1.0f, scaleY);
		for (int i = 0; i < 40; i++)
			g.drawImage(background, i * background.getWidth(), 0.0f);
		g.scale(1.0f, 1.0f/scaleY);
		g.translate((windowX - players.get(0).getPlayerCart().MIN_SCREEN_X), 0.0f);
		players.get(0).getPlayerCart().render(g);

	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		players.get(0).getPlayerCart().update(container, game, delta);
		windowX = players.get(0).getPlayerCart().getWorldX();
	}

	@Override
	public int getID() {

		return BlackFridayBlitz.MULTI_PLAYER_GAME_STATE_ID;

	}

}