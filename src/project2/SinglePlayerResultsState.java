package project2;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.awt.Color;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.font.effects.ColorEffect;

public class SinglePlayerResultsState extends BasicGameState {
	
	Image bg;
	Image receipt;
	ArrayList<Integer> player = null;
	UnicodeFont font;
	Font awtFont;
	ArrayList<String> finalTime = null;
	
	
	public void setTime(int player, long time){
		finalTime.add(String.format("%02d:%02d.%02d", 
			    TimeUnit.MILLISECONDS.toMinutes(time),
			    TimeUnit.MILLISECONDS.toSeconds(time) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)),
			     (time - (TimeUnit.MILLISECONDS.toSeconds(time) * 1000))));	
		this.player.add(new Integer(player));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void enter(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		
		bg = new Image(BlackFridayBlitz.CHECKOUT_JPG);
		try {
			awtFont = Font.createFont(java.awt.Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(BlackFridayBlitz.RECEIPT_FONT));
			awtFont = awtFont.deriveFont(java.awt.Font.PLAIN, 12);
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		font = new UnicodeFont(awtFont);
		font.addAsciiGlyphs();
		ColorEffect color = new ColorEffect();
		color.setColor(Color.black);
		font.getEffects().add(color);
		font.loadGlyphs();
		receipt = new Image(BlackFridayBlitz.RECEIPT_JPG);
		

	}

	@Override
	public void render(GameContainer c, StateBasedGame arg1, Graphics g)
			throws SlickException {
		bg.draw(0,0);
		receipt.draw(348, 100);
		g.setFont(font);
		int yOffset = 0;
		for (String str : finalTime) {

			if (player.get(yOffset) == 0){
				g.drawString("Grandpa", 393, 290 + yOffset * 20);
			} else if (player.get(yOffset) == 1) {
				g.drawString("Zombie", 393, 290 + yOffset * 20);
			} else if (player.get(yOffset) == 2) {
				g.drawString("Robot", 393, 290 + yOffset * 20);
			} else if (player.get(yOffset) == 3) {
				g.drawString("Scarecrow", 393, 290 + yOffset * 20);
			}

			g.drawString(str, 473, 290 + yOffset * 20);
			yOffset++;

		}
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

		int posX = container.getInput().getMouseX();
		int posY = container.getInput().getMouseY();
		if (((posX > 44 + 348) && (posX < 260 + 348)) && ((posY > 389+100) && (posY < 453+100))){
			if (container.getInput().isMousePressed(0)){
				reset();
				game.enterState(BlackFridayBlitz.TITLE_STATE);
			}
		}

	}

	@Override
	public int getID() {
		return BlackFridayBlitz.SP_RESULTS_STATE;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		// TODO Auto-generated method stub
		finalTime = new ArrayList<String>();
		player = new ArrayList<Integer>();
	}
	public void reset() {
		finalTime = new ArrayList<String>();
		player = new ArrayList<Integer>();
	}
	

}
