import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// one thread for each connection
// loop to listen for messages from client
// when client sends message -> send it to all clients like so:
// username: message
// stop when client types "\quit"
// tell all clients <username> left the chatroom
// close socket

public class ChatServer {

    public static void main (String args[]) { 
	try{
	    int serverPort = 6789; 
	    ServerSocket listenSocket = new ServerSocket(serverPort); 
	    while(true) {
	    	Socket clientSocket = listenSocket.accept(); 
	    	Connection c = new Connection(clientSocket);
	    }
	} catch(IOException e) {System.out.println("Listen: " + e.getMessage());}
    }
} 

class Connection extends Thread { 

    DataInputStream in; 
    DataOutputStream out; 
    Socket clientSocket;
    
    public Connection (Socket aClientSocket) throws IOException { 
	    clientSocket = aClientSocket;
	    in = new DataInputStream(clientSocket.getInputStream()); 
	    out = new DataOutputStream(clientSocket.getOutputStream()); 
	    this.start();
    } 

    public void run(){
	    String data;
		try {
			data = in.readUTF();
		    System.out.println("Received and echoed back \"" + data + "\"");
		    out.writeUTF(data);
		} catch (Exception e) {e.printStackTrace();}
	}

}
