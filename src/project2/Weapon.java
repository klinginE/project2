package project2;

import jig.Entity;

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
	float originX;
	float originY;
	int timer = 0;
	int end = 0;
	Cart owner;
	

	public Weapon(Cart playerCart, int type, int weaponToggle, Player player) {
		System.out.print("I GOT TO WEAPON");
		this.type = type;
		owner = playerCart;
		originX = playerCart.getX();
		originY = playerCart.getCoarseGrainedMaxY();
		init(weaponToggle);
		
		
	}


	private void init(int weaponToggle) {
		if (type == 0){
			owner.setBatteryBoost(500.0f);
			
		}
		if (type == 1){
					
		}
		if (type == 2){
			
		}
		if (type == 3){
			
		}
	}
	
	public void update(int delta){
		timer += delta;
		if (type == 0){
			if (timer > 2000 && owner.getBatteryBoost() > 0){
				owner.setBatteryBoost(owner.getBatteryBoost() - 25); 				
			}
			if (timer > 0 && owner.getBatteryBoost() == 0){
				end = 1;
			}
			
		}
		if (type == 1){
					
		}
		if (type == 2){
			
		}
		if (type == 3){
			
		}
	}

}
