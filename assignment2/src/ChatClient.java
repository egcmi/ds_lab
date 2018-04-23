import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

public class ChatClient {
	
	// some instance variables from the tutorial
	// still have to figure out whether we actually need them lol
	
	Socket clientSocket;
	DataInputStream inputStream;
	PrintStream outputStream;

	public static void main(String args[]) throws IOException {
		
		// ask client to enter username, host address and port ???
		// if successful start connection (open socket)
		// else print error message and quit
		// once connected loop for user input
		// when user enter messages send it to server
		// disconnect when userInput.equals("/quit");
		// ask for confirmation do you want to disconnect? yes/no
		// close socket
		
		int serverPort = 6789;
		Socket s = new Socket(InetAddress.getLocalHost(), serverPort);
		DataInputStream in = new DataInputStream(s.getInputStream());
		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		for (int i = 0; i < 5; i++) {
			out.writeUTF("Teststring"); // UTF is a string encoding
			String data = in.readUTF(); // a blocking call
			System.out.println("Got back: \"" + data + "\"");
		}
		s.close();
	}
}
