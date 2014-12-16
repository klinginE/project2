package project2;

import java.util.concurrent.TimeUnit;

import jig.ResourceManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class MultiPlayerGameState extends BasicGameState {

	private Player player = null;
	private long frame = 0;

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {

		player = new Player(Level.platformY[1]);
		player.connectToServer();

	}

	public String realTime(long time){		
		return String.format("%02d:%02d.%02d", TimeUnit.MILLISECONDS.toMinutes(time), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)), (time - (TimeUnit.MILLISECONDS.toSeconds(time) * 1000)));			
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {

		GameState gameState = player.getPlayerClient().getGameState();
		if (gameState == null)
			return;

		Cart myCart = gameState.playerCarts.get(player.getUsername()).getCart(true); //player.getPlayerCart();
		if (myCart == null)
			return;

		Level myLevel = gameState.level.getLevel();
		if (myLevel == null)
			return;

		float screenHeight = (float)BlackFridayBlitz.MAX_WINDOW_HEIGHT;
		Image back = ResourceManager.getImage(BlackFridayBlitz.BACK_PNG);
		Image background = ResourceManager.getImage(BlackFridayBlitz.BACKGROUND_PNG);
		Image flag = ResourceManager.getImage(BlackFridayBlitz.CHECKERED_FLAG_PNG);
		flag = flag.getSubImage(0, 0, 256, flag.getHeight());
		Image checkout = ResourceManager.getImage(BlackFridayBlitz.CHECKOUT_JPG);
		Input input = container.getInput();

		float scaleY = (screenHeight - 100.0f) / (float)background.getHeight();

		// Translate background
		g.translate(-1.0f * (myCart.getWorldX() - Cart.MIN_SCREEN_X), 0.0f);

		// Draw background
		g.scale(1.0f, scaleY);
		//int numPannels = level.getLength();//270
		for (int i = 0; i < gameState.level.getLevel().getLength(); i++)
			g.drawImage(background, (float)(i * background.getWidth()), 0.0f);
		g.scale(1.0f, 1.0f/scaleY);

		if (gameState.timer <= 1000)
			g.drawImage(ResourceManager.getImage(BlackFridayBlitz.TRAFFICLIGHT_PNG).getSubImage(0, 0, 32, 64), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH / 2.0f, 50.0f);
		if (gameState.timer > 1000 && gameState.timer <= 2000)
			g.drawImage(ResourceManager.getImage(BlackFridayBlitz.TRAFFICLIGHT_PNG).getSubImage(32, 0, 32, 64), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH / 2.0f, 50.0f);
		if (gameState.timer > 2000 && gameState.timer <= 3000)
			g.drawImage(ResourceManager.getImage(BlackFridayBlitz.TRAFFICLIGHT_PNG).getSubImage(64, 0, 32, 64), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH / 2.0f, 50.0f);
		if (gameState.timer > 3000)
			g.drawImage(ResourceManager.getImage(BlackFridayBlitz.TRAFFICLIGHT_PNG).getSubImage(96, 0, 32, 64), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH / 2.0f, 50.0f);

		// Draw flag
		g.drawImage(flag, (float)(gameState.level.getLevel().getLength() * background.getWidth()), 0.0f);
		// Draw items
		for(int i = 0; i < gameState.level.getLevel().getSpeedups().size(); i++)
			gameState.level.getLevel().getSpeedups().get(i).getSpeedup(true).render(g);
		for(int i = 0; i < gameState.level.getLevel().getPowerups().size(); i++)
			gameState.level.getLevel().getPowerups().get(i).getPowerup(true).render(g);
		
		// Draw Checkout
		scaleY = (float)(screenHeight / (float)checkout.getHeight());
		g.scale(scaleY, scaleY);
		g.drawImage(checkout, (float)((float)((float)gameState.level.getLevel().getLength() * (float)background.getWidth() + (float)flag.getWidth()) / scaleY), 0.0f);
		g.scale(1.0f / scaleY, 1.0f / scaleY);

		// Undo transforms
		g.resetTransform();
		
		// draw powerup area
		if (25 + back.getWidth() + myCart.getWorldX() - myCart.getX() / 2.0f <= gameState.level.getLevel().getLength() * 1000 && gameState.finish == 0) {
			back.draw(25,640);
		}
		
		//DEBUG: print mouse position
		g.drawString((input.getMouseX() + ", " + input.getMouseY()), 0, 30);
		g.drawString("speed: "+myCart.getCurrentSpeed(), 0, 50);
		// Print time
		if (gameState.timer > 3000){
			g.setColor(Color.white);
			if (gameState.finalTime != 0){
				g.drawString("Time: " + realTime(gameState.finalTime), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH - 200.0f, 676.0f);	
			} else {
				g.drawString("Time: " + realTime(gameState.timer - 3000), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH - 200.0f, 676.0f);
			}
			g.resetTransform();
		}

		// Draw the player
		for (String user : gameState.playerCarts.keySet()) {
			if (!user.equals(player.getUsername())) {
				Cart c = gameState.playerCarts.get(user).getCart(true);
				c.setX(c.getWorldX() + c.getX() - myCart.getWorldX());
				c.render(g);
			}
			else
				gameState.playerCarts.get(user).getCart(true).render(g);
		}

	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

		boolean frameChanged = false;
		do {

			player.getPlayerClient().setOkToRead();
			DataPackage state = player.getPlayerClient().getCurrentState();
			if (state == null)
				return;
			if (state.getState() != 100 && state.getState() != 0) {
	
				player.getPlayerClient().stopClient();
				game.enterState(BlackFridayBlitz.TITLE_STATE);
	
			}
			if (state.getGameState() == null)
				return;
	
			long frameState = 0;

			if (state.getGameState().frames.containsKey(player.getUsername()))
				 frameState = state.getGameState().frames.get(player.getUsername()).longValue();

			if (frame != frameState) {

				frame = frameState;
				frameChanged = true;
				player.getPlayerClient().updateGameState(player.getUsername(), player.getPlayerCart(), container, frameState);
				player.getPlayerClient().setOkToWrite();
				if (player.getPlayerClient().getGameState().inputs.get(player.getUsername()).get("up").booleanValue() == true)
					System.out.println("Multi Player frame: " + frame + " input: " + state.getGameState().inputs);

			}
			else if (frameState == 0) {
				frameChanged = true;
				player.getPlayerClient().updateGameState(player.getUsername(), player.getPlayerCart(), container, frameState);
				player.getPlayerClient().setOkToWrite();
			}

			

		} while(!frameChanged);

	}

	@Override
	public int getID() {

		return BlackFridayBlitz.MULTI_PLAYER_GAME_STATE_ID;

	}

}
