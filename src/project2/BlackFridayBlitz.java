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

	public static final String BACKGROUND_PNG = "project2/shelves.png";
	public static final String STANDIN_PLAYER_PNG = "project2/standinPlayer.png";

	public BlackFridayBlitz(String name) {

		super(name);
		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);

	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {

		container.setShowFPS(FPS_ON);
		ResourceManager.loadImage(BACKGROUND_PNG);
		ResourceManager.loadImage(STANDIN_PLAYER_PNG);
		addState(new MultiPlayerGameState());
		//addState(new SinglePlayerGameState());

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