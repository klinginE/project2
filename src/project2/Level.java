package project2;

import java.util.ArrayList;
import java.util.Random;


public class Level {

	int length, numXpixels, dspawnPoint;
	ArrayList<Float> platformY;
	ArrayList<Speedup> speedups; //coords of speedups
	Random random = new Random();
	


	public Level(int length) {
		
		this.length = length;
		speedups = new ArrayList<Speedup>();
		platformY = new ArrayList<Float>();
		platformY.add(520.0f); //floor
		platformY.add(358.0f); //1st platform
		platformY.add(213.0f); //2nd platform
		platformY.add(70.0f); //3rd platform
		
		
		
		
		//speedups.add(new Speedup(BlackFridayBlitz.SPEEDUP_PNG, 800, platformY.get(1)));
		numXpixels = length*1000; 
		int spawnPoints = numXpixels/250;
		
		/*for(int i = 0; i < platformY.size(); i++) {
			for(int j = 0; i < spawnPoints; j++) {
				int randomNumber = random.nextInt(100);	
				if(randomNumber >= 75 && randomNumber < 85)
					speedups.add( new Speedup(BlackFridayBlitz.SPEEDUP_PNG, i*numXpixels, platformY.get(i)));
				if(randomNumber >= 85 && randomNumber < 100)
					//powerups.add( new Powerup(BlackFridayBlitz.POWERUP_PNG, i*numXpixels, platformY.get(i)));
					;
			}
		}	*/
	}

	public ArrayList<Speedup> getSpeedups() {
		return speedups;
	}


	public int getLength() {
		return length;
	}
}
