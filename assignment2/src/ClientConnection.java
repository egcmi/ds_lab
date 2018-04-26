import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientConnection {
	private Socket socket;
	private String username;
	private PrintWriter out;
	private BufferedReader in;
	private ClientReceiver receiver;
	private ClientSender sender;

	public ClientConnection(String username, String host, int port) {
		this.username = username;

		try {
			socket = new Socket(InetAddress.getByName(host), port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			System.out.printf("Connection established with host %s at port %d.\n", socket.getInetAddress(),
					socket.getPort());
			out.println(username + " joined the chat.");

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
		sender = new ClientSender(socket, out);
		receiver = new ClientReceiver(socket, in);
		receiver.start();
		sender.start();
		System.out.println("started");
	}

	private class ClientReceiver extends Thread {
		Socket socket;
		BufferedReader in;

		private ClientReceiver(Socket socket, BufferedReader in) {
			this.socket = socket;
			this.in = in;
		}

		public void run() {
			String input;
			try {
				while (!socket.isClosed() && (input = in.readLine()) != null) {
					System.out.println(input);
				}
				return;
			} catch (IOException e) {
				System.err.println("Connection error. Terminating.");
				e.printStackTrace();
				System.exit(1);
			}

		}

	}
	// public static void main(String args[]) {

	// int serverPort = 6789;
	// Socket s = new Socket(InetAddress.getLocalHost(), serverPort);
	// DataInputStream in = new DataInputStream(s.getInputStream());
	// DataOutputStream out = new DataOutputStream(s.getOutputStream());
	// for (int i = 0; i < 5; i++) {
	// out.writeUTF("Teststring"); // UTF is a string encoding
	// String data = in.readUTF(); // a blocking call
	// System.out.println("Got back: \"" + data + "\"");
	// }
	// s.close();
	private class ClientSender extends Thread {
		Socket socket;
		PrintWriter out;

		private ClientSender(Socket socket, PrintWriter out) {
			this.socket = socket;
			this.out = out;
		}

		public void run() {
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			String input;
			try {
				while (!socket.isClosed() && stdin.ready()) {
					input = stdin.readLine();
					if (input.equals("/quit")) {
						out.println(username + " left the chat.");
						stdin.close();
						socket.close();
					} else {
						out.println(username + ": " + input);
					}
				}
				return;
			} catch (IOException e) {
				System.err.println("Connection error. Terminating.");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
