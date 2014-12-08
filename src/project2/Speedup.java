package project2;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import project2.BlackFridayBlitz;
import jig.Entity;
import jig.ResourceManager;

public class Speedup extends Entity {

	private final float MAX_SPEED = 2400.0f;
	private float accelerationRate = 20.0f;
	private float timer = 2000;
	private float boost = 0.0f;
	private float worldX = 0.0f;
	private float worldY = 0.0f;
	private float jumpY = 0.0f;
	private boolean active;

	public Speedup(String SpeedupImage, float w_x, float w_y) {

		super();
		active = false;
		Image i = ResourceManager.getImage(SpeedupImage);
		addImageWithBoundingBox(i);
		worldX = w_x;
		worldY = w_y;
		setX(worldX);
		setY(worldY);
		

	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
	}

	
	public void update(GameContainer container, StateBasedGame game, int delta) {
			timer-=delta;
			if(timer < 0)
				active = false;
			else active = true;
	}
			

	public float getWorldX() {

		return worldX;

	}

	public float getWorldY() {

		return worldY;

	}

	public boolean getActive() {
		return active;
	}

}
	
	
	

