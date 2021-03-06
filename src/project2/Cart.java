package project2;

import java.io.Serializable;
import java.util.HashMap;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;

public class Cart extends Entity {

	private static final float MAX_SPEED = 1000.0f;
	public static final float MIN_SCREEN_X = 100.0f;
	public float MAX_SCREEN_X = 200.0f;
	private static final float MIN_WORLD_X = MIN_SCREEN_X;
	private static final int MAX_SPEED_UPS = 10;
	private static final float BOOST = 20.0f;
	private static final float BOOST_ACCEL = .3f;
	private static final float BATTERY_ACCEL = 8f;
	private static final float ACCELERATION_RATE = 4.0f;
	private static final float DECCELERATION_RATE = 16.0f;
	private float boost = 0;
	private float currentSpeed = 0.0f;
	private float batteryBoost = 0.0f;
	private int numSpeedUps = 0;
	private float worldX = 0.0f;
	private float worldY = 0.0f;
	private int platform = 1;
	private float jumpY = 0.0f;
	private boolean keyleft, keyright;
	private String imageString = "";

	public static class CartState implements Serializable {

		private static final long serialVersionUID = 4599756143520475030L;

		public static final float MAX_SPEED_S = MAX_SPEED;
		public static final float MIN_SCREEN_X_S = MIN_SCREEN_X;
		public float MAX_SCREEN_X_S = 200.0f;
		public static final float MIN_WORLD_X_S = MIN_SCREEN_X_S;
		public static final int MAX_SPEED_UPS_S = MAX_SPEED_UPS;
		public static final float BOOST_S = BOOST;
		public static final float ACCELERATION_RATE_S = ACCELERATION_RATE;
		public static final float DECCELERATION_RATE_S = DECCELERATION_RATE;

		public float x_s = 0.0f;
		public float y_s = 0.0f;
		public float width_s = 0.0f;
		public float height_s = 0.0f;
		public int numSpeedUps_s = 0;
		public float currentSpeed_s = 0.0f;
		public float batteryBoost_s = 0.0f;
		public float boost_s = 0.0f;
		public float worldX_s = 0.0f;
		public float worldY_s = 0.0f;
		public int platform_s = 1;
		public float jumpY_s = 0.0f;
		public String imageString_s = "";
		public boolean keyleft_s, keyright_s;

		public CartState(float x, float y, float width, float height, int numSpeed, float currSpeed, float bboost, float wx, float wy, int plat, float jy, String iStr, float maxScreen, boolean kleft, boolean kright, float b) {

			super();
			x_s = x;
			y_s = y;
			width_s = width;
			height_s = height;
			numSpeedUps_s = numSpeed;
			currentSpeed_s = currSpeed;
			batteryBoost_s = bboost;
			worldX_s = wx;
			worldY_s = wy;
			platform_s = plat;
			jumpY_s = jy;
			imageString_s = iStr;
			MAX_SCREEN_X_S = maxScreen;
			keyleft_s = kleft;
			keyright_s = kright;
			boost_s = b;

		}

		public String toString() {

			String s = "{" + x_s + "," + y_s + "," + width_s + "," + height_s + "," + numSpeedUps_s + "," + currentSpeed_s + "," + worldX_s + "," + worldY_s + "," + platform_s + "," + jumpY_s + "," + imageString_s + "}";
			return s;

		}

		public Cart getCart(boolean withImage) {

			Cart c = null;
			if (withImage)
				c = new Cart(imageString_s, worldX_s, worldY_s);
			else
				c = new Cart(imageString_s, worldX_s, worldY_s, width_s, height_s);
			c.setX(x_s);
			c.setY(y_s);
			c.setNumSpeedUps(numSpeedUps_s);
			c.setCurrentSpeed(currentSpeed_s);
			c.setBatteryBoost(batteryBoost_s);
			c.setPlatform(platform_s);
			c.setJumpPoint(jumpY_s);
			c.MAX_SCREEN_X = MAX_SCREEN_X_S;
			c.keyleft = keyleft_s;
			c.keyright = keyright_s;
			c.boost = boost_s;
			return c;

		}

	}

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

