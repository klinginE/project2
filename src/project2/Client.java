package project2;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Client {

	public static SocketChannel socketChannel;
	public static int port = 4444;
	public static String ip = "";
	public static String username = "";
	private static boolean isRunning = true;

	private static Runnable receive = new Runnable() {

		@Override
		public void run() {

			while (isRunning) {

				try {

					ObjectInputStream ois = new ObjectInputStream(socketChannel.socket().getInputStream());
					/*DataPackage responseData = (DataPackage)*/ois.readObject();

				} 
				catch (Exception e) {}

			}

		}

	};

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception ex) {}

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

			boolean nameOkay = false;
			String response = "";
			while (!nameOkay) {

				String set_username = System.getProperty("user.name");
				set_username = (String)JOptionPane.showInputDialog(null, "Username: ", "Info", JOptionPane.INFORMATION_MESSAGE, null, null, set_username);
				if (set_username == null)
					break;
				username = set_username.trim();
				if (username.toLowerCase().equals(""))
					continue;

				ObjectOutputStream oos = new ObjectOutputStream(socketChannel.socket().getOutputStream());
				oos.writeObject(new DataPackage(username, 0, ""));
				oos.flush();
				oos.reset();

				ObjectInputStream ois = new ObjectInputStream(socketChannel.socket().getInputStream());
				DataPackage responseData = (DataPackage)ois.readObject();
				response = responseData.getMessage();
				if (responseData.getState() != 0)
					System.exit(1);
				if (response.substring(0, response.indexOf(":")).toLowerCase().equals("100")) {
					nameOkay = true;
				}
				else
					nameOkay = false;

				Thread thread1 = new Thread(receive);
				thread1.start();
				JOptionPane.showMessageDialog(null, response.substring(response.indexOf(":") + 1), "Message", JOptionPane.INFORMATION_MESSAGE);
				isRunning = false;
				thread1.interrupt();
				thread1.join(3000L);

			}
			//new Thread(send).start();
			

		}
		catch (Exception ex){

			JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error Alert", JOptionPane.ERROR_MESSAGE);
			System.exit(1);

		}

	}

}