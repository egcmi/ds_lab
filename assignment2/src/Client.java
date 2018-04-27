import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// ask user for username, host address and port
		System.out.print("Type your username: ");
		String username = scanner.nextLine();
		System.out.print("Type server address: ");
		String host = scanner.nextLine();
		System.out.print("Type port number: ");
		int port = scanner.nextInt();
		scanner.nextLine();
		scanner.close();

		ClientHandler conn = new ClientHandler(username, host, port);
		conn.startChat();
	}
}

class ClientHandler {
	Socket socket;
	String username;
	DataOutputStream out;
	DataInputStream in;
	ClientReceiver receiver;
	ClientSender sender;

	public ClientHandler(String username, String host, int port) {
		this.username = username;

		try {
			socket = new Socket(InetAddress.getByName(host), port);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			System.out.printf("Connection established with host %s at port %d.\n", socket.getInetAddress(),
					socket.getPort());
			sender = new ClientSender(socket, out);
			receiver = new ClientReceiver(socket, in);
			out.writeUTF(username + " joined the chat.");

		} catch (UnknownHostException e) {
			System.err.printf("Unknown host %s. Terminating.\n", host);
			System.exit(1);
		} catch (IOException e) {
			System.out.printf("Could not connect to %s:%d. Terminating.\n", host, port);
			System.exit(1);
		}
	}

	public String getUsername() {
		return username;
	}

	public void startChat() {
		receiver.start();
		sender.start();
		System.out.println("started");
	}

	private class ClientReceiver extends Thread {
		Socket socket;
		DataInputStream in;

		private ClientReceiver(Socket socket, DataInputStream in) {
			this.socket = socket;
			this.in = in;
		}

		public void run() {
			System.out.println("started receiver");
			try {
				while (!socket.isClosed()) {
					if (in.available() > 0)
						System.out.println(in.readUTF());
				}
			} catch (IOException e) {
				System.err.println("Connection error. Terminating.");
				e.printStackTrace();
				System.exit(1);
			}

		}

	}

	private class ClientSender extends Thread {
		Socket socket;
		DataOutputStream out;

		private ClientSender(Socket socket, DataOutputStream out) {
			this.socket = socket;
			this.out = out;
		}

		public void run() {
			System.out.println("started sender");
			Scanner stdin = new Scanner(System.in);
			String input;
			System.out.println("wtffff " + !socket.isClosed());
			try {
				while (!socket.isClosed()) {
					System.out.println("client sender entered while loop");

					input = stdin.nextLine();
					System.out.println("input: " + input);
					if (input.equals("/quit")) {
						out.writeUTF(input);
						stdin.close();
						socket.close();
					} else {
						out.writeUTF(username + ": " + input);
					}
				}
				System.out.println("client sender exited while loop");
			} catch (IOException e) {
				System.err.println("Connection error. Terminating.");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}