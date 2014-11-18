package project2;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

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

	private SocketChannel socketChannel;
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

				/*try {
					Thread.sleep(100l);
				}
				catch (InterruptedException e1) {}*/

				try {

					synchronized (oos) {

						oos.flush();
						synchronized(currentState) {
							oos.writeObject(currentState);
						}
						oos.flush();

					}

				} 
				catch (Exception e) {}

			}

		}

	}

	private class ReceiveThread extends Thread {

		@Override
		public void run() {

			while (isRunning) {

				/*try {
					Thread.sleep(100l);
				}
				catch (InterruptedException e1) {}*/

				try {

					synchronized (ois) {
						synchronized (currentState) {
							currentState = (DataPackage)ois.readObject();
						}
					}

				} 
				catch (Exception e) {}

			}

		}

	}

	public Client() {

		try {

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

			socketChannel = SocketChannel.open();
			socketChannel.socket().connect(new InetSocketAddress(ip, port));
			oos = new ObjectOutputStream(socketChannel.socket().getOutputStream());
			ois = new ObjectInputStream(socketChannel.socket().getInputStream());

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
				oos.writeObject(new DataPackage(username, 100, ""));
				oos.flush();

				DataPackage responseData = (DataPackage)ois.readObject();
				if (responseData.getState() == 100) {
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
					currentState = new DataPackage(username, 100, "");
					sendThread = new SendThread();
					sendThread.setName("Client Send Thread");
					sendThread.start();
					receiveThread = new ReceiveThread();
					receiveThread.setName("Client Receive Thread");
					receiveThread.start();

				}
				JOptionPane.showMessageDialog(null, responseData.getMessage(), "Message", JOptionPane.INFORMATION_MESSAGE);

			}
			isRunning = false;

		}
		catch (Exception ex){

			JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error Alert", JOptionPane.ERROR_MESSAGE);
			System.exit(1);

		}

	}

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception ex) {}
		new Client();

	}

}