package project2;

public class Player {

	private Cart playerCart = null;
	private Client playerClient = null;

	public Player() {

		super();
		playerCart = new Cart(BlackFridayBlitz.STANDIN_PLAYER_PNG, 50.0f, 275.0f);

	}

	public String getUsername() {

		if (playerClient != null)
			return playerClient.getUsername();
		return "";

	}

	public Cart getPlayerCart() {

		return playerCart;

	}

	public void connectToServer() {

		if (playerClient == null)
			playerClient = new Client();

	}

	public void disconnectServer() {

		if (playerClient != null)
			playerClient.stopClient();

	}

}