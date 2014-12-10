package project2;

import org.newdawn.slick.Image;

import jig.Entity;
import jig.ResourceManager;

public class Powerup extends Entity {
	
	private float worldX = 0.0f;
	private float worldY = 0.0f;
	private boolean active;

	
	public Powerup(String PowerupImage, float w_x, float w_y) {

		super();
		active = true;
		Image i = ResourceManager.getImage(PowerupImage);
		addImageWithBoundingBox(i);
		worldX = w_x;
		worldY = w_y;
		setX(worldX);
		setY(worldY);		
	}	
	
	
}
