package project2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

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
	private volatile boolean isRunning = false;
	private Thread receiveThread = null;
	private Thread sendThread = null;
	private volatile DataPackage currentState = null;
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;

	private class SendThread extends Thread {

		@Override
		public void run() {

			while (isRunning) {

				try {
					Thread.sleep(16l);
				}
				catch (InterruptedException e1) {}

				try {

					oos.flush();
					DataPackage dp = null;
					synchronized(currentState) {

						dp = new DataPackage(currentState.getUsername(), currentState.getState(), currentState.getMessage(), currentState.getGameData());
						System.out.println("write username: " + currentState.getUsername() + "\twrite state: " + currentState.getState() + "\twrite message: " + currentState.getMessage() + "\n");

					}
					oos.writeObject(dp);
					//System.out.println("AFTER WRITE");
					oos.flush();

					synchronized(currentState) {
						if (currentState.getState() != 100)
							isRunning = false;
					}

				}
				catch (IOException e) {

					//System.out.println("WRITE ERROR: " + e.getMessage());
					isRunning = false;

				}

			}

		}

	}

	private class ReceiveThread extends Thread {

		@Override
		public void run() {

			while (isRunning) {

				try {

					//System.out.println("BEFRORE READ");
					DataPackage dp = null;
					dp = (DataPackage)ois.readObject();
					synchronized (currentState) {
						currentState = new DataPackage(dp.getUsername(), dp.getState(), dp.getMessage(), dp.getGameData());
						System.out.println("read username: " + currentState.getUsername() + "\tread state: " + currentState.getState() + "\tread message: " + currentState.getMessage() + "\n");
						if (currentState.getState() != 100)
							isRunning = false;
					}

				} 
				catch (IOException|ClassNotFoundException e) {
					//System.out.println("READ ERROR: " + e.getMessage());
					isRunning = false;
				}

			}

		}

	}

	public Client(GameState initGameState) {

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
				oos.writeObject(new DataPackage(username, 100, "", null));
				oos.flush();

				DataPackage responseData = (DataPackage)ois.readObject();
				if (responseData.getState() == 0) {
					nameOkay = true;
				}
				else if (responseData.getState() == 200)
					nameOkay = false;
				else {
					JOptionPane.showMessageDialog(null, responseData.getMessage(), "Message", JOptionPane.INFORMATION_MESSAGE);
					System.exit(1);
				}

				if (nameOkay) {

					isRunning = true;
					currentState = new DataPackage(username, 100, "", initGameState);
					sendThread = new SendThread();
					sendThread.setName("Client Send Thread");
					sendThread.start();
					receiveThread = new ReceiveThread();
					receiveThread.setName("Client Receive Thread");
					receiveThread.start();

				}
				JOptionPane.showMessageDialog(null, responseData.getMessage(), "Message", JOptionPane.INFORMATION_MESSAGE);

			}

		}
		catch (Exception ex){

			JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error Alert", JOptionPane.ERROR_MESSAGE);
			System.exit(1);

		}
		while(isRunning);
		stopClient();

	}

	public void stopClient() {

		isRunning = false;
		sendThread.interrupt();
		try {
			sendThread.join(100l);
		}
		catch (InterruptedException e) {}
		receiveThread.interrupt();
		try {
			receiveThread.join(100l);
		}
		catch (InterruptedException e) {}
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

	public DataPackage getCurrentState() {

		return currentState;

	}

	public Object getGameData() {

		return currentState.getGameData();

	}

	public void setGameData(Object data) {

		synchronized(currentState) {

			currentState.setGameData(data);

		}

	}

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception ex) {}
		new Client(null);

	}

}