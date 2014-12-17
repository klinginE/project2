package project2;

import java.io.Serializable;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

import project2.Cart.CartState;
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
	float worldX = 0.0f;
	float worldY = 0.0f;
	int timer = 0;
	int end = 0;
	int lastKnownFrame = 0;
	Cart owner;
	String username = "";
	private Animation i;
	int platform;
	int targetPlatform;
	int falling = 0;
	int rising = 0;
	int fall = 0;
	int down = 0;
	private Shape shape;
	private Image image;

	public static class WeaponState implements Serializable {

		private static final long serialVersionUID = -8132863400619235281L;

		public int type_s;
		public int togle_s;
		public float x_s;
		public float y_s;
		public float worldX_s;
		public float worldY_s;
		public int timer_s = 0;
		public int end_s = 0;
		public int lastKnownFrame_s = 0;
		public float height_s = 0;
		public float width_s = 0;
		public int platform_s;
		public int targetPlatform_s;
		public int falling_s = 0;
		public int rising_s = 0;
		public int fall_s = 0;
		public int down_s = 0;
		public CartState owner_s;
		public String username_s = "";

		public WeaponState(int t, int tog, float x, float y, float w_x, float w_y, int time, int e, int lastFrame, float height, float width, int plat, int targPlat, int falling, int rise, int fall, int down, CartState own, String username) {

			type_s = t;
			togle_s = tog;
			x_s = x;
			y_s = y;
			worldX_s = w_x;
			worldY_s = w_y;
			timer_s = time;
			end_s = e;
			lastKnownFrame_s = lastFrame;
			height_s = height;
			width_s = width;
			platform_s = plat;
			targetPlatform_s = targPlat;
			falling_s = falling;
			rising_s = rise;
			fall_s = fall;
			down_s = down;
			owner_s = own;
			username_s = username;

		}

		public Weapon getWeapon(boolean withImage) {

			Weapon wep = null;
			if (withImage)
				wep = new Weapon(owner_s.getCart(true), x_s, y_s, worldX_s, worldY_s, username_s, type_s, togle_s, lastKnownFrame_s, true);
			else {
				wep = new Weapon(owner_s.getCart(false), x_s, y_s, worldX_s, worldY_s, username_s, type_s, togle_s, lastKnownFrame_s, false);
				wep.addShape(new ConvexPolygon(width_s, height_s));
			}

			wep.timer = timer_s;
			wep.end = end_s;
			wep.platform = platform_s;
			wep.targetPlatform = platform_s;
			wep.falling = falling_s;
			wep.rising = rising_s;
			wep.fall = fall_s;
			wep.down = down_s;

			return wep;

		}

	}

	public Weapon(Cart playerCart, float x, float y, float wx, float wy, String user, int type, int weaponToggle, int frame, boolean createAnimations) {
		//super(playerCart.getWorldX() + Cart.MIN_SCREEN_X, playerCart.getY());
		super(x, y);
		username = user;
		this.type = type;
		toggle = weaponToggle;
		owner = playerCart;
		worldX = wx;
		worldY = wy;
		platform = playerCart.getPlatform();
		lastKnownFrame = frame;
		if (createAnimations)
			init(lastKnownFrame);
		
		
	}


	private void init(int frame) {
		if (type == 0){
			setX(owner.getWorldX() + owner.getCoarseGrainedMinX() - owner.getCoarseGrainedWidth() - 3);
			i = new Animation(ResourceManager.getSpriteSheet(BlackFridayBlitz.WP_AURA_PNG, 81, 104), 0, 0, 3, 0, true, 50, true);
			//addShape(new ConvexPolygon(81, 104));
			i.setCurrentFrame(frame);
			addAnimation(i);
			i.setLooping(true);
			owner.setBatteryBoost(500.0f);
			
		}
		if (type == 1){
			i = new Animation(ResourceManager.getSpriteSheet(BlackFridayBlitz.WP_ROCKET_PNG, 73, 20), 0, 0, 1, 0, true, 50, true);
			i.setCurrentFrame(frame);
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
				i.setCurrentFrame(frame);
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
			i.setCurrentFrame(frame);
			addAnimation(i);
			i.setLooping(true);
			shape = new ConvexPolygon(50, 52);
			addShape(shape);
			if (platform > 0) {
			targetPlatform = platform - 1;
			} else {targetPlatform = platform;}
			if (toggle == 0) {
				falling = 1;
			}
		}
	}

	public void setOwner(Cart c) {
		owner = c;
	}
	public String getUsername() {
		return username;
	}
	public int getCurrentAnimationFrame() {
		if (i != null)
			return i.getFrame();
		return 0;
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
