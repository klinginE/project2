package project2;

import jig.Entity;
import jig.ResourceManager;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class BlackFridayBlitz extends StateBasedGame {

	public static final int MAX_WINDOW_WIDTH = 1000;
	public static final int MAX_WINDOW_HEIGHT = 640;
	public static final int SINGLE_PLAYER_GAME_STATE_ID = 0;
	public static final int MULTI_PLAYER_GAME_STATE_ID = 1;
	private static final boolean FPS_ON = true;

	public static final String BACKGROUND_JPG = "resource/shelves.jpg";
	public static final String CHECKERED_FLAG_PNG = "resource/checkeredFlag.png";
	public static final String CHECKOUT_JPG = "resource/Self-Checkout.jpg";
	public static final String TRAFFICLIGHT_PNG = "resource/trafficlight.png";
	public static final String PLAYER1_PNG = "resource/scooterRed.png";
	public static final String PLAYER2_PNG = "resource/scooterZombie.png";
	public static final String PLAYER3_PNG = "resource/scooterRobot.png";
	public static final String PLAYER4_PNG = "resource/scooterPumpkin.png";

	public BlackFridayBlitz(String name) {

		super(name);
		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);

	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {

		container.setShowFPS(FPS_ON);
		ResourceManager.loadImage(BACKGROUND_JPG);
		ResourceManager.loadImage(CHECKERED_FLAG_PNG);
		ResourceManager.loadImage(TRAFFICLIGHT_PNG);
		ResourceManager.loadImage(CHECKOUT_JPG);
		ResourceManager.loadImage(PLAYER1_PNG);
		ResourceManager.loadImage(PLAYER2_PNG);
		ResourceManager.loadImage(PLAYER3_PNG);
		ResourceManager.loadImage(PLAYER4_PNG);
		addState(new SinglePlayerGameState());

	}

	public static void main(String[] args) {

		AppGameContainer app;
		try {

			app = new AppGameContainer(new BlackFridayBlitz("Black Friday Blitz"));
			app.setDisplayMode(MAX_WINDOW_WIDTH, MAX_WINDOW_HEIGHT, false);
			app.setVSync(true);
			app.start();

		}
		catch (SlickException e) {
			e.printStackTrace();
		}

	}

}
