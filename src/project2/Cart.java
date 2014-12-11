package project2;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import jig.Entity;
import jig.ResourceManager;

public class Cart extends Entity {

	private final float MAX_SPEED = 1000.0f;
	public final float MIN_SCREEN_X = 100.0f;
	public float MAX_SCREEN_X = 200.0f;
	private final float MIN_WORLD_X = MIN_SCREEN_X;
	private final int MAX_SPEED_UPS = 10;
	private float BOOST = 10.0f;
	private float currentSpeed = 0.0f;
	private float accelerationRate = 4.0f;
	private float decelerationRate = 16.0f;
	private int numSpeedUps = 0;
	private float worldX = 0.0f;
	private float worldY = 0.0f;
	private float jumpY = 0.0f;
	private boolean keyleft, keyright;

	public Cart(String cartImage, float w_x, float w_y) {

		super();
		Image i = ResourceManager.getImage(cartImage);
		addImageWithBoundingBox(i);
		//addImage(i);
		worldX = w_x;
		worldY = w_y;
		setX(worldX);
		setY(worldY);
		jumpY = getY();
		if (getX() >= MIN_SCREEN_X || getX() <= MAX_SCREEN_X) {

			if (getX() > MAX_SCREEN_X)
				setX(MAX_SCREEN_X);
			if (getX() < MIN_SCREEN_X)
				setX(MIN_SCREEN_X);

		}
		if (worldX < MIN_WORLD_X) {

			worldX = MIN_WORLD_X;
			currentSpeed = 0;

		}

	}

	@Override
	public void render(Graphics g) {

		if (jumpY > getY())
			g.rotate(getX(), getY(), 25.0f);
		if (jumpY < getY())
			g.rotate(getX(), getY(), -45.0f);
		super.render(g);
		g.resetTransform();

	}

	public void setJumpPoint(float jPoint) {

			jumpY = jPoint;

	}

	public float getJumpPoint() {

		return jumpY;

	}

	public void addSpeedUp() {

		if (numSpeedUps < MAX_SPEED_UPS)
			numSpeedUps++;

	}

	public void update(Input input, StateBasedGame game, int delta) {

		float additionalSpeed = 0.0f;
		if (currentSpeed > MAX_SPEED)
			currentSpeed = MAX_SPEED;
		if (currentSpeed < (-1.0f * MAX_SPEED))
			currentSpeed = (-1.0f * MAX_SPEED);

		if (input != null) {
			if(input.isKeyDown(Input.KEY_LEFT)) {
				if(currentSpeed > 0) 
					currentSpeed -= decelerationRate;
				else currentSpeed -= accelerationRate;
			}
				
		
			if (input.isKeyDown(Input.KEY_RIGHT)) {
				keyright = true;
				if(currentSpeed < 0)
					currentSpeed += decelerationRate;
				else currentSpeed +=  accelerationRate;
			
			}
			if(!input.isKeyDown(Input.KEY_RIGHT) && !input.isKeyDown(Input.KEY_LEFT)) {
				if(currentSpeed > 0)
					currentSpeed -= accelerationRate;
				else currentSpeed += accelerationRate;
				
			}
		}
		if (getY() > jumpY) {

			setY(getY() - (accelerationRate * 1.5f));
			if (getY() < jumpY)
				setY(jumpY);

		}
		if (getY() < jumpY) {

			setY(getY() + (accelerationRate * 2.0f));
			if (getY() > jumpY)
				setY(jumpY);

		}
			
		float boost = 0.0f;
		if(keyright)
			boost = BOOST * numSpeedUps;
		worldX += ((currentSpeed + boost + additionalSpeed) * (delta / 1000.0f));
		if (getX() >= MIN_SCREEN_X || getX() <= MAX_SCREEN_X) {

			setX(getX() + ((currentSpeed + (BOOST * numSpeedUps) + additionalSpeed) * (delta / 1000.0f)));
			if (getX() > MAX_SCREEN_X)
				setX(MAX_SCREEN_X);
			if (getX() < MIN_SCREEN_X)
				setX(MIN_SCREEN_X);

		}
		if (worldX < MIN_WORLD_X) {

			worldX = MIN_WORLD_X;
			currentSpeed = 0;

		}

	}

	public float getWorldX() {

		return worldX;

	}
	public void setWorldX(float wx) {

		worldX = wx;

	}

	public float getWorldY() {

		return worldY;

	}

	public float getCurrentSpeed() {
		// TODO Auto-generated method stub
		return currentSpeed;
	}

}