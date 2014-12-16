package project2;

import java.io.Serializable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;

public class Speedup extends Entity {

	private float timer = 0.0f;
	private float worldX = 0.0f;
	private float worldY = 0.0f;
	private boolean active = true;
	private String imageString = "";

	public static class SpeedupState implements Serializable {

		private static final long serialVersionUID = -5785208796431631475L;
		public float timer_s = 0.0f;
		public float worldX_s = 0.0f;
		public float worldY_s = 0.0f;
		public boolean active_s = true;
		public String imageString_s = "";
		public float width_s = 0.0f;
		public float height_s = 0.0f;

		public SpeedupState(String iString, float time, float x_w, float y_w, boolean act, float width, float height) {

			super();
			timer_s = time;
			worldX_s = x_w;
			worldY_s = y_w;
			active_s = act;
			imageString_s = iString;
			width_s = width;
			height_s = height;

		}
		public Speedup getSpeedup(boolean withImage) {
			
			Speedup spup = null;

			if (withImage)
				spup = new Speedup(imageString_s, timer_s, worldX_s, worldY_s);
			else
				spup = new Speedup(imageString_s, timer_s, worldX_s, worldY_s, width_s, height_s);
			spup.active = active_s;
			
			return spup;

		}
		
	}
	
	public Speedup(String SpeedupImage, float time, float w_x, float w_y) {

		super();
		timer = time;
		active = true;
		imageString = SpeedupImage;
		Image i = ResourceManager.getImage(imageString);
		addImageWithBoundingBox(i);
		worldX = w_x;
		worldY = w_y;
		setX(worldX);
		setY(worldY);

	}
	
	public Speedup(String SpeedupImage, float time, float w_x, float w_y, float width, float height) {

		super();
		active = true;
		imageString = SpeedupImage;
		addShape(new ConvexPolygon(width, height));
		worldX = w_x;
		worldY = w_y;
		setX(worldX);
		setY(worldY);
		timer = time;
		

	}
	
	@Override
	public void render(Graphics g) {
		if(active)
		super.render(g);
	}

	
	public void update(GameContainer container, StateBasedGame game, int delta) {
			
			timer-=delta;
			if(timer <= 0)
				active = true;
	}
			

	public float getWorldX() {

		return worldX;

	}

	public float getWorldY() {

		return worldY;

	}
	
	public void getSpeedup() {
		timer = 2000;
		active = false; 
	}
	public boolean getActive() {
		return active;
	}

	public void setActive(boolean b) {
		active = b;
	}

	public String getImageString() {
		return imageString;
	}
	public float getTimer() {
		return timer;
	}

}