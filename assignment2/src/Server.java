import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author emanuela
 *
 */
public class Server {

	public static void main(String args[]) {
		Scanner scanner = new Scanner(System.in);
		int port;
		while (true) {
			System.out.print("Enter port number: ");
			if (scanner.hasNextInt()) {
				port = scanner.nextInt();
				if (port < 0xFFFF)
					break;
			}
			scanner.nextLine();
			System.out.println("Not a valid port number.");
		}
		ServerHandler sh = new ServerHandler(port);
		sh.startServer();
		scanner.close();
	}
}

/**
 * @author emanuela
 *
 */
class ServerHandler {
	Map<String, Connection> clients;
	ServerSocket socket;
	ServerListener listener;

	/**
	 * @param port
	 */
	public ServerHandler(int port) {
		clients = new HashMap<String, Connection>();
		try {
			socket = new ServerSocket(port);
			listener = new ServerListener(socket, clients);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void startServer() {
		listener.start();
	}

}

/**
 * @author emanuela
 *
 */
class ServerListener extends Thread {
	Map<String, Connection> clients;
	ServerSocket socket;

	/**
	 * @param socket
	 * @param clients
	 */
	public ServerListener(ServerSocket socket, Map<String, Connection> clients) {
		this.socket = socket;
		this.clients = clients;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		System.out.printf("Server started at port %d. Press <CTRL> + <C> to terminate.\n", socket.getLocalPort());
		try {
			while (true) {
				Socket clientSocket = socket.accept();
				Connection c = new Connection(clientSocket, clients);
				c.start();
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
		}
	}

}

/**
 * @author emanuela
 *
 */
class Connection extends Thread {
	DataOutputStream out;
	DataInputStream in;
	Socket clientSocket;
	Map<String, Connection> clients;
	String username;

	/**
	 * @param clientSocket
	 * @param clients
	 * @throws IOException
	 */
	public Connection(Socket clientSocket, Map<String, Connection> clients) throws IOException {
		this.clientSocket = clientSocket;
		this.clients = clients;
		in = new DataInputStream(clientSocket.getInputStream());
		out = new DataOutputStream(clientSocket.getOutputStream());

		while (true) {
			username = in.readUTF();
			if (!clients.containsKey(username)) {
				out.writeUTF("y");
				break;
			}
			username = null;
			out.writeUTF("n");
		}
		broadcast(username + " joined the chat.");
		this.clients.put(username, this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		String msg;
		try {
			msg = "Now online: ";
			for (String k : clients.keySet()) {
				msg += k + ", ";
			}
			out.writeUTF(msg.substring(0, msg.length() - 2));

			while (!clientSocket.isClosed()) {
				msg = in.readUTF();

				if (msg.equals("/quit")) {
					broadcast(username + " disconnected.");
					clients.remove(username);
					clientSocket.close();
				} else {
					broadcast(username + ": " + msg);
				}
			}

		} catch (IOException e) {
			broadcast(username + " disconnected.");
			clients.remove(username);
		}
	}

	/**
	 * @param msg
	 */
	public void broadcast(String msg) {
		System.out.println(msg);
		for (String s : clients.keySet()) {
			if (!s.equals(username)) {
				try {
					clients.get(s).out.writeUTF(msg);
				} catch (IOException e) {
					System.out.printf("Could not deliver message to %s.\n", s);
				}
			}
		}
	}
}
