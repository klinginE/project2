package project2;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import jig.Collision;
import jig.ResourceManager;

import org.newdawn.slick.Color;
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
	ArrayList<Powerup> powerups;
	ArrayList<Weapon> weapons;
	private long timer = 0;
	private long pauseTimer;
	private long finalTime;
	private int cart;
	private int finish = 0;
	Image[] itemIcon;
	Image[] toggleIcon;

	public void setPlayer(int c) {
		cart = c;
		return;
	}
	
	public String realTime(long time){		
			return String.format("%02d:%02d.%02d", TimeUnit.MILLISECONDS.toMinutes(time), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)), (time - (TimeUnit.MILLISECONDS.toSeconds(time) * 1000)));			
	}


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
		//player = new Player(level.platformY.get(platform), cart);
		
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {

		level = new Level(50);
		speedups =  level.getSpeedups();
		powerups = level.getPowerups();
		weapons = new ArrayList<Weapon>();
		player = new Player(Level.platformY[1], cart);
		timer = 0;
		finish = 0;
		finalTime = 0;

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {

		float screenHeight = (float)BlackFridayBlitz.MAX_WINDOW_HEIGHT;
		Image back = ResourceManager.getImage(BlackFridayBlitz.BACK_PNG);
		Image background = ResourceManager.getImage(BlackFridayBlitz.BACKGROUND_PNG);
		Image flag = ResourceManager.getImage(BlackFridayBlitz.CHECKERED_FLAG_PNG);	
		Image checkout = ResourceManager.getImage(BlackFridayBlitz.CHECKOUT_JPG);
		Input input = container.getInput();
		flag = flag.getSubImage(0, 0, 256, flag.getHeight());

		float scaleY = (screenHeight - 100.0f) / (float)background.getHeight();

		// Translate background
		g.translate(-1.0f * (player.getPlayerCart().getWorldX() - Cart.MIN_SCREEN_X), 0.0f);

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
		
		
		// draw collectables
		for(int i = 0; i < speedups.size(); i++)
			speedups.get(i).render(g);
		for(int i = 0; i < powerups.size(); i++)
			powerups.get(i).render(g);
		//Draw weapons
		for(int i = 0; i < weapons.size(); i++)
			weapons.get(i).render(g);

		// Draw Checkout
		scaleY = (float)(screenHeight / (float)checkout.getHeight());
		g.scale(scaleY, scaleY);
		g.drawImage(checkout, (float)((float)((float)level.getLength() * (float)background.getWidth() + (float)flag.getWidth()) / scaleY), 0.0f);

		// Undo transforms
		g.resetTransform();
		
		// draw powerup area
		if (25 + back.getWidth() + player.getPlayerCart().getWorldX() - player.getPlayerCart().getX() / 2.0f <= level.getLength() * 1000 && finish == 0) {
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
		g.drawString("speed: "+player.getPlayerCart().getCurrentSpeed(), 0, 50);
		
		// Print time
		if (timer > 3000){
			g.setColor(Color.white);
			if (finalTime != 0){
				g.drawString("Time: " + realTime(finalTime), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH - 200.0f, 676.0f);	
			} else {
				g.drawString("Time: " + realTime(timer - 3000), (float)BlackFridayBlitz.MAX_WINDOW_WIDTH - 200.0f, 676.0f);
			}
			g.resetTransform();
		}
		
		// Draw the player
		player.getPlayerCart().render(g);
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
		Input input = container.getInput();

		timer += (long)delta;
		if (timer < 3000l) {
			input.clearControlPressedRecord();
			input.clearKeyPressedRecord();
			return;
		}
		if (finish == 1) {
			input = null;
		}
		
		player.getPlayerCart().update(input, delta);
		if (player.getPlayerCart().getX() >= ((float)BlackFridayBlitz.MAX_WINDOW_WIDTH) / 3.0f)
			player.getPlayerCart().setJumpPoint(440.0f);

		if (player.getPlayerCart().getWorldX() >= BlackFridayBlitz.MAX_WINDOW_WIDTH * level.getLength() + 200) {

			if (finish == 0) {
				finish = 1;
			finalTime = timer - 3000;
			pauseTimer = timer + 3000;
			}
			if (timer > pauseTimer){
				((SinglePlayerResultsState)game.getState(BlackFridayBlitz.SP_RESULTS_STATE)).setTime(cart, finalTime);
				game.enterState(BlackFridayBlitz.SP_RESULTS_STATE);
			}
			player.getPlayerCart().MAX_SCREEN_X = BlackFridayBlitz.MAX_WINDOW_WIDTH - 300;
			player.getPlayerCart().setWorldX(BlackFridayBlitz.MAX_WINDOW_WIDTH * level.getLength() + 200);
			return;
		}
		
		if (input.isKeyPressed(Input.KEY_UP) && player.getPlayerCart().getY() == player.getPlayerCart().getJumpPoint()) {
			if(player.getPlayerCart().getPlatform() < Level.platformY.length - 1) {
				player.getPlayerCart().setPlatform(player.getPlayerCart().getPlatform() + 1);
				player.getPlayerCart().setJumpPoint(Level.platformY[player.getPlayerCart().getPlatform()]);
			}
		}
		if (input.isKeyPressed(Input.KEY_DOWN) && player.getPlayerCart().getY() == player.getPlayerCart().getJumpPoint()) {
			if(player.getPlayerCart().getPlatform() > 0) {
				player.getPlayerCart().setPlatform(player.getPlayerCart().getPlatform() - 1);
				player.getPlayerCart().setJumpPoint(Level.platformY[player.getPlayerCart().getPlatform()]);
			}
		}
		if (input.isKeyPressed(Input.KEY_SPACE) && player.getPlayerCart().getY() == player.getPlayerCart().getJumpPoint() && player.getPowerup() != -1) {
			if (player.getPowerup() == 0 && player.getPlayerCart().getBatteryBoost() != 0){
			} else {
				weapons.add(player.fireWeapon());
			}
		}
		
		if (input.isKeyPressed(Input.KEY_TAB) && player.getPowerup() != -1) {
			player.toggleWeapon();
		}
		
		
		for(Iterator<Weapon> br = weapons.iterator(); br.hasNext();){
			Weapon wow = br.next();
			wow.update(delta);
			if (wow.end == 1)
				br.remove();
		}	
		
		for(Iterator<Powerup> br = powerups.iterator(); br.hasNext();){
			Powerup pow = br.next();
			
			pow.setX(pow.getX() - (player.getPlayerCart().getWorldX() - Cart.MIN_SCREEN_X));
			//System.out.println(pow.getX()+" ,"+ player.getPlayerCart().getX());
			if (player.getPlayerCart().collides(pow) != null && player.getPowerup() == -1){
				player.setPowerup(pow.pickup());
				br.remove();		
			}		
			pow.setX(pow.getX() + (player.getPlayerCart().getWorldX() - Cart.MIN_SCREEN_X));
		}
		
		
		for(Iterator<Speedup> br = speedups.iterator(); br.hasNext();){
			Speedup zoom = br.next();
			
			zoom.setX(zoom.getX() - (player.getPlayerCart().getWorldX() - Cart.MIN_SCREEN_X));
			//System.out.println(zoom.getX()+" ,"+ player.getPlayerCart().getX());
			if (player.getPlayerCart().collides(zoom) != null && zoom.getActive()){
				zoom.setActive(false);
				player.getPlayerCart().addSpeedUp();		
			}		
			zoom.setX(zoom.getX() + (player.getPlayerCart().getWorldX() - Cart.MIN_SCREEN_X));
		}		
	}
	
	

	@Override
	public int getID() {

		return BlackFridayBlitz.SINGLE_PLAYER_GAME_STATE_ID;

	}

}
