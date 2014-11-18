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

import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;

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

/**
 * CLIENT STATES:
 * 100 - All is good normal operations
 * 200 - Invalid username
 * 300 - Client is disconnecting
 * 400 - Server is disconnecting Client
 * 500 - Server is shutting down
**/
public class Server {

	// Socket data
	public final int PORT = 4444;
	public String ip_4 = "";
	public String ip_6 = "";
	public ServerSocketChannel socketChannel = null;

	// Sever data
	public CopyOnWriteArrayList<ClientThread> list_clientThreads = null;
	public JList<String> list_clients = null;
	public DefaultListModel<String> list_clientsModel = null;

	// Window data
	public JFrame frame = null;
	public JPanel content = null;
	public JPanel panel1 = null;
	public JPanel panel2 = null;
	public JPanel panel3 = null;
	public JButton disconnectBtn = null;

	// Thread data
	public volatile boolean isRunning = false;
	public Thread acceptThread = null;
	public Thread gameThread = null;

	private class ClientThread extends Thread {

		private SocketChannel socketChannel = null;

		private ReceiveThread receiveThread = null;
		private SendThread sendThread = null;

		private DataPackage dataState = null;

		private ObjectOutputStream oos = null;
		private ObjectInputStream ois = null;

		public ClientThread(SocketChannel s, String username, ObjectOutputStream oos, ObjectInputStream ois) {

			socketChannel = s; 
			dataState = new DataPackage(username, 100, "");
			this.oos = oos;
			this.ois = ois;

			sendThread = new SendThread(this);
			sendThread.setName("Send Thread");
			//sendThread.setDaemon(true);

			receiveThread = new ReceiveThread(this);
			receiveThread.setName("Receive Thread");
			//receiveThread.setDaemon(true);

		}

		@Override
		public void run() {

			receiveThread.start();
			sendThread.start();
			while(isRunning) {
				try {
					Thread.sleep(100l);
				}
				catch (InterruptedException e1) {}
			}
			receiveThread.interrupt();
			try {
				receiveThread.join(100l);
			}
			catch (InterruptedException e) {}
			sendThread.interrupt();
			try {
				sendThread.join(100l);
			}
			catch (InterruptedException e) {}

		}

		public synchronized DataPackage getDataState() {
			return dataState;
		}

		public synchronized void setDataState(DataPackage dataState) {
			synchronized (this.dataState) {
				this.dataState = dataState;
			}
		}

		public synchronized ObjectOutputStream getOos() {
			return oos;
		}

		public synchronized ObjectInputStream getOis() {
			return ois;
		}

		public synchronized SocketChannel getSocketChannel() {
			return socketChannel;
		}

		public synchronized SendThread getSendThread() {
			return sendThread;
		}
		public synchronized ReceiveThread getReceiveThread() {
			return receiveThread;
		}

	}

    private class AcceptThread extends Thread {

		public AcceptThread() {
			super();
		}

		@Override
		public void run() {

			while (isRunning) {

				/*try {
					Thread.sleep(100l);
				}
				catch (InterruptedException e1) {}*/

				try {

					SocketChannel newClientSocket = null;
					while (isRunning && newClientSocket == null) {

						socketChannel.configureBlocking(false);
						newClientSocket = socketChannel.accept();
						socketChannel.configureBlocking(true);

						synchronized (list_clientThreads) {

							int size = list_clientThreads.size();
							for (int i = 0; i < size; i++) {

								if (i >= 0 && i < list_clientThreads.size() &&
								    (list_clientThreads.get(i).getDataState().getState() == 300 ||
								     list_clientThreads.get(i).getDataState().getState() == 400 ||
								     list_clientThreads.get(i).getDataState().getState() == 500))
									disconnectClient(i);

							}

						}

					}
					ObjectInputStream ois = new ObjectInputStream(newClientSocket.socket().getInputStream());
					ObjectOutputStream oos = new ObjectOutputStream(newClientSocket.socket().getOutputStream());

					DataPackage dp = null;
					String username = "";
					int count = 0;
					while (count < 2 && username.toLowerCase().equals("")) {

						dp = (DataPackage)ois.readObject();
						if (dp.getState() != 100) {

							count = 2;
							break;

						}
						username = dp.getUsername();

						synchronized (list_clientThreads) {
							for (ClientThread client : list_clientThreads) {

								DataPackage clientData = client.getDataState();
								if (clientData.getUsername().toLowerCase().equals(username.toLowerCase())) {

									oos.flush();
									oos.writeObject(new DataPackage(username, 200, "Username already choosen!"));
									oos.flush();
									username = "";
									break;

								}

							}
						}
						count++;

					}

					if (count < 2) {

						DataPackage newClientPackage = new DataPackage(username, 100, "Welcome to the server!");
						oos.flush();
						oos.writeObject(newClientPackage);
						oos.flush();

						synchronized (list_clientsModel) {
							list_clientsModel.addElement(dp.getUsername() + " - " + newClientSocket.socket().getInetAddress().getHostAddress() + " - " + newClientSocket.socket().getInetAddress().getHostName());
						}
						ClientThread newClient = new ClientThread(newClientSocket, username, oos, ois);
						newClient.setName("Client Thread");
						newClient.start();
						list_clientThreads.add(newClient);

					}
					else {

						oos.flush();
						oos.writeObject(new DataPackage(dp.getUsername(), 400, "Connection failed!"));
						oos.flush();

					}

				}
				catch (Exception e) {}

			}

		}

	}

