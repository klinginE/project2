package project2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import project2.Powerup.PowerupState;
import project2.Speedup.SpeedupState;


public class Level {

	private int length, numXpixels, dspawnPoint;
	private ArrayList<SpeedupState> speedups;
	private ArrayList<PowerupState> powerups;
	private static final Random random = new Random();
	public static final float[] platformY = {520, 358 ,213, 70};
	
	public static class LevelState implements Serializable {

		private static final long serialVersionUID = 5240679637926703874L;

		public int length_s, numXpixels_s, dspawnPoint_s;
		public ArrayList<SpeedupState> speedups_s;
		public ArrayList<PowerupState> powerups_s;

		public LevelState(int len, int numXpix, int dspawnP, ArrayList<SpeedupState> spups, ArrayList<PowerupState> powups) {

			super();
			length_s = len;
			numXpixels_s = numXpix;
			dspawnPoint_s = dspawnP;
			speedups_s = spups;
			powerups_s = powups;

		}
		public Level getLevel() {
			
			Level lvl = new Level();
			lvl.length = length_s;
			lvl.numXpixels = numXpixels_s;
			lvl.dspawnPoint = dspawnPoint_s;
			lvl.speedups = speedups_s;
			lvl.powerups = powerups_s;
			return lvl;

		}
		
	}

	public Level() {
		super();
	}
	public Level(int length) {
		
		this.length = length;
		speedups = new ArrayList<SpeedupState>();
		powerups = new ArrayList<PowerupState>();
		
		//speedups.add(new Speedup(BlackFridayBlitz.SPEEDUP_PNG, 800, platformY.get(1)));
		numXpixels = length*1000; 
		int spawnPoints = numXpixels/250;
		
		for(int i = 0; i < platformY.length; i++) {
			//System.out.println("i:" +i);
			for(int j = 5; j < spawnPoints - 5; j++) {
				//System.out.println("j: " +j);
				int randomNumber = random.nextInt(100);	
				if(randomNumber >= 75 && randomNumber < 85) {
					//System.out.println("spawning speedup at " +j*250 + ", " +platformY.get(i));
					Speedup spup = new Speedup(BlackFridayBlitz.SPEEDUP_PNG, 0, j*250, platformY[i]-20);
					speedups.add(new SpeedupState(spup.getImageString(), spup.getTimer(), spup.getWorldX(), spup.getWorldY(), spup.getActive(), spup.getCoarseGrainedWidth(), spup.getCoarseGrainedHeight()));
					}
				if(randomNumber >= 85 && randomNumber < 100) {
					Powerup powup = new Powerup(BlackFridayBlitz.POWERUP_PNG, j*250, platformY[i]-20);
					powerups.add(new PowerupState(powup.getImageString(), powup.getWorldX(), powup.getWorldY(), powup.getActive(), powup.getCoarseGrainedWidth(), powup.getCoarseGrainedHeight()));
				}
			}
		}	
	}

	public ArrayList<SpeedupState> getSpeedups() {
		return speedups;
	}
	public ArrayList<PowerupState> getPowerups() {
		return powerups;
	}
	public void setSpeedups(ArrayList<SpeedupState> spup) {
		speedups = spup;
	}
	public void setPowerups(ArrayList<PowerupState> powup) {
		powerups = powup;
	}

	public int getLength() {
		return length;
	}
	public int getNumXpixels() {
		return numXpixels;
	}
	public int getDspawnPoint() {
		return dspawnPoint;
	}
}
