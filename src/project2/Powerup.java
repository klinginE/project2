package project2;

import org.newdawn.slick.Image;

import jig.Entity;

public class Powerup extends Entity {
	
	int type;
	Image[] icon;
	int weaponToggle = 0;
	
	public Powerup(int type){
		this.type = type;
	}
	
	public void toggleWeapon(){
		if (weaponToggle == 0){
			weaponToggle = 1;
		} else {
			weaponToggle = 0;
		}
	}

}
