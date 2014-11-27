package project2;

import jig.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class SinglePlayerGameState extends BasicGameState {

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {

	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {

		Image background = ResourceManager.getImage(BlackFridayBlitz.BACKGROUND_PNG);
		int screenWidth = BlackFridayBlitz.MAX_WINDOW_WIDTH;
		int screenHeight = BlackFridayBlitz.MAX_WINDOW_HEIGHT;
		float scaleX = 0.25f * screenWidth / background.getWidth();
		float scaleY = screenHeight / 3.0f / background.getHeight();
		g.scale(scaleX, scaleY);
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 3; j++)
				g.drawImage(background, i * background.getWidth(), j * background.getHeight());
		g.scale(1.0f/scaleX, 1.0f/scaleY);
		Image player = ResourceManager.getImage(BlackFridayBlitz.STANDIN_PLAYER_PNG);
		g.drawImage(player, 0.0f, 0.0f);

	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2) throws SlickException {

	}

	@Override
	public int getID() {

		return BlackFridayBlitz.SINGLE_PLAYER_GAME_STATE_ID;

	}

}