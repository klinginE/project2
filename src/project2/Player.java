package project2;

public class Player {

	private Cart playerCart = null;
	private Client playerClient = null;

	public Player(float y) {

		super();
		playerCart = new Cart(BlackFridayBlitz.PLAYER1_PNG, 0, y);

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

		return playerCart;

	}

	public void connectToServer() {

		if (playerClient == null) {

			GameState gs = new GameState();
			gs.addCart(playerCart);
			playerClient = new Client(gs);

		}

	}

	public void disconnectServer() {

		if (playerClient != null)
			playerClient.stopClient();

	}

}