package project2;

import java.io.Serializable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import jig.Entity;
import jig.ResourceManager;

public class Cart extends Entity {

	private static final float MAX_SPEED = 1000.0f;
	public static final float MIN_SCREEN_X = 100.0f;
	public float MAX_SCREEN_X = 200.0f;
	private static final float MIN_WORLD_X = MIN_SCREEN_X;
	private static final int MAX_SPEED_UPS = 10;
	private static final float BOOST = 10.0f;
	private static final float ACCELERATION_RATE = 4.0f;

	private float currentSpeed = 0.0f;
	private int numSpeedUps = 0;
	private float worldX = 0.0f;
	private float worldY = 0.0f;
	private float jumpY = 0.0f;
	private String imageString = "";

	public static class CartState implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4599756143520475030L;

		public static final float MAX_SPEED_S = MAX_SPEED;
		public static final float MIN_SCREEN_X_S = MIN_SCREEN_X;
		public float MAX_SCREEN_X_S = 200.0f;
		public static final float MIN_WORLD_X_S = MIN_SCREEN_X_S;
		public static final int MAX_SPEED_UPS_S = MAX_SPEED_UPS;
		public static final float BOOST_S = BOOST;
		public static final float ACCELERATION_RATE_S = ACCELERATION_RATE;

		public float x_s = 0.0f;
		public float y_s = 0.0f;
		public float width_s = 0.0f;
		public float height_s = 0.0f;
		public int numSpeedUps_s = 0;
		public float currentSpeed_s = 0.0f;
		public float worldX_s = 0.0f;
		public float worldY_s = 0.0f;
		public float jumpY_s = 0.0f;
		public String imageString_s = "";

		public CartState(float x, float y, float width, float height, int numSpeed, float currSpeed, float wx, float wy, float jy, String iStr) {

			super();
			x_s = x;
			y_s = y;
			width_s = width;
			height_s = height;
			numSpeedUps_s = numSpeed;
			currentSpeed_s = currSpeed;
			worldX_s = wx;
			worldY_s = wy;
			jumpY_s = jy;
			imageString_s = iStr;

		}

		public Cart getCart() {

			Cart c = new Cart(imageString_s, worldX_s, worldY_s);
			c.setX(x_s);
			c.setY(y_s);
			c.setNumSpeedUps(numSpeedUps_s);
			c.setCurrentSpeed(currentSpeed_s);
			c.setJumpPoint(jumpY_s);
			return c;

		}

	}

	public Cart(String cartImage, float w_x, float w_y) {

		super();
		Image i = ResourceManager.getImage(cartImage);
		addImage(i);
		worldX = w_x;
		worldY = w_y;
		setX(worldX);
		setY(worldY);
		jumpY = getY();
		imageString = cartImage;
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

		if (jPoint >= 100 && jPoint <= 450)
			jumpY = jPoint;

	}

	public float getJumpPoint() {

		return jumpY;

	}

	public void addSpeedUp() {

		if (numSpeedUps < MAX_SPEED_UPS)
			numSpeedUps++;

	}
	public void setNumSpeedUps(int num) {

		numSpeedUps = num;
		if (numSpeedUps < 0)
			numSpeedUps = 0;
		if (numSpeedUps > MAX_SPEED_UPS)
			numSpeedUps = MAX_SPEED_UPS;

	}
	public int getNumSpeedUps() {

		return numSpeedUps;

	}

	public String getImageString() {

		return imageString;

	}

	public void update(GameContainer container, StateBasedGame game, int delta) {

		Input input = container.getInput();
		float additionalSpeed = 0.0f;
		if (currentSpeed > MAX_SPEED || input.isKeyDown(Input.KEY_LEFT)) {

			currentSpeed -= ACCELERATION_RATE;
			if (currentSpeed < (-1.0f * MAX_SPEED))
				currentSpeed = (-1.0f * MAX_SPEED);

		}
		if (currentSpeed < (-1.0f * MAX_SPEED) || input.isKeyDown(Input.KEY_RIGHT)) {

			if (input.isKeyDown(Input.KEY_RIGHT) && currentSpeed >= 500.0f)
				additionalSpeed = 500.0f;
			currentSpeed += ACCELERATION_RATE;
			if (currentSpeed > MAX_SPEED)
				currentSpeed = MAX_SPEED;

		}
		if (getY() > jumpY) {

			setY(getY() - (ACCELERATION_RATE * 1.5f));
			if (getY() < jumpY)
				setY(jumpY);

		}
		if (getY() < jumpY) {

			setY(getY() + (ACCELERATION_RATE * 2.0f));
			if (getY() > jumpY)
				setY(jumpY);

		}

		worldX += ((currentSpeed + (BOOST * numSpeedUps) + additionalSpeed) * (delta / 1000.0f));
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

	public float getCurrentSpeed() {

		return currentSpeed;

	}
	public void setCurrentSpeed(float speed) {

		currentSpeed = speed;
		if (currentSpeed < 0)
			currentSpeed = 0;
		if (currentSpeed > MAX_SPEED + BOOST * numSpeedUps)
			currentSpeed = MAX_SPEED + BOOST * numSpeedUps;

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

}