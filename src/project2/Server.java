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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

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

import org.newdawn.slick.Input;

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
	public ServerSocket socket = null;

	// Sever data
	private final int MAX_CLIENTS = 4;
	private int numClients = 0;
	public final String MSG_000 = "Welcome to the server";
	public final String MSG_200 = "Invalid Username";
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
	public volatile boolean serverIsRunning = false;
	public AcceptThread acceptThread = null;
	public GameThread gameThread = null;

	private class GameThread extends Thread {

		private GameState currentGameState = null;

		public void setGameState(GameState gs) {

			currentGameState = gs;

		}
		public GameState getGameState() {

			return currentGameState;

		}

		@Override
		public void run() {

			while (serverIsRunning) { 

				if (currentGameState == null || currentGameState.playerCarts == null)
					continue;

				for (String key : currentGameState.playerCarts.keySet()) {
	
					currentGameState.timer += (long)currentGameState.deltas.get(key).intValue();
					if (currentGameState.timer < 3000l) {

						currentGameState.containers.get(key).getInput().clearControlPressedRecord();
						currentGameState.containers.get(key).getInput().clearKeyPressedRecord();
						return;

					}
					Cart c = currentGameState.playerCarts.get(key);
					c.update(currentGameState.containers.get(key), currentGameState.games.get(key), currentGameState.deltas.get(key));
					if (c.getX() >= ((float)BlackFridayBlitz.MAX_WINDOW_WIDTH) / 3.0f)
						c.setJumpPoint(400.0f);
					if (c.getWorldX() >= BlackFridayBlitz.MAX_WINDOW_WIDTH * 2 + 128) {
						c.MAX_SCREEN_X = BlackFridayBlitz.MAX_WINDOW_WIDTH - 300;
						c.setWorldX(BlackFridayBlitz.MAX_WINDOW_WIDTH * 2 + 128);
						return;
					}
	
					Input input = currentGameState.containers.get(key).getInput();
					if (input.isKeyPressed(Input.KEY_UP) && c.getY() == c.getJumpPoint())
						c.setJumpPoint(c.getY() - 175.0f);
					if (input.isKeyPressed(Input.KEY_DOWN) && c.getY() == c.getJumpPoint())
						c.setJumpPoint(c.getY() + 175.0f);
	
				}

			}

		}

	}

	private class ClientThread extends Thread {

		private Server main = null;
		private Socket socket = null;

		private ReceiveThread receiveThread = null;
		private SendThread sendThread = null;
		private GameThread gameThreadReference = null;

		private DataPackage dataState = null;

		private ObjectOutputStream oos = null;
		private ObjectInputStream ois = null;

		protected volatile boolean clientIsRunning = false;

		public ClientThread(Socket s, String username, ObjectOutputStream oos, ObjectInputStream ois, Server m, GameThread game) {

			main = m;
			socket = s;
			dataState = new DataPackage(username, 100, DataPackage.MSG_100, new GameState());
			this.oos = oos;
			this.ois = ois;

			sendThread = new SendThread(this);
			sendThread.setName("Send Thread");
			sendThread.setDaemon(true);

			receiveThread = new ReceiveThread(this);
			receiveThread.setName("Receive Thread");
			receiveThread.setDaemon(true);

			gameThreadReference = game;

			clientIsRunning = true;

		}

		@Override
		public void run() {

			receiveThread.start();
			sendThread.start();
			int state = 100;
			while(serverIsRunning && clientIsRunning && state == 100) {
				try {
					Thread.sleep(100l);
				}
				catch (InterruptedException e1) {}
				synchronized(dataState) {
					state = dataState.getState();
				}
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
			main.clean();

		}

		public synchronized DataPackage getDataState() {
			return dataState;
		}

		public synchronized void setDataState(DataPackage dataState) {
			synchronized (this.dataState) {
				this.dataState = dataState;
				System.out.println(this.dataState.getGameData());
				gameThreadReference.setGameState((GameState)this.dataState.getGameData());
			}
		}

		public synchronized ObjectOutputStream getOos() {
			return oos;
		}

		public synchronized ObjectInputStream getOis() {
			return ois;
		}

		public synchronized Socket getSocket() {
			return socket;
		}

		public synchronized SendThread getSendThread() {
			return sendThread;
		}

		public synchronized ReceiveThread getReceiveThread() {
			return receiveThread;
		}

	}

    private class AcceptThread extends Thread {

    	private Server main = null;
    	private GameThread gameThread = null;

		public AcceptThread(Server m, GameThread game) {

			super();
			main = m;
			gameThread = game;

		}

		@Override
		public void run() {

			while (serverIsRunning) {

				try {

					Socket newClientSocket = null;
					if (numClients >= MAX_CLIENTS)
						continue;
					newClientSocket = socket.accept();
					numClients++;
					if (newClientSocket == null)
						continue;

					ObjectInputStream ois = new ObjectInputStream(newClientSocket.getInputStream());
					ObjectOutputStream oos = new ObjectOutputStream(newClientSocket.getOutputStream());

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
									oos.writeObject(new DataPackage(username, 200, MSG_200, null));
									oos.flush();
									username = "";
									break;

								}

							}
						}
						count++;

					}

					if (count < 2) {

						DataPackage newClientPackage = new DataPackage(username, 0, MSG_000, null);
						oos.flush();
						oos.writeObject(newClientPackage);
						oos.flush();

						synchronized (list_clientsModel) {
							list_clientsModel.addElement(dp.getUsername() + " - " + newClientSocket.getInetAddress().getHostAddress() + " - " + newClientSocket.getInetAddress().getHostName());
						}
						ClientThread newClient = new ClientThread(newClientSocket, username, oos, ois, main, gameThread);
						newClient.setName("Client Thread");
						newClient.start();
						list_clientThreads.add(newClient);

					}
					else {

						oos.flush();
						oos.writeObject(new DataPackage(dp.getUsername(), 400, DataPackage.MSG_400, null));
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

			while(serverIsRunning && parrent.clientIsRunning) {

				try {
					Thread.sleep(16l);
				}
				catch (InterruptedException e1) {}

				try {

					parrent.getOos().flush();
					System.out.println("write username: " + parrent.getDataState().getUsername() + "\twrite state: " + parrent.getDataState().getState() + "\twrite message: " + parrent.getDataState().getMessage() + "\n");
					DataPackage dp = null;
					synchronized (parrent) {
						dp = new DataPackage(parrent.getDataState().getUsername(), parrent.getDataState().getState(), parrent.getDataState().getMessage(), parrent.getDataState().getGameData());
					}
					parrent.getOos().writeObject(dp);
					//System.out.println("AFTER WRITE");
					parrent.getOos().flush();
					if (parrent.getDataState().getState() != 100)
						parrent.clientIsRunning = false;

				}
				catch (IOException e) {

					//System.out.println("WRITE ERROR: " + e.getMessage());
					parrent.clientIsRunning = false;
					parrent.getDataState().setState(400);
					parrent.getDataState().setMessage(DataPackage.MSG_400);

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

			while(serverIsRunning && parrent.clientIsRunning) {

				try {

					//System.out.println("BEFORE READ");
					DataPackage dp = null;
					dp = (DataPackage)parrent.getOis().readObject();

					System.out.println("read username: " + dp.getUsername() + "\tread state: " + dp.getState() + "\tread message: " + dp.getMessage() + "\n");
					synchronized (parrent) {
						parrent.setDataState(new DataPackage(dp.getUsername(), dp.getState(), dp.getMessage(), dp.getGameData()));
					}
					if (parrent.getDataState().getState() != 100)
						parrent.clientIsRunning = false;

				}
				catch (IOException|ClassNotFoundException e) {

					//System.out.println("READ ERROR: " + e.getMessage());
					parrent.clientIsRunning = false;
					parrent.getDataState().setState(400);
					parrent.getDataState().setMessage(DataPackage.MSG_400);

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
				ClientThread ct = list_clientThreads.get(index);
				if (ct.getDataState().getState() != 300 &&
					ct.getDataState().getState() != 400 &&
					ct.getDataState().getState() != 500) {

					ct.setDataState(new DataPackage(ct.getDataState().getUsername(), 400, DataPackage.MSG_400, ct.getDataState().getGameData()));

				}
				try {
					ct.getOos().flush();
					ct.getOos().writeObject(ct.getDataState());
					ct.getOos().flush();
				}
				catch (IOException e1) {}

				try {

					SendThread st = ct.getSendThread();
					st.interrupt();
					st.join(100l);
					ReceiveThread rt = ct.getReceiveThread();
					rt.interrupt();
					rt.join(100l);
					ct.getOis().close();
					ct.getOos().close();
					ct.getSocket().close();
					ct.join(100l);

				}
				catch (Exception e) {}
				list_clientThreads.remove(index);
				numClients--;

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

				serverIsRunning = false;
				synchronized (list_clientThreads) {

					int size = list_clientThreads.size();
					while(size > 0) {

						for (int i = 0; i < size; i++) {

							if (i >=  0  && i < list_clientThreads.size()) {

								list_clientThreads.get(i).setDataState(new DataPackage(list_clientThreads.get(i).getDataState().getUsername(), 500, DataPackage.MSG_500, list_clientThreads.get(i).getDataState().getGameData()));
								disconnectClient(i);

							}

						}
						size = list_clientThreads.size();

					}

				}
				try {
					socket.close();
				}
				catch (IOException e1) {}
				try {
					acceptThread.join(100l);
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

				synchronized (list_clientThreads) {

					int selected = 0;
					synchronized (list_clients) {
						selected = list_clients.getSelectedIndex();
					}

					if (selected != -1) {

						list_clientThreads.get(selected).setDataState(new DataPackage(list_clientThreads.get(selected).getDataState().getUsername(), 400, DataPackage.MSG_400, list_clientThreads.get(selected).getDataState().getGameData()));

					}

				}
				clean();

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

	public void clean() {

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

	public Server () {

		//Init stuff
		initLists();
		getIp();
		createServerFrame();

		//Start the server
		try {

		    socket = new ServerSocket();
		    socket.bind(new InetSocketAddress(PORT));
			serverIsRunning = true;
			gameThread = new GameThread();
			gameThread.start();
			acceptThread = new AcceptThread(this, gameThread);
			acceptThread.setDaemon(true);
			acceptThread.setName("Accept Thread");
			acceptThread.start();

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