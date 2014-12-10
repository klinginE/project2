package project2;

import project2.Cart.CartState;

public class Player {

	private Cart playerCart = null;
	private Client playerClient = null;

	public Player(int cart) {

		super();
		if (cart == 0){
			playerCart = new Cart(BlackFridayBlitz.PLAYER1_PNG, 0, 275.0f);
		} else if (cart == 1){
			playerCart = new Cart(BlackFridayBlitz.PLAYER2_PNG, 0, 275.0f);
		} else if (cart == 2){
			playerCart = new Cart(BlackFridayBlitz.PLAYER3_PNG, 0, 275.0f);
		} else if (cart == 3){
			playerCart = new Cart(BlackFridayBlitz.PLAYER4_PNG, 0, 275.0f);
		}

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

		if (playerClient != null && playerClient.getGameState() != null && playerClient.getGameState().playerCarts != null && playerClient.getGameState().playerCarts.get(getUsername()) != null)
			playerCart = playerClient.getGameState().playerCarts.get(getUsername()).getCart();
		return playerCart;

	}

	public void connectToServer() {

		if (playerClient == null) {

			playerClient = new Client();
			GameState ps = new GameState();
			ps.playerCarts.put(getUsername(), new CartState(playerCart.getX(), playerCart.getY(), playerCart.getCoarseGrainedWidth(), playerCart.getCoarseGrainedHeight(), playerCart.getNumSpeedUps(), playerCart.getCurrentSpeed(), playerCart.getWorldX(), playerCart.getWorldY(), playerCart.getJumpPoint(), playerCart.getImageString()));
			playerClient.setGameState(ps);

		}

	}

	public void disconnectServer() {

		if (playerClient != null)
			playerClient.stopClient();

	}

}
