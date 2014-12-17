package project2;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import jig.Shape;

public class Weapon extends Entity{
	
	
	/*
	 * 
	 * TYPE 0 : BATTERY
	 * TYPE 1 : ROCKET
	 * TYPE 2 : BAG
	 * TYPE 3 : BALL
	 *
	 */
	
	int type;
	int toggle;
	float worldX;
	float worldY;
	int timer = 0;
	int end = 0;
	Cart owner;
	private Animation i;
	int platform;
	int targetPlatform;
	int falling = 0;
	int rising = 0;
	Image image;
	Shape shape;
	private int fall = 0;
	private int down = 0;
	

	public Weapon(Cart playerCart, int type, int weaponToggle, Player player) {
		super(playerCart.getWorldX() + Cart.MIN_SCREEN_X, playerCart.getY());
		this.type = type;
		toggle = weaponToggle;
		owner = playerCart;
		worldX = playerCart.getWorldX();
		worldY = playerCart.getWorldY();
		platform = playerCart.getPlatform();
		init();
		
		
	}


	private void init() {
		if (type == 0){
			setX(owner.getWorldX() + owner.getCoarseGrainedMinX() - owner.getCoarseGrainedWidth() - 3);
			i = new Animation(ResourceManager.getSpriteSheet(BlackFridayBlitz.WP_AURA_PNG, 81, 104), 0, 0, 3, 0, true, 50, true);
			//addShape(new ConvexPolygon(81, 104));
			addAnimation(i);
			i.setLooping(true);
			owner.setBatteryBoost(500.0f);
			
		}
		if (type == 1){
			i = new Animation(ResourceManager.getSpriteSheet(BlackFridayBlitz.WP_ROCKET_PNG, 73, 20), 0, 0, 1, 0, true, 50, true);
			shape = new ConvexPolygon(50, 52);
			addShape(shape);
			addAnimation(i);
			i.setLooping(true);	
		}
		if (type == 2){
			setY(getY() + 30);
			if (toggle == 0){ //down
				setX(getX() - owner.getCoarseGrainedWidth());
				image = ResourceManager.getImage(BlackFridayBlitz.WP_BAGREST_PNG);
				addImageWithBoundingBox(image); 
				down = 1;

				
			} else { //up and drop
				i = new Animation(ResourceManager.getSpriteSheet(BlackFridayBlitz.WP_BAGDOWN_PNG, 52, 60), 0, 0, 3, 0, true, 50, true);
				shape = new ConvexPolygon(50, 52);
				addShape(shape);
				i.setLooping(true);

				if (platform < Level.platformY.length - 1){
					targetPlatform = platform + 1;
					image = ResourceManager.getImage(BlackFridayBlitz.WP_BAGUP_PNG);
					addImageWithBoundingBox(image);
					rising = 1;
				} else {targetPlatform = platform;
					falling = 1;
				}
			}
		}
		if (type == 3){
			setX(owner.getWorldX() + owner.getCoarseGrainedMinX() - owner.getCoarseGrainedWidth());
			setY(getY() + 7);
			i = new Animation(ResourceManager.getSpriteSheet(BlackFridayBlitz.WP_BALL_PNG, 50, 52), 0, 0, 3, 0, true, 50, true);
			addAnimation(i);
			i.setLooping(true);
			if (platform > 0) {
			targetPlatform = platform - 1;
			} else {targetPlatform = platform;}
			if (toggle == 0) {
				falling = 1;
			}
		}
	}
	
	public void update(int delta){	
		timer += delta;
			
		//BATTERY
		if (type == 0){
			setX(owner.getWorldX() + owner.getCoarseGrainedMinX() - owner.getCoarseGrainedWidth() - 3);
			setY(owner.getY() - 7);
			if (timer > 2000 && owner.getBatteryBoost() > 0){
				owner.setBatteryBoost(owner.getBatteryBoost() - 25); 				
			}
			if (timer > 0 && owner.getBatteryBoost() == 0){
				end = 1;
			}
			
		}
		
		// ROCKET
		if (type == 1){
			if (timer < 1000){
				setX(getX() + delta * 2.5f);
			}
			if (timer > 1000 && fall == 0){
				removeShape(shape);
				removeAnimation(i);
				i = new Animation(ResourceManager.getSpriteSheet(BlackFridayBlitz.WP_FIREWORKS_PNG, 300, 300), 0, 0, 3, 0, true, 50, true);
				shape  = new ConvexPolygon(300, 300);
				addShape(shape);
				addAnimation(i);
				i.setLooping(true);
				fall = 1;
			} if (timer > 3000 && fall == 1){
				end = 1;
			}
		}
		
		//BAG
		if (type == 2){	
			if (rising == 1){
				if (getY() > Level.platformY[targetPlatform] - 30){
					setY(getY() - delta * .25f);
				} else {
					rising = 0;
					fall = 1;
				}
			}
			if (fall == 1){
				removeImage(image);
				addAnimation(i);
				if (platform > 0) {
					targetPlatform = platform - 1;
				} else {targetPlatform = platform;}
				fall = 0;
				falling = 1;
			}
			if (falling == 1){
				if (getY() < Level.platformY[targetPlatform] + 30){
					setY(getY() + delta * .25f);
				} else { 
					down = 1; 
					falling = 0;
					//System.out.println("*sound of plastic bag hitting floor*");
				}
			if (down == 1){
				if (toggle == 1){
					removeAnimation(i);
					image = ResourceManager.getImage(BlackFridayBlitz.WP_BAGREST_PNG);
					addImageWithBoundingBox(image);
					toggle = 0;
				}
			}
				
			}
		}
		
		//BALL
		if (type == 3){
			if (falling == 0){	
				setX(getX() - delta * .25f);
			} else {
				if (getY() < Level.platformY[targetPlatform] + 7){
					setY(getY() + delta * .125f);
				} else { 
					falling = 0; 
					//System.out.println("HIT!");
				}
			}
			if (getX() < 100) end = 1;
		}
	}
}
