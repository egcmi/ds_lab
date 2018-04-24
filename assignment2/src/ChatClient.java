import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient extends Thread {
	Socket socket;
	String username;
	DataInputStream in;
	DataOutputStream out;
	boolean readInput, connection;

	public ChatClient(String username, String host, int port, boolean flag) {
		connection = false;
		this.username = username;

		try {
			socket = new Socket(InetAddress.getByName(host), port);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			connection = true;

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

		run();
	}

	@Override
	public void run() {
		try {
			while (connection) {
				
				if (readInput) {
					Scanner scanner = new Scanner(System.in);
					String buffer = scanner.nextLine();
					out.writeUTF(username + ": " + buffer);
					if (buffer.equals("/quit")) {
						connection = false;
						scanner.close();
					}
				}
				
				else {
					String buffer = in.readUTF();
					if (buffer != null)
						System.out.println(buffer);
				}
			}
			
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
