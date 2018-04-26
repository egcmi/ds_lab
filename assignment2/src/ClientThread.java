import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientThread extends Thread {
	Socket socket;
	String username;
	DataOutputStream out;
	boolean connection;
	ReadFromServer in;

	public ClientThread(String username, String host, int port) {
		connection = false;
		this.username = username;

		try {
			socket = new Socket(InetAddress.getByName(host), port);
			in = new ReadFromServer(new DataInputStream(socket.getInputStream()));
			out = new DataOutputStream(socket.getOutputStream());
			connection = socket != null && in != null && out != null;
			if (connection) {
				System.out.println("Connection established with " + socket.getInetAddress());
				out.writeUTF(username + " joined the chat.");
			} else {
				System.out.printf("Could not connect to %s:%d\nTerminating", host, port);
				System.exit(1);
			}

		} catch (UnknownHostException e) {
			connection = false;
			System.err.println("Unknown host" + host + "\nTerminating");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			connection = false;
			System.err.println("Connection error\nTerminating");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void run() {
		try {
			while (connection) {
				Scanner scanner = new Scanner(System.in);
				String buffer = scanner.nextLine();
				if (buffer.equals("/quit")) {
					out.writeUTF(username + " left the chat."); 
					connection = false;
					scanner.close();
				} else {
					out.writeUTF(username + ": " + buffer);
				}
			}

			out.close();
			socket.close();
			join();
		} catch (IOException e) {
			connection = false;
			System.err.println("Connection error\nTerminating");
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class ReadFromServer extends Thread {
		DataInputStream in;

		private ReadFromServer(DataInputStream in) {
			this.in = in;
		}

		public void run() {
			String buffer;
			try {
				while (true) {
					buffer = in.readUTF();
					if (buffer != null)
						System.out.println(buffer);
				}
			} catch (IOException e) {
				System.err.println("Connection error\nTerminating");
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
}
