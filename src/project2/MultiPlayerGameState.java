package project2;

import jig.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class MultiPlayerGameState extends BasicGameState {

	private Player player = null;
	

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {

		//player = new Player();
	    player.connectToServer();

	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {

		GameState gameState = (GameState)(player.getPlayerClient().getGameData());
		Cart myCart = gameState.playerCarts.get(player.getUsername());
		float screenHeight = (float)BlackFridayBlitz.MAX_WINDOW_HEIGHT;

		Image background = ResourceManager.getImage(BlackFridayBlitz.BACKGROUND_PNG);
		Image flag = ResourceManager.getImage(BlackFridayBlitz.CHECKERED_FLAG_PNG);
		flag = flag.getSubImage(0, 0, 256, flag.getHeight());
		Image checkout = ResourceManager.getImage(BlackFridayBlitz.CHECKOUT_JPG);
		Image light = ResourceManager.getImage(BlackFridayBlitz.TRAFFICLIGHT_PNG);

		float scaleY = screenHeight / (float)background.getHeight();

		// Translate background
		g.translate(-1.0f * (myCart.getWorldX() - myCart.MIN_SCREEN_X), 0.0f);

		// Draw background
		g.scale(1.0f, scaleY);
		int numPannels = 2;//270
		for (int i = 0; i < numPannels; i++)
			g.drawImage(background, (float)(i * background.getWidth()), 0.0f);
		g.scale(1.0f, 1.0f/scaleY);

		if (gameState.timer <= 1000)
			g.drawImage(light.getSubImage(0, 0, 32, 64), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH / 2.0f, 50.0f);
		if (gameState.timer > 1000 && gameState.timer <= 2000)
			g.drawImage(light.getSubImage(32, 0, 32, 64), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH / 2.0f, 50.0f);
		if (gameState.timer > 2000 && gameState.timer <= 3000)
			g.drawImage(light.getSubImage(64, 0, 32, 64), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH / 2.0f, 50.0f);
		if (gameState.timer > 3000)
			g.drawImage(light.getSubImage(96, 0, 32, 64), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH / 2.0f, 50.0f);

		// Draw flag
		g.drawImage(flag, (float)(numPannels * background.getWidth()), 0.0f);

		// Draw Checkout
		scaleY = (float)(screenHeight / (float)checkout.getHeight());
		g.scale(scaleY, scaleY);
		g.drawImage(checkout, (float)((float)((float)numPannels * (float)background.getWidth() + (float)flag.getWidth()) / scaleY), 0.0f);

		// Undo transforms
		g.resetTransform();

		// Print time
		g.drawString("Time: " + gameState.timer / 1000 + " sec", (float)BlackFridayBlitz.MAX_WINDOW_WIDTH - 200.0f, 16.0f);

		// Draw the player
		for (String user : gameState.playerCarts.keySet())
			gameState.playerCarts.get(user).render(g);

	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

		((GameState)player.getPlayerClient().getGameData()).containers.put(player.getUsername(), container);
		((GameState)player.getPlayerClient().getGameData()).games.put(player.getUsername(), game);
		((GameState)player.getPlayerClient().getGameData()).deltas.put(player.getUsername(), new Integer(delta));

	}

	@Override
	public int getID() {

		return BlackFridayBlitz.SINGLE_PLAYER_GAME_STATE_ID;

	}

}
