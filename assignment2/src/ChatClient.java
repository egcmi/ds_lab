import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient extends Thread {
	Socket socket;
	String username, buffer;
	DataInputStream in;
	DataOutputStream out;
	boolean connection;
	
	public ChatClient() {
	}
	
	public ChatClient(String username, String host, int port) {
		connection = false;
		this.username = username;

		try {
			socket = new Socket(InetAddress.getByName(host), port);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			connection = true;
			
			new ChatClient().start();
			while(connection) {
				out.writeUTF(username + ": " + buffer);
			}

			in.close();
			out.close();
			socket.close();
		} catch (UnknownHostException e) {
			connection = false;
			System.err.println("Unknown host" + host + "\nTerminating");
			e.printStackTrace();
		} catch (IOException e) {
			connection = false;
			System.err.println("Connection error\nTerminating");
			e.printStackTrace();
		}

		run();
	}

	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		while (connection) {
			buffer = scanner.nextLine();
			try {
				out.writeUTF(buffer);
				if (buffer.equals("/quit")) {
					connection = false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		scanner.close();
	}

	// remove throws clause and handle exceptions internally
	public static void main(String args[]) {

		// if successful start connection (open socket)
		// else print error message and quit
		// once connected loop for user input
		// when user enter messages send it to server
		// disconnect when userInput.equals("/quit");
		// ask for confirmation do you want to disconnect? yes/no
		// close socket
		// if connected print connection successful
		// else print error message couldn't connect

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

}
