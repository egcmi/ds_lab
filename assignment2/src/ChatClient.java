import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ChatClient {

    public static void main (String args[]) throws IOException { 
	int serverPort = 6789;
    Socket s = new Socket(InetAddress.getLocalHost(), serverPort);
    DataInputStream in = new DataInputStream(s.getInputStream());
    DataOutputStream out = new DataOutputStream(s.getOutputStream());
    for (int i=0; i<5; i++) {
    	out.writeUTF("Teststring"); // UTF is a string encoding 
    	String data = in.readUTF(); // a blocking call
    	System.out.println("Got back: \"" + data + "\"");
    }
	s.close();
    }
}