	public Cart(String cartImage, float w_x, float w_y, float width, float height) {

		super();
		addShape(new ConvexPolygon(width, height));
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

	public int getPlatform() {

		return platform;

	}
	public void setPlatform(int p) {

		platform = p;

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


	public void update(HashMap<String, Boolean> inputs, long delta) {

		boost = batteryBoost + BOOST * numSpeedUps;
		if (currentSpeed > MAX_SPEED + BOOST * numSpeedUps)
			currentSpeed = MAX_SPEED + BOOST * numSpeedUps;
		if(currentSpeed < 0)
			currentSpeed = 0;
		
		
		if (inputs != null) {
			if(inputs.get("left").booleanValue()) {
					currentSpeed -= DECCELERATION_RATE;
					if(currentSpeed < 0)
						currentSpeed = 0;
			}
			if(inputs.get("right").booleanValue()) {
				if(getBatteryBoost() > 0) 
					currentSpeed += ACCELERATION_RATE + (numSpeedUps * BOOST_ACCEL) + BATTERY_ACCEL;
				else currentSpeed += ACCELERATION_RATE + (numSpeedUps * BOOST_ACCEL);
			}		
			if(!inputs.get("right").booleanValue() && !inputs.get("right").booleanValue()) {
				currentSpeed -= ACCELERATION_RATE;
				if(currentSpeed <= ACCELERATION_RATE && currentSpeed >= -1*DECCELERATION_RATE)
					currentSpeed = 0;
				
			}
			worldX += ((currentSpeed + batteryBoost) * (delta / 1000.0f));
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
	
	if (getX() >= MIN_SCREEN_X || getX() <= MAX_SCREEN_X) {


			setX(getX() + ((currentSpeed) * (delta / 1000.0f)));
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

	public void update(Input input, int delta) {
		boost = batteryBoost + BOOST * numSpeedUps;
		if (currentSpeed > MAX_SPEED + BOOST * numSpeedUps)
			currentSpeed = MAX_SPEED + BOOST * numSpeedUps;
		if(currentSpeed < 0)
			currentSpeed = 0;
		
		
		if (input != null) {
			if(input.isKeyDown(Input.KEY_LEFT)) {
					currentSpeed -= DECCELERATION_RATE;
					if(currentSpeed < 0)
						currentSpeed = 0;
			}
			if(input.isKeyDown(Input.KEY_RIGHT)) {
				if(getBatteryBoost() > 0) 
					currentSpeed += ACCELERATION_RATE + (numSpeedUps * BOOST_ACCEL) + BATTERY_ACCEL;
				else currentSpeed += ACCELERATION_RATE + (numSpeedUps * BOOST_ACCEL);
			}		
			if(!input.isKeyDown(Input.KEY_RIGHT) && !input.isKeyDown(Input.KEY_LEFT)) {
				currentSpeed -= ACCELERATION_RATE;
				if(currentSpeed <= ACCELERATION_RATE && currentSpeed >= -1*DECCELERATION_RATE)
					currentSpeed = 0;
				
			}
			worldX += ((currentSpeed + batteryBoost) * (delta / 1000.0f));
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
	
	if (getX() >= MIN_SCREEN_X || getX() <= MAX_SCREEN_X) {


			setX(getX() + ((currentSpeed) * (delta / 1000.0f)));
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

		return currentSpeed + batteryBoost;

	}
	public void setCurrentSpeed(float speed) {

		currentSpeed = speed;
		if (currentSpeed < 0)
			currentSpeed = 0;
		if (currentSpeed > MAX_SPEED + boost)
			currentSpeed = MAX_SPEED + boost;

	}

	public float getBatteryBoost() {
		return batteryBoost;
	}
	public float getBoost() {
		return boost;
	}
	public void setBatteryBoost(float bboost) {
		batteryBoost = bboost;
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

	public float getMaxSpeed() {
		return 1000;
	}
	public boolean getKeyleft() {
		return keyleft;
	}
	public boolean getKeyright() {
		return keyright;
	}
}


