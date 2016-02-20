package project2;

import java.io.Serializable;
import java.util.Random;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;

public class Powerup extends Entity {

	private float worldX = 0.0f;
	private float worldY = 0.0f;
	private boolean active = true;
	private String imageString = "";
	@SuppressWarnings("unused")
	private static final Random spawn = new Random();

	public static class PowerupState implements Serializable {

		private static final long serialVersionUID = 2547080137547610618L;

		public float worldX_s = 0.0f;
		public float worldY_s = 0.0f;
		public boolean active_s = false;
		public String imageString_s = "";
		public float width_s = 0.0f;
		public float height_s = 0.0f;

		public PowerupState(String iString, float w_x, float w_y, boolean act, float w, float h) {

			super();
			worldX_s = w_x;
			worldY_s = w_y;
			active_s = act;
			imageString_s = iString;
			width_s = w;
			height_s = h;

		}

		public Powerup getPowerup(boolean withImage) {
			
			Powerup powup = null;

			if (withImage)
				powup = new Powerup(imageString_s, worldX_s, worldY_s);
			else
				powup = new Powerup(imageString_s, worldX_s, worldY_s, width_s, height_s);
			powup.active = active_s;
			
			return powup;

		}

	}

	public Powerup(String PowerupImage, float w_x, float w_y) {

		super();
		active = true;
		imageString = PowerupImage;
		Image i = ResourceManager.getImage(imageString);
		addImageWithBoundingBox(i);
		worldX = w_x;
		worldY = w_y;
		setX(worldX);
		setY(worldY);

	}

	public Powerup(String PowerupImage, float w_x, float w_y, float width, float height) {

		super();
		active = true;
		imageString = PowerupImage;
		addShape(new ConvexPolygon(width, height));
		worldX = w_x;
		worldY = w_y;
		setX(worldX);
		setY(worldY);

	}

	@Override
	public void render(Graphics g) {
		if(active)
			super.render(g);
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
	public void setActive(boolean act) {
		active = act;
	}
	public String getImageString() {
		return imageString;
	}
	public int pickup(){		
		active = false;
		return 0;//spawn.nextInt(4);
	}

}