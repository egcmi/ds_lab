import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Diese Klasse liest die Serveradresse und den Port vom Benutzer und gibt sie
 * an den ClientHandler weiter, um eine Verbindung aufzubauen. Der Scanner wird
 * auch als Parameter für einen Workaround übergeben, den wir in unserem Bericht
 * erwähnt haben.
 * 
 * @author Emanuela Calabi - 13186
 * @author Angelo Rosace - 13386
 *
 */
public class Client {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter server address: ");
		String host = scanner.nextLine();
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
		scanner.nextLine();

		ClientHandler conn = new ClientHandler(host, port, scanner);
		conn.startChat();
	}
}

/**
 * Diese Klasse stellt eine Verbindung zum Server her, fragt nach einem
 * Benutzernamen und führt dann die Sender- und Empfänger-Threads aus, um mit
 * dem Server zu kommunizieren.
 * 
 * @author Emanuela Calabi - 13186
 * @author Angelo Rosace - 13386
 */
class ClientHandler {
	Socket socket;
	String username;
	DataOutputStream out;
	DataInputStream in;
	ClientReceiver receiver;
	ClientSender sender;

	/**
	 * Stellt die Verbindung zum Server her und erhält den Benutzernamen vom
	 * Benutzer. Erstellt dann untergeordnete Sender- und Empfänger-Threads.
	 * 
	 * @param host
	 *            die Server-Addresse
	 * @param port
	 *            der Server-Port
	 * @param scanner
	 *            Workaround @see Client
	 */
	public ClientHandler(String host, int port, Scanner scanner) {
		try {
			socket = new Socket(InetAddress.getByName(host), port);
			System.out.printf("Connection established with host %s at port %d.\n", socket.getInetAddress(),
					socket.getPort());

			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

			username = "";
			String msg = "";
			while (true) {
				System.out.print("Enter your username: ");
				username = scanner.nextLine();
				if (username.length() == 0) {
					System.out.println("Username too short.");
					continue;
				}
				out.writeUTF(username);
				msg = in.readUTF();
				if (msg.equals("y")) {
					System.out.printf("You are now logged in as %s. Type \"/quit\" to exit.\n", username);
					break;
				}
				System.out.printf("Username %s is already taken.\n", username);
				username = null;
			}
			sender = new ClientSender(socket, out, scanner);
			receiver = new ClientReceiver(socket, in);
		} catch (UnknownHostException e) {
			System.err.printf("Unknown host %s. Terminating.\n", host);
			System.exit(1);
		} catch (IOException e) {
			System.out.printf("Could not connect to %s:%d. Terminating.\n", host, port);
			System.exit(1);
		}
	}

	/**
	 * Startet die untergeordnete Sender- und Empfänger-Threads
	 */
	public void startChat() {
		receiver.start();
		sender.start();
	}

	/**
	 * empfängt Nachrichten vom Server
	 * 
	 * @author Emanuela Calabi - 13186
	 * @author Angelo Rosace - 13386
	 *
	 */
	private class ClientReceiver extends Thread {
		Socket socket;
		DataInputStream in;

		/**
		 * erstellt den Empfänger-Thread
		 * 
		 * @param socket
		 *            Verbindung zum Server
		 * @param in
		 *            eingehender Eingangsstrom vom Server
		 */
		private ClientReceiver(Socket socket, DataInputStream in) {
			this.socket = socket;
			this.in = in;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * schleift für eingehende Nachrichten vom Server, bis die Verbindung
		 * geschlossen wird, und druckt sie auf der Konsole aus.
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			try {
				while (!socket.isClosed()) {
					System.out.println(in.readUTF());
				}
			} catch (IOException e) {
				System.err.println("Disconnected. Terminating.");
				System.exit(1);
			}
		}
	}

	/**
	 * liest Nachrichten vom Benutzer und sendet sie an den Server
	 * 
	 * @author Emanuela Calabi - 13186
	 * @author Angelo Rosace - 13386
	 *
	 */
	private class ClientSender extends Thread {
		Socket socket;
		DataOutputStream out;
		Scanner scanner;

		/**
		 * erstellt den Sender-Thread
		 * 
		 * @param socket
		 *            Verbindung zum Server
		 * 
		 * @param out
		 *            ausgehender Ausgangsdatenstrom zum Server
		 * @param scanner
		 *            Workaround @see Client
		 */
		private ClientSender(Socket socket, DataOutputStream out, Scanner scanner) {
			this.socket = socket;
			this.out = out;
			this.scanner = scanner;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * es schleift für Benutzereingaben und sendet sie an den Server, bis die
		 * Verbindung nicht geschlossen wird. Wenn der Benutzer "/quit" eingibt, wird
		 * die Verbindung geschlossen und das Programm beendet.
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			String input;
			try {
				while (true) {
					input = scanner.nextLine();
					out.writeUTF(input);
					if (input.equals("/quit")) {
						scanner.close();
						socket.close();
						break;
					}
				}
			} catch (IOException e) {
				System.err.println("Disconnected. Terminating.");
				System.exit(1);
			}
		}
	}
}