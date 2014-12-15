package project2;

import java.util.ArrayList;
import java.util.Random;


public class Level {

	int length, numXpixels, dspawnPoint;
	ArrayList<Float> platformY;
	ArrayList<Speedup> speedups;
	ArrayList<Powerup> powerups;
	Random random = new Random();
	


	public Level(int length) {
		
		this.length = length;
		speedups = new ArrayList<Speedup>();
		powerups = new ArrayList<Powerup>();
		platformY = new ArrayList<Float>();
		platformY.add(520.0f); //floor
		platformY.add(358.0f); //1st platform
		platformY.add(213.0f); //2nd platform
		platformY.add(70.0f); //3rd platform
		
		
		
		
		//speedups.add(new Speedup(BlackFridayBlitz.SPEEDUP_PNG, 800, platformY.get(1)));
		numXpixels = length*1000; 
		int spawnPoints = numXpixels/250;
		
		for(int i = 0; i < platformY.size(); i++) {
			//System.out.println("i:" +i);
			for(int j = 5; j < spawnPoints - 5; j++) {
				//System.out.println("j: " +j);
				int randomNumber = random.nextInt(100);	
				if(randomNumber >= 75 && randomNumber < 85) {
					//System.out.println("spawning speedup at " +j*250 + ", " +platformY.get(i));
					speedups.add( new Speedup(BlackFridayBlitz.SPEEDUP_PNG, j*250, platformY.get(i)-20));
					}
				if(randomNumber >= 85 && randomNumber < 100)
					powerups.add( new Powerup(BlackFridayBlitz.POWERUP_PNG, j*250, platformY.get(i)-43));
					;
			}
		}	
	}

	public ArrayList<Speedup> getSpeedups() {
		return speedups;
	}
	public ArrayList<Powerup> getPowerups() {
		return powerups;
	}

	public int getLength() {
		return length;
	}
}
