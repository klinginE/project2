package project2;

import jig.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class SinglePlayerGameState extends BasicGameState {

	Cart c = null;
	float windowX = 0.0f;

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {

		c = new Cart(BlackFridayBlitz.PLAYER4_PNG, 50.0f, 275.0f);

	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {

		float screenHeight = (float)BlackFridayBlitz.MAX_WINDOW_HEIGHT;
		Image background = ResourceManager.getImage(BlackFridayBlitz.BACKGROUND_PNG);		
		float scaleY = screenHeight / (float)background.getHeight();
		g.translate(-1.0f * (windowX - c.MIN_SCREEN_X), 0.0f);
		g.scale(1.0f, scaleY);
		for (int i = 0; i < 40; i++)
			g.drawImage(background, i * background.getWidth(), 0.0f);
		g.scale(1.0f, 1.0f/scaleY);
		g.translate((windowX - c.MIN_SCREEN_X), 0.0f);
		c.render(g);

	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

		c.update(container, game, delta); 
		windowX = c.getWorldX();
		Input input = container.getInput();
		if (input.isKeyPressed(Input.KEY_UP) && c.getY() == c.getJumpPoint())
			c.setJumpPoint(c.getY() - 175.0f);
		if (input.isKeyPressed(Input.KEY_DOWN) && c.getY() == c.getJumpPoint())
			c.setJumpPoint(c.getY() + 175.0f);

	}

	@Override
	public int getID() {

		return BlackFridayBlitz.SINGLE_PLAYER_GAME_STATE_ID;

	}

}