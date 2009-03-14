package CircusOfPlates;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.swt.widgets.Display;

/**
 * The server class in the network application
 * 
 * @author Mostafa Mahmod Mahmod Eweda
 * @version 1.0
 * @see Client
 * @since JDK 1.6
 */
public class Server {

	private ServerSocket server;
	private Socket connection;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private CircusUI facade;
	private Display display;

	/**
	 * true when the current connection side is the server
	 */
	private boolean working;

	/**
	 * creates a server
	 * @param facade the above layer to call the methods from
	 * @param display the display that will handle multi-threading
	 */
	public Server(CircusUI facade, Display display) {
		this.facade = facade;
		this.display = display;
	}

	/**
	 * run the server services from the connection thread ant stays here till the end of the connection
	 */
	public void runServer() {
		try {
			server = new ServerSocket(12345, 100);
			while (true) {
				try {
					waitForConnection();
					getStreams();
					processConnection();
					closeConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	/**
	 * wait for the client to connect
	 * @throws IOException
	 */
	private void waitForConnection() throws IOException {
		connection = server.accept();
	}

	/**
	 * Get the input and output streams from the client to deal with it sending and receiving data
	 * @throws IOException
	 */
	private void getStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		setWorking(true);
	}

	/**
	 * Process connection requirements
	 * @throws IOException
	 */
	private void processConnection() throws IOException {
		Object o = null;
		do {
			try {
				o = input.readObject();
				System.out.println("feeh 7aga wasalet le el server");
				if (o instanceof String) {
					String s = (String) o;
					if (s.equals("New Game")) {
						display.syncExec(new Runnable(){
							@Override
							public void run() {
								PlateBar.createFirst();
								facade.newGame();
							}});
					}
				}
			} catch (ClassNotFoundException classNotFoundException) {
			}
		} while (o != null);
	}

	/**
	 * Close the connection when done
	 */
	private void closeConnection() {
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	/**
	 * sends an object to the client
	 * @param obj the object to send
	 */
	public void sendData(Object obj) {
		try {
			output.writeObject(obj);
			output.flush();
		} catch (IOException ioException) {
		}
	}

	/**
	 * sets the current program to be as a server working
	 * @param working
	 */
	public void setWorking(boolean working) {
		this.working = working;
	}

	/**
	 * checks if the current program is running as a server
	 * @return
	 */
	public boolean isWorking() {
		return working;
	}
}
