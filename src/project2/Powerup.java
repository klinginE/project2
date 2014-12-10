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
	Image[] itemIcon;
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
		itemIcon = new Image[4];
		itemIcon[0] = ResourceManager.getImage(BlackFridayBlitz.WPICON_BOWLING_BALL_PNG);
		itemIcon[1] = ResourceManager.getImage(BlackFridayBlitz.WPICON_FIREWORK_PNG);
		itemIcon[2] = ResourceManager.getImage(BlackFridayBlitz.WPICON_PLASTICBAG_PNG);
		itemIcon[3] = ResourceManager.getImage(BlackFridayBlitz.WPICON_BOWLING_BALL_PNG);
		
		
		
	}	
	
	public void pickup(){
		type = spawn.nextInt(4);
		active = false;
	}
	
}
