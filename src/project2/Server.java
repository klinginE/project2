package project2;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class Server {

	// Socket data
	public static final int PORT = 4444;
	public static String ip_4 = "";
	public static String ip_6 = "";
	public static ServerSocketChannel socketChannel = null;

	// Sever data
	public static Map<Integer, SocketChannel> list_clientSockets = null;
	public static Map<Integer, DataPackage> list_clientStates = null;
	private static List<Integer> list_unusedIndices = null;
	public static JList<String> list_clients = null;
	public static DefaultListModel<String> list_clientsModel;

	// Window data
	public static JFrame frame = null;
	public static JPanel content = null;
	public static JPanel panel1 = null;
	public static JPanel panel2 = null;
	public static JPanel panel3 = null;
	public static JButton disconnectBtn = null;

	// Thread data
	public static boolean isRunning = false;
	public static Thread acceptThread = null;
	public static Thread receiveThread = null;
	public static Thread sendThread = null;
	public static Thread gameThread = null;

	// Runnables
	private static Runnable accept = new Runnable() {

		@Override
		public void run() {

			while (isRunning) {

				try {

					SocketChannel newClientSocket = null;
					synchronized (socketChannel) {
						newClientSocket = socketChannel.accept();
					}
					ObjectInputStream ois = null;
					ObjectOutputStream oos = null;

					DataPackage dp = null;
					String username = "";
					int count = 0;
					while (count < 2 && username.toLowerCase().equals("")) {

						ois = new ObjectInputStream(newClientSocket.socket().getInputStream());
						dp = (DataPackage)ois.readObject();
						username = dp.getUsername();

						for (Integer index : list_clientStates.keySet()) {

							DataPackage clientData = list_clientStates.get(index);
							if (clientData.getUsername().toLowerCase().equals(username.toLowerCase())) {

								oos = new ObjectOutputStream(newClientSocket.socket().getOutputStream());
								oos.flush();
								oos.writeObject(new DataPackage(username, 0, "400: Username already choosen!"));
								oos.flush();
								oos.reset();
								username = "";
								break;

							}

						}
						count++;

					}

					if (count < 2) {

						DataPackage newClientPackage = new DataPackage(dp.getUsername(), 0, "100: Welcome to the server");
						oos = new ObjectOutputStream(newClientSocket.socket().getOutputStream());
						oos.flush();
						oos.writeObject(newClientPackage);
						oos.flush();
						oos.reset();

						synchronized (list_clientsModel) {
							list_clientsModel.addElement(dp.getUsername() + " - " + newClientSocket.socket().getInetAddress().getHostAddress() + " - " + newClientSocket.socket().getInetAddress().getHostName());
						}

						Integer index = null;
						if (list_unusedIndices.size() > 0) {
							index = list_unusedIndices.get(0);
							list_unusedIndices.remove(0);
						}
						if (index == null) {
							Integer maxIndex = new Integer(-1);
							for (Integer key : list_clientStates.keySet()) {
								if (key.intValue() > maxIndex.intValue())
									maxIndex = key.intValue();
							}
							index = new Integer(maxIndex.intValue() + 1);
						}
						list_clientStates.put(index, newClientPackage);
						list_clientSockets.put(index, newClientSocket);

					}
					else {

						oos = new ObjectOutputStream(newClientSocket.socket().getOutputStream());
						oos.flush();
						oos.writeObject(new DataPackage(dp.getUsername(), 1, "500: Connection failed!"));
						oos.flush();
						oos.reset();

					}

				}
				catch (Exception e) {}

			}

		}

	};

	private static Runnable send = new Runnable() {

		@Override
		public void run() {

			while(isRunning) {

				ObjectOutputStream oos = null;
				ArrayList<Integer>keys = new ArrayList<Integer>();
				for (Integer temp_key : list_clientSockets.keySet()) {
					if (temp_key != null) {
						keys.add(temp_key);
					}
				}
				for (Integer key : keys) {

					if (list_clientSockets.keySet().contains(key)) {

						try {

							oos = new ObjectOutputStream(list_clientSockets.get(key).socket().getOutputStream());
							oos.flush();
							DataPackage client_state = list_clientStates.get(key);
							oos.writeObject(client_state);
							oos.flush();
							oos.reset();

							if (client_state.getState() == 1) {// Kicked by server

								disconnectClient(key);

							}
							else if (client_state.getState() == 2) {// BasicServer Disconnected

								disconnectClient(key);

							}

						}
						catch (Exception ex) {disconnectClient(key);}

					}

				}

			}

		}

	};

	public static synchronized void disconnectClient(Integer key) {

		if (list_clientSockets.size() <= 0 || key.intValue() < 0 || !list_clientSockets.containsKey(key))
			return;

		try {

			ObjectOutputStream oos = new ObjectOutputStream(list_clientSockets.get(key).socket().getOutputStream());
			oos.flush();
			oos.writeObject(new DataPackage(list_clientStates.get(key).getUsername(), 2, "300: Server Shutting Down!"));
			oos.flush();
			oos.reset();
			list_clientSockets.get(key).close();

		}
		catch (IOException e) {}

		DataPackage d = list_clientStates.get(key); 
		list_clientStates.remove(key);
		synchronized (list_clientsModel) {

			for (int i = 0; i < list_clientsModel.size(); i++) {

				String client = list_clientsModel.get(i);
				if (client.split(" ")[0].toLowerCase().equals(d.getUsername().toLowerCase())) {

					list_clientsModel.remove(i);
					break;

				}

			}

		}
		list_clientSockets.remove(key);
		list_unusedIndices.add(key);

	}

	private static void initLists() {

		list_clientsModel = new DefaultListModel<String>();
		list_clients = new JList<String>(list_clientsModel);
		list_clientSockets = (Map<Integer, SocketChannel>)Collections.synchronizedMap(new HashMap<Integer, SocketChannel>());
		list_clientStates = (Map<Integer, DataPackage>)Collections.synchronizedMap(new HashMap<Integer, DataPackage>());
		list_unusedIndices = (List<Integer>)Collections.synchronizedList(new ArrayList<Integer>());
		list_unusedIndices.add(new Integer(0));

	}

	private static void createServerFrame() {

		frame = new JFrame();
		frame.setTitle("Server");
		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowClosing(WindowEvent arg0) {

				isRunning = false;
				try {
					acceptThread.interrupt();
					acceptThread.join(3000L);
					//receiveThread.join();
					sendThread.interrupt();
					sendThread.join(3000L);
					//gameThread.join();
				}
				catch (InterruptedException e) {}
				while (list_clientSockets.size() > 0) {

					for (Integer key : list_clientSockets.keySet()) {

						disconnectClient(key);

					}

				}
				System.exit(0);

			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
			}

		});

		disconnectBtn = new JButton();
		disconnectBtn.setText("Disconnect");
		disconnectBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int selected = 0;
				synchronized (list_clients) {
					selected = list_clients.getSelectedIndex();
				}

				if (selected != -1) {

					synchronized (list_clientsModel) {

						String client = list_clientsModel.get(selected);
						for (Integer key : list_clientStates.keySet()) {

							DataPackage dp = list_clientStates.get(key);
							if (client.split(" ")[0].toLowerCase().equals(dp.getUsername().toLowerCase())) {

								list_clientStates.get(key).setState(2);
								break;

							}

						}

					}

				}

			}

		});

		panel1 = new JPanel();
		panel1.setLayout(new GridLayout(1, 1, 1, 1));
		panel1.add(disconnectBtn);

		panel2 = new JPanel();
		panel2.setLayout(new BorderLayout(1, 1));

		String ipText = "IPV4: " + ip_4 + "    IPV6: " + ip_6;
		FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
		Font font = new Font("Tahoma", Font.PLAIN, 12);
		int textWidth = (int)(font.getStringBounds(ipText, frc).getWidth());
		int charWidth = (int)(textWidth / (int)(font.getStringBounds("M", frc).getWidth()));

		JTextArea textArea = new JTextArea(1, charWidth);
		textArea.setFont(font);
		textArea.append(ipText);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea); 
		panel2.add(scrollPane, BorderLayout.CENTER);
		panel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		panel3 = new JPanel();
		panel3.setLayout(new BorderLayout(1, 1));
		panel3.add(panel1, BorderLayout.NORTH);
		panel3.add(new JScrollPane(list_clients), BorderLayout.CENTER);
		panel3.add(panel2, BorderLayout.SOUTH);

		content = new JPanel();
		content.setLayout(new GridLayout(1, 1, 1, 1));
		content.add(panel3);
		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		frame.setContentPane(content);
		frame.pack();
		frame.setSize(panel2.getWidth() + 20, panel2.getWidth() + 20);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setMinimumSize(frame.getSize());

	}

	private static void getIp() {

		String set_ip4 = null;
		String set_ip6 = null;
		boolean haveIP4 = false;
		boolean haveIP6 = false;

    	Enumeration<NetworkInterface> interfaces = null;
		try {

			interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {

	    	    NetworkInterface current = interfaces.nextElement();
	    	    if (!current.isUp() || current.isLoopback() || current.isVirtual())
	    	    	continue;

	    	    Enumeration<InetAddress> addresses = current.getInetAddresses();
	    	    while (addresses.hasMoreElements()) {

	    	        InetAddress current_addr = addresses.nextElement();
	    	        if (current_addr.isLoopbackAddress())
	    	        	continue;

	    	        if (current_addr instanceof Inet4Address) {

	    	        	set_ip4 = current_addr.getHostAddress();
	    	        	haveIP4 = true;
	    	        	if (haveIP4 && haveIP6)
	    	        		break;

	    	        }
	    	        else if (current_addr instanceof Inet6Address) {

	    	        	set_ip6 = current_addr.getHostAddress();
	    	        	haveIP6 = true;
	    	        	if (haveIP4 && haveIP6)
	    	        		break;

	    	        }

	    	    }

	    	}
			ip_4 = set_ip4;
			ip_6 = set_ip6;
		}
		catch (SocketException e) {
			try {
				ip_4 = InetAddress.getLocalHost().getHostAddress();
			}
			catch (UnknownHostException e1) {
				ip_4 = "127.0.0.1";
			}
		}

	}

	public static void main(String[] args) {

		//Set the look and feel of windows
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception ex) {}

		//Init stuff
		initLists();
		getIp();
		createServerFrame();

		//Start the server
		try {

		    socketChannel = ServerSocketChannel.open();
		    socketChannel.socket().bind(new InetSocketAddress(PORT));
			isRunning = true;
			acceptThread = new Thread(accept);
			acceptThread.setDaemon(true);
			acceptThread.start();
			//receiveThread = new Thread(receive).start();
			sendThread = new Thread(send);
			sendThread.setDaemon(true);
			sendThread.start();
			//gameThread = new Thread(game).start();

		}
		catch (IOException e) {

			JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error Alert!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(1);

		}

	}

}