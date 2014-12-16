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
import java.util.HashMap;
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

import jig.Entity;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import project2.Cart.CartState;

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
	private final int MAX_CLIENTS = 1;
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
	public GameThread serverGameThread = null;

	private class GameThread extends Thread {

		ServerBlackFridayBlitz serverGame = null;

		private class ServerBlackFridayBlitz extends StateBasedGame {

			public ServerBlackFridayBlitz(String name) {

				super(name);
				Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);

			}

			@Override
			public void initStatesList(GameContainer contianer) throws SlickException {

				addState(new SeverMultiPlayerGameState());

			}

		}

		private class SeverMultiPlayerGameState extends BasicGameState {

			private GameState currentGameState = null;
			private volatile long frame = 0;
			private int finish = 0;
			private float finalTimer = 0.0f;
			private float pauseTimer = 0.0f;

			@Override
			public void init(GameContainer container, StateBasedGame game) throws SlickException {

				currentGameState = new GameState();
				frame = 0;

			}

			@Override
			public void render(GameContainer contianer, StateBasedGame game, Graphics g) throws SlickException {}

			@Override
			public void update(GameContainer contianer, StateBasedGame game, int delta) throws SlickException {

				//System.out.println("Frame: " + frame);
				synchronized(currentGameState) {

					if (currentGameState == null ||
						currentGameState.playerCarts == null ||
						currentGameState.playerCarts.size() < MAX_CLIENTS)
						return;

				}

				boolean framesCorrect = true;
				synchronized(currentGameState) {

					for (String key : currentGameState.frames.keySet()) {

						if (frame != currentGameState.frames.get(key).longValue()) {
							framesCorrect = false;
							break;
						}

					}

				}
				if (!framesCorrect)
					return;

				frame++;

				//System.out.println("delta: " + delta);
				synchronized(currentGameState) {

					currentGameState.timer += delta;
					if (currentGameState.timer >= 3000l) {
	
						for (String key : currentGameState.frames.keySet()) {
		
							if (finish == 1) {
								currentGameState.inputs.put(key, null);
							}
		
							Cart c = currentGameState.playerCarts.get(key).getCart(false);
							HashMap<String, Boolean> inputs = currentGameState.inputs.get(key);
							c.update(inputs, delta);
							if (c.getX() >= ((float)BlackFridayBlitz.MAX_WINDOW_WIDTH) / 3.0f)
								c.setJumpPoint(440.0f);
			
							/*if (c.getWorldX() >= BlackFridayBlitz.MAX_WINDOW_WIDTH * 2 + 200) {//level.getLength()
			
								if (finish == 0) {
		
									finish = 1;
									finalTimer = currentGameState.timer - 3000;
									pauseTimer = currentGameState.timer + 3000;
		
								}
								if (currentGameState.timer > pauseTimer) {
		
									//((SinglePlayerResultsState)game.getState(BlackFridayBlitz.SP_RESULTS_STATE)).setTime(cart, finalTime);
									//game.enterState(BlackFridayBlitz.SP_RESULTS_STATE);
		
								}
								c.MAX_SCREEN_X = BlackFridayBlitz.MAX_WINDOW_WIDTH - 300;
								c.setWorldX(BlackFridayBlitz.MAX_WINDOW_WIDTH * 2 + 200);//level.getLength()
								return;
		
							}*/
							
							if (inputs.get("up") && c.getY() == c.getJumpPoint()) {
		
								if(c.getPlatform() < 3) {//level.platformY.size() - 1
		
									c.setPlatform(c.getPlatform() + 1);
									c.setJumpPoint(200.0f);//level.platformY.get(platform)
		
								}
		
							}
		
							if (inputs.get("down") && c.getY() == c.getJumpPoint()) {
		
								if(c.getPlatform() > 0) {
		
									c.setPlatform(c.getPlatform() - 1);
									c.setJumpPoint(400.0f);//level.platformY.get(platform)
		
								}
		
							}
	
							currentGameState.playerCarts.put(key, new CartState(c.getX(), c.getY(), c.getCoarseGrainedWidth(), c.getCoarseGrainedHeight(), c.getNumSpeedUps(), c.getCurrentSpeed(), c.getBatteryBoost(), c.getWorldX(), c.getWorldY(), c.getPlatform(), c.getJumpPoint(), c.getImageString()));
	
						}

					}

				}

				synchronized(list_clientThreads) {

					for (ClientThread ct : list_clientThreads) {
						GameState gs = new GameState();
						synchronized(currentGameState) {
							gs.playerCarts = new HashMap<String, CartState>(currentGameState.playerCarts);
							gs.inputs = new HashMap<String, HashMap<String, Boolean>>(currentGameState.inputs);
							gs.frames = new HashMap<String, Long>(currentGameState.frames);
							gs.frames.put(ct.getDataState().getUsername(), new Long(frame));
							gs.timer = currentGameState.timer;
							//System.out.println("server timer: " + currentGameState.timer);
						}
						ct.setGameState(gs);
					}

				}

			}

			public synchronized void resetState() {

				synchronized(currentGameState) {
					currentGameState = new GameState();
				}
				frame = 0;
				finish = 0;
				finalTimer = 0.0f;
				pauseTimer = 0.0f;

			}

			public synchronized void mergeGameState(String name, GameState gs) {

				//System.out.println("state: " + gs.playerCarts.size());
				synchronized(currentGameState) {
					currentGameState.playerCarts.put(name, gs.playerCarts.get(name));
				}
				//System.out.println("state2: " + currentGameState.playerCarts.size());

				synchronized(currentGameState) {
					currentGameState.inputs.put(name, gs.inputs.get(name));
				}

				synchronized(currentGameState) {

					long tempFrame = gs.frames.get(name).longValue();
					//System.out.println("Frame from client: " + tempFrame);
					currentGameState.frames.put(name, new Long(tempFrame));

				}


			}

			public synchronized GameState getGameState() {

				return currentGameState;

			}

			public synchronized long getFrame() {

				return frame;

			}

			@Override
			public int getID() {
				return 0;
			}

		}

		public GameThread() {

			serverGame = new ServerBlackFridayBlitz("ServerBlackFridayBlitz");
			try {
				serverGame.initStatesList(null);
				serverGame.getState(0).init(null, serverGame);
			}
			catch (SlickException e) {
				e.printStackTrace();
			}
			serverGame.enterState(0);

		}

		public void mergeGameState(String name, GameState gs) {

			((SeverMultiPlayerGameState)serverGame.getState(0)).mergeGameState(name, gs);

		}
		public long getFrame() {

			return ((SeverMultiPlayerGameState)serverGame.getState(0)).getFrame();

		}
		
		public void resetState() {

			((SeverMultiPlayerGameState)serverGame.getState(0)).resetState();

		}

		private long lastTime  = System.currentTimeMillis();
		@Override
		public void run() {

			lastTime  = System.currentTimeMillis();
			while (serverIsRunning) {

				try {
					Thread.sleep(16l);
				}
				catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				try {
					long currentTime = System.currentTimeMillis();
					long time = currentTime - lastTime;
					lastTime  = currentTime;
					//System.out.println("delta: " + time);
					serverGame.update(null, (int)time);
				}
				catch (SlickException e) {
					e.printStackTrace();
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
			dataState = new DataPackage(username, 100, DataPackage.MSG_100);
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
				//System.out.println("server calling gameThread merge");
				gameThreadReference.mergeGameState(this.dataState.getUsername(), this.dataState.getGameState());

			}

		}
		
		public synchronized void setGameState(GameState gs) {

			synchronized (dataState) {

				dataState.setGameState(gs);

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
    	private GameThread acceptGameThread = null;

		public AcceptThread(Server m, GameThread game) {

			super();
			main = m;
			acceptGameThread = game;

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

					ObjectOutputStream oos = new ObjectOutputStream(newClientSocket.getOutputStream());
					oos.flush();
					ObjectInputStream ois = new ObjectInputStream(newClientSocket.getInputStream());

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
									oos.writeObject(new DataPackage(username, 200, MSG_200));
									oos.flush();
									username = "";
									break;

								}

							}
						}
						count++;

					}

					if (count < 2) {

						DataPackage newClientPackage = null;
						if (acceptGameThread != null && acceptGameThread.getFrame() != 0) {

							newClientPackage = new DataPackage(username, 50, DataPackage.MSG_050);
							oos.flush();
							oos.writeObject(newClientPackage);
							oos.flush();
							continue;

						}
						newClientPackage = new DataPackage(username, 0, MSG_000);

						oos.flush();
						oos.writeObject(newClientPackage);
						oos.flush();

						synchronized (list_clientsModel) {
							list_clientsModel.addElement(dp.getUsername() + " - " + newClientSocket.getInetAddress().getHostAddress() + " - " + newClientSocket.getInetAddress().getHostName());
						}
						ClientThread newClient = new ClientThread(newClientSocket, username, oos, ois, main, acceptGameThread);
						newClient.setName("Client Thread");
						newClient.start();
						list_clientThreads.add(newClient);

					}
					else {

						oos.flush();
						oos.writeObject(new DataPackage(dp.getUsername(), 400, DataPackage.MSG_400));
						oos.flush();

					}

				}
				catch (Exception e) {}

			}

		}

	}

	private class SendThread extends Thread {

		private ClientThread parrent = null;
		private volatile long lastKnownFrame = -1;

		public SendThread(ClientThread parrent) {

			this.parrent = parrent;

		}

		@Override
		public void run() {

			while(serverIsRunning && parrent.clientIsRunning) {

				try {

					parrent.getOos().flush();
					//System.out.println("write username: " + parrent.getDataState().getUsername() + "\twrite state: " + parrent.getDataState().getState() + "\twrite message: " + parrent.getDataState().getMessage() + "\twrite gameState: " + parrent.getDataState().getGameState() + "\n");

					DataPackage currentState = null;
					synchronized (parrent) {
						 currentState = parrent.getDataState();
					}
					DataPackage dp = null;
					long frame = lastKnownFrame;
					GameState gs = currentState.getGameState();

					if (currentState != null &&
						currentState.getGameState() != null &&
						gs != null &&
						gs.frames.containsKey(currentState.getUsername()) &&
						gs.frames.get(currentState.getUsername()) != null)
						frame = gs.frames.get(currentState.getUsername()).longValue();

					if (frame == lastKnownFrame)
						continue;
					lastKnownFrame = frame;
					dp = new DataPackage(currentState.getUsername(), currentState.getState(), currentState.getMessage(), currentState.getGameState());
					parrent.getOos().writeObject(dp);

					//System.out.println("AFTER WRITE");
					parrent.getOos().flush();
					if (currentState.getState() != 100)
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
		private volatile long lastKnownFrame = -1;

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
					long frame = lastKnownFrame;
					GameState gs = dp.getGameState();
					if (dp != null &&
						dp.getGameState() != null &&
						gs.frames != null &&
						gs.frames.containsKey(dp.getUsername()) &&
						gs.frames.get(dp.getUsername()) != null)
						frame = gs.frames.get(dp.getUsername()).longValue();

					if (frame == lastKnownFrame)
						continue;
					lastKnownFrame = frame;

					//System.out.println("read username: " + dp.getUsername() + "\tread state: " + dp.getState() + "\tread message: " + dp.getMessage() + "\tread gameState: " + dp.getGameState() + "\n");
					synchronized (parrent) {
						parrent.setDataState(new DataPackage(dp.getUsername(), dp.getState(), dp.getMessage(), dp.getGameState()));
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

					ct.setDataState(new DataPackage(ct.getDataState().getUsername(), 400, DataPackage.MSG_400, ct.getDataState().getGameState()));

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
				if (numClients <= 1)
					serverGameThread.resetState();

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

								list_clientThreads.get(i).setDataState(new DataPackage(list_clientThreads.get(i).getDataState().getUsername(), 500, DataPackage.MSG_500, list_clientThreads.get(i).getDataState().getGameState()));
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
				try {
					serverGameThread.join(100l);
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

						list_clientThreads.get(selected).setDataState(new DataPackage(list_clientThreads.get(selected).getDataState().getUsername(), 400, DataPackage.MSG_400, list_clientThreads.get(selected).getDataState().getGameState()));

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

			serverGameThread = new GameThread();
			serverGameThread.setName("Game Thread");
			serverGameThread.start();

			acceptThread = new AcceptThread(this, serverGameThread);
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
