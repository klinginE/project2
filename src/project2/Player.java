package project2;

import project2.Cart.CartState;

public class Player {

	public Cart playerCart = null;
	private Client playerClient = null;
	private int type = -1;
	int weaponToggle = 0;
	int id;

	public Player() {
		super();
	}
	public Player(float y, int cart) {

		super();
		id = cart;
		if (cart == 0){
			playerCart = new Cart(BlackFridayBlitz.PLAYER1_PNG, 0, y);
		} else if (cart == 1){
			playerCart = new Cart(BlackFridayBlitz.PLAYER2_PNG, 0, y);
		} else if (cart == 2){
			playerCart = new Cart(BlackFridayBlitz.PLAYER3_PNG, 0, y);
		} else if (cart == 3){
			playerCart = new Cart(BlackFridayBlitz.PLAYER4_PNG, 0, y);
		}
	}
	public Player(float y) {

		super();
		playerCart = new Cart(BlackFridayBlitz.PLAYER1_PNG, 0, y);

	}
	public String getUsername() {

		if (playerClient != null)
			return playerClient.getUsername();
		return "";

	}
	
	public Weapon fireWeapon() {

		Weapon temp = new Weapon(playerCart, playerCart.getWorldX() + Cart.MIN_SCREEN_X, playerCart.getY(), playerCart.getWorldX() + Cart.MIN_SCREEN_X, playerCart.getY(), getUsername(), type, weaponToggle, 0, true);
		playerCart = temp.owner;
		type = -1;
		return temp;

	}
	

	public void toggleWeapon(){
		if (weaponToggle == 0){
			weaponToggle = 1;
		} else {
			weaponToggle = 0;
		}
	}
	public int getWeaponToggle(){
		return weaponToggle;
	}

	public Client getPlayerClient() {

		return playerClient;

	}

	public Cart getPlayerCart() {

		if (playerClient != null && playerClient.getGameState() != null && playerClient.getGameState().playerCarts != null && playerClient.getGameState().playerCarts.get(getUsername()) != null)
			playerCart = playerClient.getGameState().playerCarts.get(getUsername()).getCart(true);
		return playerCart;

	}
	
	public int getPowerup(){		
		return type;
	}
	
	public void setPowerup(int t){
		type = t;
	}

	public void connectToServer() {

		if (playerClient == null) {

			playerClient = new Client();
			GameState ps = new GameState(new Level(BlackFridayBlitz.LEVEL_LENGTH));

			int cart = playerClient.userId;
			if (cart == 0){
				playerCart = new Cart(BlackFridayBlitz.PLAYER1_PNG, 0, playerCart.getY());
			} else if (cart == 1){
				playerCart = new Cart(BlackFridayBlitz.PLAYER2_PNG, 0, playerCart.getY());
			} else if (cart == 2){
				playerCart = new Cart(BlackFridayBlitz.PLAYER3_PNG, 0, playerCart.getY());
			} else if (cart == 3){
				playerCart = new Cart(BlackFridayBlitz.PLAYER4_PNG, 0, playerCart.getY());
			}

			ps.playerCarts.put(getUsername(), new CartState(playerCart.getX(), playerCart.getY(), playerCart.getCoarseGrainedWidth(), playerCart.getCoarseGrainedHeight(), playerCart.getNumSpeedUps(), playerCart.getCurrentSpeed(), playerCart.getBatteryBoost(), playerCart.getWorldX(), playerCart.getWorldY(), playerCart.getPlatform(), playerCart.getJumpPoint(), playerCart.getImageString(), playerCart.MAX_SCREEN_X, playerCart.getKeyleft(), playerCart.getKeyright(), playerCart.getBoost()));

			if (playerClient.getCurrentState().getState() != 100 && playerClient.getCurrentState().getState() != 0)
				return;
			playerClient.setGameState(ps);

		}

	}

	public void disconnectServer() {

		if (playerClient != null)
			playerClient.stopClient();

	}

}
