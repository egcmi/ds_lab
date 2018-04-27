import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

	public static void main(String args[]) {
		int port = 6789;
		ServerHandler sh = new ServerHandler(port);
		sh.startServer();
	}
}

class ServerHandler {
	Map<Integer, Connection> clients;
	ServerSocket socket;
	ServerListener listener;

	public ServerHandler(int port) {
		clients = new HashMap<Integer, Connection>();
		try {
			socket = new ServerSocket(port);
			listener = new ServerListener(socket, clients);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void startServer() {
		listener.start();
	}

}

class ServerListener extends Thread {
	Map<Integer, Connection> clients;
	ServerSocket socket;
	Integer id;

	public ServerListener(ServerSocket socket, Map<Integer, Connection> clients) {
		this.socket = socket;
		this.clients = clients;
		id = 0;
	}

	public void run() {
		try {
			while (true) {
				Socket clientSocket = socket.accept();
				Connection c = new Connection(clientSocket, id, clients);
				id++;
				c.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

class Connection extends Thread {
	DataOutputStream out;
	DataInputStream in;
	Socket clientSocket;
	Map<Integer, Connection> clients;
	int id;

	public Connection(Socket clientSocket, int id, Map<Integer, Connection> clients) throws IOException {
		this.clientSocket = clientSocket;
		this.id = id;
		this.clients = clients;
		this.clients.put(id, this);
		in = new DataInputStream(clientSocket.getInputStream());
		out = new DataOutputStream(clientSocket.getOutputStream());
	}

	public void run() {
		String data;
		try {
			while (!clientSocket.isClosed()) {
				if (in.available() > 0) {
					try {
						data = in.readUTF();
						System.out.println(data);

						if (data.equals("/quit")) {
							clientSocket.close();
							System.out.printf("Client %d left the chat\n", id);
							clients.remove(id);
						}
						out.writeUTF("reply: " + data);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
