package project2;

public class Player {

	private Cart playerCart = null;
	private Client playerClient = null;

	public Player() {

		super();
		playerCart = new Cart(BlackFridayBlitz.PLAYER1_PNG, 0, 275.0f);

	}

	public String getUsername() {

		if (playerClient != null)
			return playerClient.getUsername();
		return "";

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