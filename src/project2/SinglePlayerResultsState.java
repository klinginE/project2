package project2;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
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
	int player;
	UnicodeFont font;
	Font awtFont;
	String finalTime = "";
	
	
	public void setTime(int player, long time){
		finalTime = String.format("%02d:%02d.%02d", 
			    TimeUnit.MILLISECONDS.toMinutes(time),
			    TimeUnit.MILLISECONDS.toSeconds(time) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)),
			     (time - (TimeUnit.MILLISECONDS.toSeconds(time) * 1000)));	
		this.player = player;
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
		receipt.draw(348, 25);
		g.setFont(font);
		if (player == 0){
			g.drawString("Grandpa", 393, 215);
		} else if (player == 1) {
			g.drawString("Zombie", 393, 215);
		} else if (player == 2) {
			g.drawString("Robot", 393, 215);
		} else if (player == 3) {
			g.drawString("Scarecrow", 393, 215);
		}
		
		g.drawString(finalTime, 473, 215);
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		BlackFridayBlitz bb = (BlackFridayBlitz)game;
		int posX = container.getInput().getMouseX();
		int posY = container.getInput().getMouseY();
		if (((posX > 44 + 348) && (posX < 260 + 348)) && ((posY > 389+25) && (posY < 453+25))){
			if (container.getInput().isMousePressed(0)){
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
		
	}

}
