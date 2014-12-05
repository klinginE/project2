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

	private Player player = null;
	private float windowX = 0.0f;
	private long timer = 0;

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
		Image background = ResourceManager.getImage(BlackFridayBlitz.BACKGROUND_JPG);
		Image flag = ResourceManager.getImage(BlackFridayBlitz.CHECKERED_FLAG_PNG);
		Image checkout = ResourceManager.getImage(BlackFridayBlitz.CHECKOUT_JPG);
		float scaleY = screenHeight / (float)background.getHeight();
		g.translate(-1.0f * (windowX - player.getPlayerCart().MIN_SCREEN_X), 0.0f);
		g.scale(1.0f, scaleY);
		for (int i = 0; i < 20; i++)//270
			g.drawImage(background, (float)(i * background.getWidth()), 0.0f);
		g.scale(1.0f, 1.0f/scaleY);
		g.drawImage(flag.getSubImage(0, 0, 256, flag.getHeight()), (float)(20 * background.getWidth()), 0.0f);
		scaleY = (float)((float)BlackFridayBlitz.MAX_WINDOW_HEIGHT / (float)checkout.getHeight());
		g.scale(scaleY, scaleY);
		g.drawImage(checkout, (float)((float)((20.0f * (float)background.getWidth()) + 256.0f) / scaleY), 0.0f);
		g.resetTransform();
		g.drawString("Time: " + timer / 1000 + " sec", (float)BlackFridayBlitz.MAX_WINDOW_WIDTH - 200.0f, 16.0f);
		player.getPlayerCart().render(g);

	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

		timer += (long)delta;
		windowX = player.getPlayerCart().getWorldX();
		if (windowX > BlackFridayBlitz.MAX_WINDOW_WIDTH * 20 + 256 + ((float)ResourceManager.getImage(BlackFridayBlitz.CHECKOUT_JPG).getWidth()) / 2.0f) {
			windowX = 0;
			player.resetCart();
			timer = 0;
		}
		if (timer < 4000l)
			return;
		player.getPlayerCart().update(container, game, delta);
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