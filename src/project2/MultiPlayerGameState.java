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

import project2.Cart.CartState;

public class MultiPlayerGameState extends BasicGameState {

	private Player player = null;
	private long frame = 0;
	Image[] itemIcon;
	Image[] toggleIcon;

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
		itemIcon = new Image[4];
		toggleIcon = new Image[6];
		itemIcon[0] = ResourceManager.getImage(BlackFridayBlitz.WPICON_BATTERY_PNG);
		itemIcon[1] = ResourceManager.getImage(BlackFridayBlitz.WPICON_FIREWORK_PNG);
		itemIcon[2] = ResourceManager.getImage(BlackFridayBlitz.WPICON_PLASTICBAG_PNG);
		itemIcon[3] = ResourceManager.getImage(BlackFridayBlitz.WPICON_BOWLING_BALL_PNG);
		toggleIcon[0] = ResourceManager.getImage(BlackFridayBlitz.ARROWL_PNG);
		toggleIcon[1] = ResourceManager.getImage(BlackFridayBlitz.ARROWR_PNG);
		toggleIcon[2] = ResourceManager.getImage(BlackFridayBlitz.ARROWU_PNG);
		toggleIcon[3] = ResourceManager.getImage(BlackFridayBlitz.ARROWD_PNG);
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

		// Undo transforms
		g.resetTransform();
		
		// draw powerup area
		if (myCart.getWorldX() <= gameState.level.getLevel().getLength() * 1000) {
			back.draw(25,640);
			if (player.getPowerup() != -1){
				itemIcon[player.getPowerup()].draw(45, 660);
				if (player.getPowerup() == 2){
					if (player.getWeaponToggle() == 1){
						toggleIcon[2].draw(65, 640);
					} else { 
						toggleIcon[3].draw(65, 726); 
					}
				}
				if (player.getPowerup() == 3){
					if (player.getWeaponToggle() == 1){
						toggleIcon[0].draw(25, 680);
					} else { 
						toggleIcon[3].draw(65, 726); 
					}			
				}
			}
		}
		
		//DEBUG: print mouse position
		g.drawString((input.getMouseX() + ", " + input.getMouseY()), 0, 30);
		g.drawString("speed: "+myCart.getCurrentSpeed(), 0, 50);
		// draw progress bar
		g.setColor(Color.white);
		g.drawLine(150, 700, 750, 700);
		for (String user: gameState.playerCarts.keySet()) {
			g.drawImage(ResourceManager.getImage(gameState.playerCarts.get(user).getCart(true).getImageString()), 150 + 600.0f/(float)(gameState.level.getLevel().getNumXpixels()+200)*gameState.playerCarts.get(user).getCart(true).getWorldX(), 650);
		}
				
		// Print time
		if (gameState.timer > 3000){
			g.setColor(Color.white);
			if (gameState.timer != 0){
				g.drawString("Time: " + realTime(gameState.finalTime.get(player.getUsername())), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH - 200.0f, 676.0f);	
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

			if (!player.getPlayerClient().getIsRunning()) {

				//System.out.println("changing state from: " + this.getID() + "  to: " + BlackFridayBlitz.TITLE_STATE + " with object: " + game.getState(BlackFridayBlitz.TITLE_STATE) + " that has a state of: " + game.getState(BlackFridayBlitz.TITLE_STATE).getID());
				player.getPlayerClient().stopClient();
				game.enterState(BlackFridayBlitz.TITLE_STATE);
				//System.out.println("Should not have reached this line of code");
				System.exit(1);

			}
			player.getPlayerClient().setOkToRead();
			DataPackage state = player.getPlayerClient().getCurrentState();
			if (state == null)
				return;
			if (state.getState() != 100 && state.getState() != 0) {

				player.getPlayerClient().stopClient();
				game.enterState(BlackFridayBlitz.TITLE_STATE);
	
			}

			if (state.getGameState().done) {
				for (String key : state.getGameState().playerCarts.keySet()) {
					Cart c = state.getGameState().playerCarts.get(key).getCart(true);
					int type = 0;
					if (c.getImageString().equals(BlackFridayBlitz.PLAYER1_PNG))
						type = 0;
					else if (c.getImageString().equals(BlackFridayBlitz.PLAYER2_PNG))
						type = 1;
					else if (c.getImageString().equals(BlackFridayBlitz.PLAYER3_PNG))
						type = 2;
					else
						type = 3;
					((SinglePlayerResultsState)game.getState(BlackFridayBlitz.SP_RESULTS_STATE)).setTime(type, state.getGameState().finalTime.get(key));
				}
				player.getPlayerClient().stopClient();
				System.out.println("entering results");
				game.enterState(BlackFridayBlitz.SP_RESULTS_STATE);
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
//				if (player.getPlayerClient().getGameState().inputs.get(player.getUsername()).get("up").booleanValue() == true)
//					System.out.println("Multi Player frame: " + frame + " input: " + state.getGameState().inputs);

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
