package project2;

import java.util.ArrayList;

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

	private Level level = null;
	ArrayList<Speedup> speedups;
	//ArrayList<Powerup> powerups;
	private int platform;
	private long timer = 0;
	private long pauseTimer;
	private long finalTime;
	private int cart;
	private int finish = 0;
	
	public void setPlayer(int c){
		cart = c;
		return;
	}


	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
		platform = 1;
		//player = new Player(level.platformY.get(platform), cart);
		
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {

		level = new Level(10);
		speedups =  level.getSpeedups();
		player = new Player(level.platformY.get(platform), cart);
		timer = 0;
		finish = 0;

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {

		float screenHeight = (float)BlackFridayBlitz.MAX_WINDOW_HEIGHT;

		Image background = ResourceManager.getImage(BlackFridayBlitz.BACKGROUND_PNG);
		Image flag = ResourceManager.getImage(BlackFridayBlitz.CHECKERED_FLAG_PNG);
		flag = flag.getSubImage(0, 0, 256, flag.getHeight());
		Image checkout = ResourceManager.getImage(BlackFridayBlitz.CHECKOUT_JPG);

		float scaleY = screenHeight / (float)background.getHeight();

		// Translate background
		g.translate(-1.0f * (player.getPlayerCart().getWorldX() - player.getPlayerCart().MIN_SCREEN_X), 0.0f);

		// Draw background
		g.scale(1.0f, scaleY);
		//int numPannels = level.getLength();//270
		for (int i = 0; i < level.getLength(); i++)
			g.drawImage(background, (float)(i * background.getWidth()), 0.0f);
		g.scale(1.0f, 1.0f/scaleY);

		if (timer <= 1000)
			g.drawImage(ResourceManager.getImage(BlackFridayBlitz.TRAFFICLIGHT_PNG).getSubImage(0, 0, 32, 64), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH / 2.0f, 50.0f);
		if (timer > 1000 && timer <= 2000)
			g.drawImage(ResourceManager.getImage(BlackFridayBlitz.TRAFFICLIGHT_PNG).getSubImage(32, 0, 32, 64), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH / 2.0f, 50.0f);
		if (timer > 2000 && timer <= 3000)
			g.drawImage(ResourceManager.getImage(BlackFridayBlitz.TRAFFICLIGHT_PNG).getSubImage(64, 0, 32, 64), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH / 2.0f, 50.0f);
		if (timer > 3000)
			g.drawImage(ResourceManager.getImage(BlackFridayBlitz.TRAFFICLIGHT_PNG).getSubImage(96, 0, 32, 64), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH / 2.0f, 50.0f);

		// Draw flag
		g.drawImage(flag, (float)(level.getLength() * background.getWidth()), 0.0f);
		// Draw items
		for(int i = 0; i < speedups.size(); i++)
			speedups.get(i).render(g);
		
		// Draw Checkout
		scaleY = (float)(screenHeight / (float)checkout.getHeight());
		g.scale(scaleY, scaleY);
		g.drawImage(checkout, (float)((float)((float)level.getLength() * (float)background.getWidth() + (float)flag.getWidth()) / scaleY), 0.0f);

		// Undo transforms
		g.resetTransform();

		// Print time
		g.drawString("Time: " + timer / 1000 + " sec", (float)BlackFridayBlitz.MAX_WINDOW_WIDTH - 200.0f, 16.0f);

		// Draw the player
		player.getPlayerCart().render(g);
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

		timer += (long)delta;
		if (timer < 3000l) {
			container.getInput().clearControlPressedRecord();
			container.getInput().clearKeyPressedRecord();
			return;
		}
		player.getPlayerCart().update(container, game, delta);
		if (player.getPlayerCart().getX() >= ((float)BlackFridayBlitz.MAX_WINDOW_WIDTH) / 3.0f)
			player.getPlayerCart().setJumpPoint(400.0f);

		if (player.getPlayerCart().getWorldX() >= BlackFridayBlitz.MAX_WINDOW_WIDTH * level.getLength() + 128) {

			if (finish == 0) {
				finish = 1;
			finalTime = timer;
			System.out.print(timer);
			pauseTimer = timer + 3000;
			}
			if (timer > pauseTimer){
				((SinglePlayerResultsState)game.getState(BlackFridayBlitz.SP_RESULTS_STATE)).setTime(cart, finalTime);
				game.enterState(BlackFridayBlitz.SP_RESULTS_STATE);
			}
			player.getPlayerCart().MAX_SCREEN_X = BlackFridayBlitz.MAX_WINDOW_WIDTH - 300;
			player.getPlayerCart().setWorldX(BlackFridayBlitz.MAX_WINDOW_WIDTH * level.getLength() + 128);
			return;
		}
		
		

		Input input = container.getInput();
		if (input.isKeyPressed(Input.KEY_UP) && player.getPlayerCart().getY() == player.getPlayerCart().getJumpPoint()) {
			if(platform < level.platformY.size() - 1) {
				platform++;
				player.getPlayerCart().setJumpPoint(level.platformY.get(platform));
			}
		}
		if (input.isKeyPressed(Input.KEY_DOWN) && player.getPlayerCart().getY() == player.getPlayerCart().getJumpPoint()) {
			if(platform > 0) {
				platform--; 
				player.getPlayerCart().setJumpPoint(level.platformY.get(platform));
			}
		}
	}
	
	

	@Override
	public int getID() {

		return BlackFridayBlitz.SINGLE_PLAYER_GAME_STATE_ID;

	}

}
