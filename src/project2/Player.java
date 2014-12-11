package project2;

public class Player {

	private Cart playerCart = null;
	private Client playerClient = null;
	private int powerupType = -1;
	int weaponToggle = 0;

	public Player(float y, int cart) {

		super();

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

	public String getUsername() {

		if (playerClient != null)
			return playerClient.getUsername();
		return "";

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

	public void resetCart() {

		playerCart = new Cart(BlackFridayBlitz.PLAYER1_PNG, 0, 275.0f);

	}

	public Cart getPlayerCart() {

		if (playerClient != null)
			playerCart = ((GameState)playerClient.getGameData()).playerCarts.get(getUsername());
		return playerCart;

	}
	
	public int getPowerup(){		
		return powerupType;
	}
	
	public void setPowerup(int type){
		powerupType = type;
	}

	public void connectToServer() {

		if (playerClient == null) {

			playerClient = new Client(null);
			GameState ps = new GameState();
			ps.playerCarts.put(getUsername(), playerCart);
			playerClient.setGameData(ps);

		}

	}

	public void disconnectServer() {

		if (playerClient != null)
			playerClient.stopClient();

	}

}
