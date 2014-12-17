package project2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.newdawn.slick.GameContainer;

import project2.Cart.CartState;

/**
 * CLIENT STATES:
 * 100 - All is good normal operations
 * 200 - Invalid username
 * 300 - Client is disconnecting
 * 400 - Server is disconnecting Client
 * 500 - Server is shuting down
**/
public class Client {

	private Socket socket;
	private int port = 4444;
	private String ip = "";
	private String username = "";
	public int userId = 0;
	private volatile boolean isRunning = false;
	private ReceiveThread receiveThread = null;
	private SendThread sendThread = null;
	private volatile DataPackage currentState = null;
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;

	private class SendThread extends Thread {

		public boolean okToWrite = true;

		@Override
		public void run() {

			while (isRunning) {

				if (!okToWrite)
					continue;
				try {

					DataPackage dataState = null;
					synchronized(currentState) {
						dataState = currentState;
					}

					oos.flush();
					DataPackage dp = null;

//					if (dataState != null && dataState.getGameState() != null && dataState.getGameState().inputs.get(dataState.getUsername()).get("up").booleanValue() == true)
//						System.out.println("Client writing frame: " + dataState.getGameState().frames.get(dataState.getUsername()) + " input: " + dataState.getGameState().inputs.get(dataState.getUsername()));
					dp = new DataPackage(dataState.getUsername(), dataState.getState(), dataState.getMessage(), dataState.getGameState());
					//System.out.println("write username: " + currentState.getUsername() + "\twrite state: " + currentState.getState() + "\twrite message: " + currentState.getMessage() + "\twrite game data: " + currentState.getGameState() + "\n");

					oos.writeObject(dp);
					okToWrite = false;
					//System.out.println("AFTER WRITE");
					oos.flush();

					synchronized(currentState) {
						if (currentState.getState() != 100)
							isRunning = false;
					}

				}
				catch (IOException e) {

					System.out.println("WRITE ERROR: " + e.getMessage());
					isRunning = false;

				}

			}

		}

	}

	private class ReceiveThread extends Thread {

		public boolean okToRead = true;
	
		@Override
		public void run() {

			while (isRunning) {

				if (!okToRead)
					continue;
				try {

					//System.out.println("BEFRORE READ");
					DataPackage dp = null;
					dp = (DataPackage)ois.readObject();
					okToRead = false;

					synchronized (currentState) {

						currentState = new DataPackage(dp.getUsername(), dp.getState(), dp.getMessage(), dp.getGameState());
						//System.out.println("read username: " + currentState.getUsername() + "\tread state: " + currentState.getState() + "\tread message: " + currentState.getMessage() + "\tgame data: " + currentState.getGameState() + "\n");
						if (currentState.getState() != 100)
							isRunning = false;

					}

				} 
				catch (IOException|ClassNotFoundException e) {
					System.out.println("READ ERROR: " + e.getMessage());
					isRunning = false;
				}

			}

		}

	}

	public Client() {

		try {

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch(Exception ex) {}

			String local;
			try {
				local = InetAddress.getLocalHost().getHostAddress() + ":" + port;
			}
			catch (UnknownHostException ex) {
				local = "Network Error";
			}

			ip = (String)JOptionPane.showInputDialog(null, "IP: ", "Info", JOptionPane.INFORMATION_MESSAGE, null, null, local);
			port = Integer.parseInt(ip.substring(ip.indexOf(":") + 1));
			ip = ip.substring(0, ip.indexOf(":"));

			socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			boolean nameOkay = false;
			while (!nameOkay) {

				String set_username = System.getProperty("user.name");
				set_username = (String)JOptionPane.showInputDialog(null, "Username: ", "Info", JOptionPane.INFORMATION_MESSAGE, null, null, set_username);
				if (set_username == null)
					break;
				username = set_username.trim();
				if (username.toLowerCase().equals(""))
					continue;

				oos.flush();
				oos.writeObject(new DataPackage(username, 100, DataPackage.MSG_100));
				oos.flush();

				DataPackage responseData = (DataPackage)ois.readObject();
				if (responseData.getState() >= 0 && responseData.getState() <= 3) {
					nameOkay = true;
					userId = responseData.getState();
				}
				else if (responseData.getState() == 200)
					nameOkay = false;
				else if (responseData.getState() == 50) {
					nameOkay = false;
					currentState = new DataPackage(username, 50, DataPackage.MSG_050);
					break;
				}
				else {
					JOptionPane.showMessageDialog(null, responseData.getMessage(), "Message", JOptionPane.INFORMATION_MESSAGE);
					System.exit(1);
				}

				if (nameOkay) {

					isRunning = true;
					currentState = new DataPackage(username, 100, DataPackage.MSG_100);
					sendThread = new SendThread();
					sendThread.setName("Client Send Thread");
					sendThread.start();
					receiveThread = new ReceiveThread();
					receiveThread.setName("Client Receive Thread");
					receiveThread.start();

				}

				JOptionPane.showMessageDialog(null, responseData.getMessage(), "Message", JOptionPane.INFORMATION_MESSAGE);
				//stopClient();

			}

		}
		catch (Exception ex){

			JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error Alert", JOptionPane.ERROR_MESSAGE);
			System.exit(1);

		}

	}

	public void stopClient() {

		isRunning = false;

		if (sendThread != null) {

			sendThread.interrupt();
			try {
				sendThread.join(100l);
			}
			catch (InterruptedException e) {}

		}

		if (receiveThread != null) {

			receiveThread.interrupt();
			try {
				receiveThread.join(100l);
			}
			catch (InterruptedException e) {}

		}
		currentState.setState(300);
		currentState.setMessage(DataPackage.MSG_300);
		try {

			oos.writeObject(currentState);
			oos.flush();
			oos.close();
			ois.close();
			socket.close();

		}
		catch (IOException e) {}

	}

	public String getUsername() {

		return username;

	}

	public boolean getIsRunning() {
		return isRunning;
	}
	public DataPackage getCurrentState() {

		return currentState;

	}

	public GameState getGameState() {

		return currentState.getGameState();

	}

	public void setGameState(GameState state) {

		synchronized(currentState) {

			currentState.setGameState(state);

		}

	}

	public void updateGameState (String username, Cart cart, GameContainer container, long frame) {

		synchronized (currentState) {
			currentState.getGameState().addGame(username, cart, container, frame);
		}

	}

	public void setOkToRead() {
		
		receiveThread.okToRead = true;
		
	}
	public void setOkToWrite() {
		sendThread.okToWrite = true;
	}
	
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception ex) {}
		new Client();

	}

}