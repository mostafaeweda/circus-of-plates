package CircusOfPlates;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.eclipse.swt.widgets.Display;

/**
 * The client class in the network application
 * 
 * @author Mostafa Mahmod Mahmod Eweda
 * @version 1.0
 * @see Server
 * @since JDK 1.6
 */
public class Client {

	private Socket connection;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private boolean working = false;
	private String hostIP;
	private CircusUI facade;

	/**
	 * creates a new Client at specified location IP
	 * 
	 * @param hostIP
	 */
	public Client(CircusUI facade, String hostIP) {
		this.facade = facade;
		this.hostIP = hostIP;
	}

	/**
	 * runs the client in the networking thread
	 * 
	 * @throws Exception
	 */
	public void runClient() throws Exception {
		connection = new Socket(InetAddress.getByName(hostIP), 12345);
		getStreams();
		processConnection();
		closeConnection();
	}

	private void getStreams() {
		try {
			input = new ObjectInputStream(connection.getInputStream());
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			working = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * close the connection when done
	 */
	private void closeConnection() {
		try {
			input.close();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * process the connection by receiving messages and do resulting actions
	 */
	private void processConnection() {
		Object o = null;
		try {
			do {
				o = input.readObject();
				System.out.println("fee 7aga gat le el client");
				if (o instanceof PlateBar[]) {
					final PlateBar[] current = (PlateBar[]) o;
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							facade.setPlateBars(current);
						}
					});
				}
			} while (o != null);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * returns true if the client is the active one
	 * @return
	 */
	public boolean isWorking() {
		return working;
	}

	/**
	 * send data the server
	 * @param obj the object to be sent to the server
	 */
	public void sendData(Object obj) {
		try {
			output.writeObject(obj);
			output.flush();
		} catch (IOException ioException) {
		}
	}
}
