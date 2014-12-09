package project2;

import java.util.Iterator;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class TitleState extends BasicGameState {
	Image bg;
	Image button;
	Image button2;
	Image title;
	Image back;
	Image[] cartImg;
	int players = 0;
	int cart = 0;
	Color myColor = new Color(35, 0 ,180);
	

		public void enter(GameContainer container, StateBasedGame game) {
			//generate terrain
		}

		
		@Override
		public void init(GameContainer container, StateBasedGame game)
				throws SlickException {
			bg = new Image(BlackFridayBlitz.TITLEBG_JPG);
			back = new Image(BlackFridayBlitz.BACK_PNG);
			button = new Image(BlackFridayBlitz.BUTTON_PNG);
			button2 = new Image(BlackFridayBlitz.BUTTON2_PNG);
			title = new Image(BlackFridayBlitz.TITLE_PNG);
			cartImg = new Image[4];
			cartImg[0] = new Image(BlackFridayBlitz.PLAYER1_PNG);
			cartImg[1] = new Image(BlackFridayBlitz.PLAYER2_PNG);
			cartImg[2] = new Image(BlackFridayBlitz.PLAYER3_PNG);
			cartImg[3] = new Image(BlackFridayBlitz.PLAYER4_PNG);
			
		}
		
		@Override
		public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
			bg.draw(0,0);
			title.draw(175, 50);
			button.draw(415,300);			
			button2.draw(340, 500);
			g.setColor(myColor);
			if (players == 0){
				g.drawString("Single Player", 430, 310);
				back.draw(445, 390);
				cartImg[cart].draw(470, 400);				
			} else {
				g.drawString("Multi Player", 435,310);
			}
		}

		@Override
		public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
			BlackFridayBlitz bb = (BlackFridayBlitz)game;
			int posX = container.getInput().getMouseX();
			int posY = container.getInput().getMouseY();
			
			
			if (((posX > 415) && (posX < 415 + 150)) && ((posY > 300) && (posY < 300 + 39))){
				if (container.getInput().isMousePressed(0)){
					if (players == 0){
						players = 1;
					} else {
						players = 0;
					}
				}
			}
			
			if (((posX > 470) && (posX < 470 + 80)) && ((posY > 400) && (posY < 400 + 80))){
				if (players == 0){
					if (container.getInput().isMousePressed(0)){
						if (cart < 3){
							cart += 1;
						} else cart = 0;
					}
				}
			}
			
			
			if (((posX > 340) && (posX < 340 + 300)) && ((posY > 500) && (posY < 500 + 78))){
				if (container.getInput().isMousePressed(0)){
					if (players == 0){
						((SinglePlayerGameState)game.getState(BlackFridayBlitz.SINGLE_PLAYER_GAME_STATE_ID)).setPlayer(cart);
						game.enterState(BlackFridayBlitz.SINGLE_PLAYER_GAME_STATE_ID);
					}
				}
			}
		}
		
		@Override
		public int getID() {
			return BlackFridayBlitz.TITLE_STATE;
		}
	}