	private class SendThread extends Thread {

		private ClientThread parrent = null;

		public SendThread(ClientThread parrent) {

			this.parrent = parrent;

		}

		@Override
		public void run() {

			boolean done = false;
			while(isRunning && !done) {

				/*try {
					Thread.sleep(100l);
				}
				catch (InterruptedException e1) {}*/

				try {

					parrent.getOos().flush();
					parrent.getOos().writeObject(parrent.getDataState());
					parrent.getOos().flush();
					if (parrent.getDataState().getState() == 300 ||
						parrent.getDataState().getState() == 400 ||
						parrent.getDataState().getState() == 500)
						done = true;

				}
				catch (Exception e) {
					done = true;
					parrent.getDataState().setState(400);
				}

			}

		}

	}

	private class ReceiveThread extends Thread {

		private ClientThread parrent = null;

		public ReceiveThread(ClientThread parrent) {

			this.parrent = parrent;

		}

		@Override
		public void run() {

			boolean done = false;
			while(isRunning && !done) {

				/*try {
					Thread.sleep(100l);
				}
				catch (InterruptedException e1) {}*/

				try {

					parrent.setDataState((DataPackage)parrent.getOis().readObject());
					if (parrent.getDataState().getState() == 300 ||
						parrent.getDataState().getState() == 400 ||
						parrent.getDataState().getState() == 500)
						done = true;

				}
				catch (Exception e) {
					done = true;
					parrent.getDataState().setState(400);
				}

			}

		}

	}

	public synchronized void disconnectClient(int index) {

		synchronized (list_clientThreads) {

			if (index >= 0 && index < list_clientThreads.size()) {
	
				synchronized (list_clientsModel) {
					list_clientsModel.remove(index);
				}
				ClientThread t = list_clientThreads.get(index);
				try {

					t.interrupt();
					t.join(100l);
		
				}
				catch (Exception e) {}
				list_clientThreads.remove(index);
	
			}

		}

	}

	private void initLists() {

		list_clientsModel = new DefaultListModel<String>();
		list_clients = new JList<String>(list_clientsModel);
		list_clientThreads = new CopyOnWriteArrayList<ClientThread>();

	}

	private void createServerFrame() {

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
				acceptThread.interrupt();
				try {
					acceptThread.join(100l);
				}
				catch (InterruptedException e) {}
				synchronized (list_clientThreads) {

					int size = list_clientThreads.size();
					while(size > 0) {

						for (int i = 0; i < size; i++) {

							disconnectClient(i);

						}
						size = list_clientThreads.size();

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

					disconnectClient(selected);

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
		frame.setSize(panel3.getWidth() + 20, panel3.getWidth() + 20);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setMinimumSize(frame.getSize());

	}

	private void getIp() {

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

	public Server () {

		//Init stuff
		initLists();
		getIp();
		createServerFrame();

		//Start the server
		try {

		    socketChannel = ServerSocketChannel.open();
		    socketChannel.socket().bind(new InetSocketAddress(PORT));
			isRunning = true;
			acceptThread = new AcceptThread();
			//acceptThread.setDaemon(true);
			acceptThread.setName("Accept Thread");
			acceptThread.start();
			//gameThread = new Thread(game)

		}
		catch (IOException e) {

			JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error Alert!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(1);

		}

	}

	public static void main(String[] args) {

		//Set the look and feel of windows
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception ex) {}
		new Server();

	}

}