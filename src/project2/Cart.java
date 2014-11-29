package project2;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import jig.Entity;
import jig.ResourceManager;

public class Cart extends Entity {

	private final float MAX_SPEED = 1600.0f;
	public final float MIN_SCREEN_X = 400.0f;
	private final float MAX_SCREEN_X = (float)BlackFridayBlitz.MAX_WINDOW_WIDTH - MIN_SCREEN_X;
	private final float MIN_WORLD_X = MIN_SCREEN_X;
	private float currentSpeed = 0.0f;
	private float accelerationRate = 4.0f;
	private float boost = 0.0f;
	private float worldX = 0.0f;
	private float worldY = 0.0f;
	private float jumpY = 0.0f;

	public Cart(String cartImage, float w_x, float w_y) {

		super();
		Image i = ResourceManager.getImage(cartImage);
		addImage(i);
		worldX = w_x;
		worldY = w_y;
		setX(worldX);
		setY(worldY);
		jumpY = getY();

	}

	@Override
	public void render(Graphics g) {
		super.render(g);
	}

	public void setJumpPoint(float jPoint) {

		if (jPoint >= 100 && jPoint <= 450)
			jumpY = jPoint;

	}

	public float getJumpPoint() {

		return jumpY;

	}

	public void update(GameContainer container, StateBasedGame game, int delta) {

		Input input = container.getInput();
		if (currentSpeed > MAX_SPEED || input.isKeyDown(Input.KEY_LEFT)) {
			currentSpeed -= accelerationRate;
			if (currentSpeed < (-1.0f * MAX_SPEED))
				currentSpeed = (-1.0f * MAX_SPEED);
		}
		if (currentSpeed < (-1.0f * MAX_SPEED) || input.isKeyDown(Input.KEY_RIGHT)) {
			currentSpeed += accelerationRate;
			if (currentSpeed > MAX_SPEED)
				currentSpeed = MAX_SPEED;
		}
		if (getY() > jumpY) {

			setY(getY() - (Math.abs(currentSpeed) * (delta / 1000.0f)));
			if (getY() < jumpY)
				setY(jumpY);

		}
		if (getY() < jumpY) {

			setY(getY() + (Math.abs(currentSpeed) * (delta / 1000.0f)));
			if (getY() > jumpY)
				setY(jumpY);

		}

		worldX += ((currentSpeed + boost) * (delta / 1000.0f));
		if (getX() >= MIN_SCREEN_X || getX() <= MAX_SCREEN_X) {

			setX(getX() + ((currentSpeed + boost) * (delta / 1000.0f)));
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

	public float getWorldY() {

		return worldY;

	}

}