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
import java.util.List;

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
	public static List<SocketChannel> list_clientSockets = null;
	public static List<DataPackage> list_clientStates = null;
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

						socketChannel.configureBlocking(false);
						newClientSocket = socketChannel.accept();
						socketChannel.configureBlocking(true);

					}
					if (newClientSocket == null) {
						Thread.sleep(100L);
						continue;
					}

					ObjectInputStream ois = new ObjectInputStream(newClientSocket.socket().getInputStream());
					DataPackage dp = (DataPackage)ois.readObject();

					ObjectOutputStream oos = new ObjectOutputStream(newClientSocket.socket().getOutputStream());
					int count = 0;
					if (count < 3) {

						DataPackage newClientPackage = new DataPackage(dp.getUsername(), 0, "Welcome to the server");
						oos.writeObject(newClientPackage);

						synchronized (list_clientsModel) {
							list_clientsModel.addElement(dp.getUsername() + " - " + newClientSocket.socket().getInetAddress().getHostAddress() + " - " + newClientSocket.socket().getInetAddress().getHostName());
						}
						list_clientStates.add(newClientPackage);
						list_clientSockets.add(newClientSocket);

					}
					else {

						oos.writeObject(new DataPackage(dp.getUsername(), 2, "Cannot get valid user name. Try to connect again."));

					}

				}
				catch (Exception e) {e.printStackTrace();}

			}

		}

	};

	private static void initLists() {

		list_clientsModel = new DefaultListModel<String>();
		list_clients = new JList<String>(list_clientsModel);
		list_clientSockets = (List<SocketChannel>)Collections.synchronizedList(new ArrayList<SocketChannel>());
		list_clientStates = (List<DataPackage>)Collections.synchronizedList(new ArrayList<DataPackage>());

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
				int i = 0;
				for (SocketChannel clientSocket : list_clientSockets) {

					try {
						clientSocket.close();
					}
					catch (IOException e) {}
					list_clientSockets.set(i, null);
					i++;

				}
				try {
					acceptThread.join();
					//receiveThread.join();
					//sendThread.join();
					//gameThread.join();
				}
				catch (InterruptedException e) {}
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
				selected = list_clients.getSelectedIndex();

				if (selected != -1) {

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
		    socketChannel.socket().bind(new InetSocketAddress(ip_4, PORT));
			isRunning = true;
			acceptThread = new Thread(accept);
			acceptThread.setDaemon(true);
			acceptThread.start();
			//receiveThread = new Thread(receive).start();
			//sendThread = new Thread(send).start();
			//gameThread = new Thread(game).start();

		}
		catch (IOException e) {

			JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error Alert!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(1);

		}

	}

}