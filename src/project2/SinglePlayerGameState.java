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

	Player player = null;
	float windowX = 0.0f;

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {

		player = new Player();

	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {

		float screenHeight = (float)BlackFridayBlitz.MAX_WINDOW_HEIGHT;
		Image background = ResourceManager.getImage(BlackFridayBlitz.BACKGROUND_PNG);		
		float scaleY = screenHeight / (float)background.getHeight();
		g.translate(-1.0f * (windowX - player.getPlayerCart().MIN_SCREEN_X), 0.0f);
		g.scale(1.0f, scaleY);
		for (int i = 0; i < 40; i++)
			g.drawImage(background, i * background.getWidth(), 0.0f);
		g.scale(1.0f, 1.0f/scaleY);
		g.translate((windowX - player.getPlayerCart().MIN_SCREEN_X), 0.0f);
		player.getPlayerCart().render(g);

	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

		player.getPlayerCart().update(container, game, delta); 
		windowX = player.getPlayerCart().getWorldX();
		Input input = container.getInput();
		if (input.isKeyPressed(Input.KEY_UP) && player.getPlayerCart().getY() == player.getPlayerCart().getJumpPoint())
			player.getPlayerCart().setJumpPoint(player.getPlayerCart().getY() - 175.0f);
		if (input.isKeyPressed(Input.KEY_DOWN) && player.getPlayerCart().getY() == player.getPlayerCart().getJumpPoint())
			player.getPlayerCart().setJumpPoint(player.getPlayerCart().getY() + 175.0f);

	}

	@Override
	public int getID() {

		return BlackFridayBlitz.SINGLE_PLAYER_GAME_STATE_ID;

	}

}