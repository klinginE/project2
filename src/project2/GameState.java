package project2;

import java.io.Serializable;
import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import project2.Cart.CartState;

public class GameState implements Serializable {

	private static final long serialVersionUID = -6976615829378092809L;

	public HashMap<String, CartState> playerCarts = null;
	public HashMap<String, HashMap<String, Boolean>> inputs = null;
	public HashMap<String, Long> frames = null;
	public long timer = 0;

	public GameState() {

		super();
		playerCarts = new HashMap<String, CartState>();
		inputs = new HashMap<String, HashMap<String, Boolean>>();
		frames = new HashMap<String, Long>();

	}

	public void addGame(String username, Cart cart, GameContainer container, long frame) {

		playerCarts.put(username, new CartState(cart.getX(), cart.getY(), cart.getCoarseGrainedWidth(), cart.getCoarseGrainedHeight(), cart.getNumSpeedUps(), cart.getCurrentSpeed(), cart.getWorldX(), cart.getWorldY(), cart.getPlatform(), cart.getJumpPoint(), cart.getImageString()));
		frames.put(username, new Long(frame));

		HashMap<String, Boolean> inputMap = new HashMap<String, Boolean>();
		Input i = container.getInput();
		if (i.isKeyDown(Input.KEY_LEFT))
			inputMap.put("left", new Boolean(true));
		else
			inputMap.put("left", new Boolean(false));

		if (i.isKeyDown(Input.KEY_RIGHT))
			inputMap.put("right", new Boolean(true));
		else
			inputMap.put("right", new Boolean(false));

		if (i.isKeyPressed(Input.KEY_UP))
			inputMap.put("up", new Boolean(true));
		else
			inputMap.put("up", new Boolean(false));

		if (i.isKeyPressed(Input.KEY_DOWN))
			inputMap.put("down", new Boolean(true));
		else
			inputMap.put("down", new Boolean(false));

		if (i.isKeyPressed(Input.KEY_SPACE))
			inputMap.put("space", new Boolean(true));
		else
			inputMap.put("space", new Boolean(false));

		if (i.isKeyPressed(Input.KEY_TAB))
			inputMap.put("tab", new Boolean(true));
		else
			inputMap.put("tab", new Boolean(false));
		inputs.put(username, inputMap);

	}

	public String toString() {

		return playerCarts.toString() + "\n" + inputs.toString() + "\n" + frames.toString() + "\n" + timer;

	}

	public static GameState toObject(String gs) {

		GameState newGs = new GameState();

		String[] parts = gs.split("\n");
		String carts = parts[0].substring(1);
		String inputs = parts[1].substring(1);
		String frames = parts[2].substring(1);
		long time = Long.parseLong(parts[3]);

		HashMap<String, CartState>hashCarts = new HashMap<String, CartState>();
		String[] values = carts.split("=");
		for (int i = 0; i < values.length;) {

			String username = values[i];
			i++;
			if (i >= values.length)
				break;
			String[] nums = values[i].split(",");
			CartState cs = null;
			if (nums.length > 10)
				cs = new CartState(Float.parseFloat(nums[0].substring(1)), Float.parseFloat(nums[1]), Float.parseFloat(nums[2]), Float.parseFloat(nums[3]), Integer.parseInt(nums[4]), Float.parseFloat(nums[5]), Float.parseFloat(nums[6]), Float.parseFloat(nums[7]), Integer.parseInt(nums[8]), Float.parseFloat(nums[9]), nums[10].split("\\}")[0]);
			hashCarts.put(username, cs);
			i++;

		}

		HashMap<String, HashMap<String, Boolean>>hashInputs = new HashMap<String, HashMap<String, Boolean>>();
		values = inputs.split("=\\{");
		for (int i = 0; i < values.length;) {

			String username = values[i];
			i++;
			if (i >= values.length)
				break;
			String[] nums = values[i].split(", ");
			HashMap<String, Boolean> hashStrings = new HashMap<String, Boolean>();
			for (int j = 0; j < nums.length;  j++) {
				if (nums[j].split("=")[1].contains("}"))
					hashStrings.put(nums[j].split("=")[0], new Boolean(Boolean.parseBoolean(nums[j].split("=")[1].split("\\}")[0])));
				else
					hashStrings.put(nums[j].split("=")[0], new Boolean(Boolean.parseBoolean(nums[j].split("=")[1])));
			}
			hashInputs.put(username, hashStrings);
			i++;

		}

		HashMap<String, Long>hashFrames = new HashMap<String, Long>();
		values = frames.split("=");
		for (int i = 0; i < values.length;) {

			String username = values[i];
			i++;
			if (i >= values.length)
				break;
			String num = values[i];
			if (num.contains("}"))
				num = num.split("\\}")[0];
			hashFrames.put(username, new Long(Long.parseLong(num)));
			i++;

		}

		newGs.playerCarts = hashCarts;
		newGs.inputs = hashInputs;
		newGs.frames = hashFrames;
		newGs.timer = time;
		return newGs;

	}

}