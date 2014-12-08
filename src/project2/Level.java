package project2;

import java.util.ArrayList;


public class Level {

	int length;
	ArrayList<Float> platformY;
	ArrayList<Speedup> speedups; //coords of speedups
	
	


	public Level(int length) {
		
		this.length = length;
		speedups = new ArrayList<Speedup>();
		platformY = new ArrayList<Float>();
		platformY.add((float) BlackFridayBlitz.MAX_WINDOW_HEIGHT - 40); //floor
		platformY.add(475.0f - 40); //1st platform
		platformY.add(155.0f - 40); //2nd platform
		speedups.add(new Speedup(BlackFridayBlitz.SPEEDUP_PNG, 800, platformY.get(1)));
		
	
	}

	public ArrayList<Speedup> getSpeedups() {
		return speedups;
	}


	public int getLength() {
		return length;
	}
}
