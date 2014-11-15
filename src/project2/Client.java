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

			String set_username = System.getProperty("user.name");
			set_username = (String)JOptionPane.showInputDialog(null, "Username: ", "Info", JOptionPane.INFORMATION_MESSAGE, null, null, set_username);
			username = set_username;

			ObjectOutputStream oos = new ObjectOutputStream(socketChannel.socket().getOutputStream());
			oos.writeObject(new DataPackage(username, 0, ""));

			ObjectInputStream ois = new ObjectInputStream(socketChannel.socket().getInputStream());
			DataPackage responseData = (DataPackage)ois.readObject();
			String response = responseData.getMessage();

			JOptionPane.showMessageDialog(null, response, "Message", JOptionPane.INFORMATION_MESSAGE);

			//new Thread(send).start();
			//new Thread(receive).start();

		}
		catch (Exception ex){

			JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error Alert", JOptionPane.ERROR_MESSAGE);
			System.exit(1);

		}

	}

}