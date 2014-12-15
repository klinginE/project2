package project2;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class BlackFridayBlitz extends StateBasedGame {

	public static final int MAX_WINDOW_WIDTH = 1000;
	public static final int MAX_WINDOW_HEIGHT = 740;
	public static final int TITLE_STATE = 0;
	public static final int MENU_STATE = 1;
	public static final int SINGLE_PLAYER_GAME_STATE_ID = 2;
	public static final int MULTI_PLAYER_GAME_STATE_ID = 3;
	public static final int SP_RESULTS_STATE = 4;
	public static final int MP_RESULTS_STATE = 5;
	
	private static final boolean FPS_ON = true;

	public static final String BACKGROUND_PNG = "resource/shelf.png";
	public static final String TITLEBG_JPG = "resource/store.jpg";
	public static final String CHECKERED_FLAG_PNG = "resource/checkeredFlag.png";
	public static final String CHECKOUT_JPG = "resource/Self-Checkout.jpg";
	public static final String TRAFFICLIGHT_PNG = "resource/trafficlight.png";
	public static final String SPEEDUP_PNG = "resource/speedup.png";
	public static final String POWERUP_PNG = "resource/baglight.png";
	public static final String PLAYER1_PNG = "resource/scooterRed.png";
	public static final String PLAYER2_PNG = "resource/scooterZombie.png";
	public static final String PLAYER3_PNG = "resource/scooterRobot.png";
	public static final String PLAYER4_PNG = "resource/scooterPumpkin.png";
	public static final String TITLE_PNG = "resource/logo.png";
	public static final String BUTTON_PNG = "resource/button.png";
	public static final String BUTTON2_PNG = "resource/button2.png";
	public static final String BACK_PNG = "resource/back.png";
	public static final String RECEIPT_JPG = "resource/receipt.jpg";
	public static final String RECEIPT_FONT = "resource/fakereceipt.ttf";
	public static final String WPICON_BOWLING_BALL_PNG = "resource/bowlingball.png";
	public static final String WPICON_FIREWORK_PNG = "resource/firework.png";
	public static final String WPICON_PLASTICBAG_PNG = "resource/fallingplasticbag.png";
	public static final String WPICON_BATTERY_PNG = "resource/battery.png";
	public static final String ARROWL_PNG = "resource/arrowL.png";
	public static final String ARROWR_PNG = "resource/arrowR.png";
	public static final String ARROWU_PNG = "resource/arrowU.png";
	public static final String ARROWD_PNG = "resource/arrowD.png";


	public BlackFridayBlitz(String name) {

		super(name);
		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);

	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {

		container.setShowFPS(FPS_ON);
		ResourceManager.loadImage(BACKGROUND_PNG);
		ResourceManager.loadImage(CHECKERED_FLAG_PNG);
		ResourceManager.loadImage(TRAFFICLIGHT_PNG);
		ResourceManager.loadImage(SPEEDUP_PNG);
		ResourceManager.loadImage(POWERUP_PNG);
		ResourceManager.loadImage(CHECKOUT_JPG);
		ResourceManager.loadImage(PLAYER1_PNG);
		ResourceManager.loadImage(PLAYER2_PNG);
		ResourceManager.loadImage(PLAYER3_PNG);
		ResourceManager.loadImage(PLAYER4_PNG);
		ResourceManager.loadImage(TITLE_PNG);
		ResourceManager.loadImage(TITLEBG_JPG);
		ResourceManager.loadImage(BUTTON_PNG);
		ResourceManager.loadImage(BACK_PNG);
		ResourceManager.loadImage(RECEIPT_JPG);
		ResourceManager.loadImage(WPICON_BOWLING_BALL_PNG);
		ResourceManager.loadImage(WPICON_FIREWORK_PNG);
		ResourceManager.loadImage(WPICON_PLASTICBAG_PNG);
		ResourceManager.loadImage(WPICON_BATTERY_PNG);
		ResourceManager.loadImage(ARROWL_PNG);
		ResourceManager.loadImage(ARROWR_PNG);
		ResourceManager.loadImage(ARROWU_PNG);
		ResourceManager.loadImage(ARROWD_PNG);
		addState(new TitleState());
		//addState(new MenuState());
		addState(new TitleState());
		addState(new SinglePlayerGameState());
		addState(new SinglePlayerResultsState());
		addState(new MultiPlayerGameState());
		//addState(new MultiPlayerResultsState());

	}

	public static void main(String[] args) {

		AppGameContainer app = null;
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
