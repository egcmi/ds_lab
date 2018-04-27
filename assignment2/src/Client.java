import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author emanuela
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
 * @author emanuela
 *
 */
class ClientHandler {
	Socket socket;
	String username;
	DataOutputStream out;
	DataInputStream in;
	ClientReceiver receiver;
	ClientSender sender;

	/**
	 * @param host
	 * @param port
	 * @param scanner
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
	 * 
	 */
	public void startChat() {
		receiver.start();
		sender.start();
	}

	/**
	 * @author emanuela
	 *
	 */
	private class ClientReceiver extends Thread {
		Socket socket;
		DataInputStream in;

		/**
		 * @param socket
		 * @param in
		 */
		private ClientReceiver(Socket socket, DataInputStream in) {
			this.socket = socket;
			this.in = in;
		}

		/* (non-Javadoc)
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
	 * @author emanuela
	 *
	 */
	private class ClientSender extends Thread {
		Socket socket;
		DataOutputStream out;
		Scanner scanner;

		/**
		 * @param socket
		 * @param out
		 * @param scanner
		 */
		private ClientSender(Socket socket, DataOutputStream out, Scanner scanner) {
			this.socket = socket;
			this.out = out;
			this.scanner = scanner;
		}

		/* (non-Javadoc)
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