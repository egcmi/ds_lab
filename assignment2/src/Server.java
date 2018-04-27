import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Diese Klasse liest den Port vom Benutzer und übergibt ihn an den
 * ServerHandler, der den Server-Socket startet.
 * 
 * @author Emanuela Calabi - 13186
 * @author Angelo Rosace - 13386
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
 * diese Klasse setzt den Server-Socket für das Abhören neuer Verbindungen und
 * gibt ihn dann an den Listener-Thread weiter.
 * 
 * @author Emanuela Calabi - 13186
 * @author Angelo Rosace - 13386
 *
 */
class ServerHandler {
	Map<String, Connection> clients;
	ServerSocket socket;
	ServerListener listener;

	/**
	 * erstellt den Server-Socket auf dem angegebenen Port und erstellt einen
	 * Listener-Thread, um diesen Socket anzuhören.
	 * 
	 * @param port
	 *            die Portnummer
	 */
	public ServerHandler(int port) {
		clients = new HashMap<String, Connection>();
		try {
			socket = new ServerSocket(port);
			listener = new ServerListener(socket, clients);
		} catch (IOException e) {
			System.out.println("Connection error. Terminating.");
			System.exit(1);
		}
	}

	/**
	 * Startet der Listener-Thread
	 */
	public void startServer() {
		listener.start();
	}

}

/**
 * diese Klasse lauscht auf eingehende Verbindungen auf dem Socket-Port des
 * Servers.
 * 
 * @author Emanuela Calabi - 13186
 * @author Angelo Rosace - 13386
 *
 */
class ServerListener extends Thread {
	Map<String, Connection> clients;
	ServerSocket socket;

	/**
	 * Initialisiert den Listener
	 * 
	 * @param socket
	 *            der Server-Socket zum Anhören
	 * @param clients
	 *            die Datenstruktur, in der alle Client-Verbindungen gespeichert
	 *            sind
	 */
	public ServerListener(ServerSocket socket, Map<String, Connection> clients) {
		this.socket = socket;
		this.clients = clients;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * startet die Schleife, die eingehende Verbindungen entgegennimmt und jeweils
	 * das entsprechende Connection-Objekt erzeugt.
	 * 
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
			System.out.println("A connection error occurred.");
		}
	}

}

/**
 * Diese Klasse hält die Verbindung mit dem Client aufrecht, indem sie von ihm
 * liest und in ihn schreibt.
 * 
 * @author Emanuela Calabi - 13186
 * @author Angelo Rosace - 13386
 *
 */
class Connection extends Thread {
	DataOutputStream out;
	DataInputStream in;
	Socket clientSocket;
	Map<String, Connection> clients;
	String username;

	/**
	 * initialisiert die Verbindung, wartet dann auf die Eingabe eines gültigen
	 * Benutzernamens (einmalig und nicht leer), teilt ihre Gültigkeit dem Client
	 * mit und stoppt sie, wenn sie gültig ist. sendet dann an alle anderen Clients,
	 * dass ein neuer Benutzer dem Chat beigetreten ist und fügt die neue Verbindung
	 * zur HashMap hinzu, die alle Verbindungen für die zukünftige Verwendung
	 * enthält.
	 * 
	 * @param clientSocket
	 *            Verbindung zum Client
	 * @param clients
	 *            HashMap mit allen Clients
	 * @throws IOException
	 *             wenn es nicht gelingt, die Input- und Output-Streams vom
	 *             Client-Socket zu bekommen.
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

	/*
	 * (non-Javadoc)
	 * 
	 * informiert den Client darüber, wer online ist, und liest dann Nachrichten vom
	 * Client, bis die Verbindung geschlossen wird. Wenn die gelesene Nachricht
	 * "/quit" ist, schließt sie die Verbindung und entfernt sich von den Clients
	 * HashMap. sendet Nachrichten und Aktionen des Clients an die anderen Clients.
	 * 
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
	 * sendet die Eingabe-Strings an alle angeschlossenen Clients mit Ausnahme des
	 * sendenden Clients.
	 * 
	 * @param msg
	 *            Nachricht, die an alle Clients gesendet werden soll
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
