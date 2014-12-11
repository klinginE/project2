package project2;

import java.util.Random;

import org.newdawn.slick.Image;

import jig.Entity;
import jig.ResourceManager;

public class Powerup extends Entity {
	
	private float worldX = 0.0f;
	private float worldY = 0.0f;
	private boolean active;
	Random spawn = new Random();
	Image image;
	int type;
	

	
	public Powerup(String PowerupImage, float w_x, float w_y) {

		super();
		active = true;
		image = ResourceManager.getImage(PowerupImage);
		addImageWithBoundingBox(image);
		worldX = w_x;
		worldY = w_y;
		setX(worldX);
		setY(worldY);	

		
		
		
	}	
	
	public int pickup(){
		return spawn.nextInt(4);
	}
	
}
