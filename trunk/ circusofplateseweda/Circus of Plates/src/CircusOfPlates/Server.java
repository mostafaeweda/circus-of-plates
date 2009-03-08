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
	private boolean working;

	public Server(CircusUI facade, Display display) {
		this.facade = facade;
		this.display = display;
	}

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

	private void waitForConnection() throws IOException {
		connection = server.accept();
	}

	private void getStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		setWorking(true);
	}

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

	private void closeConnection() {
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void sendData(Object obj) {
		try {
			output.writeObject(obj);
			output.flush();
		} catch (IOException ioException) {
		}
	}

	public void setWorking(boolean working) {
		this.working = working;
	}

	public boolean isWorking() {
		return working;
	}
}
