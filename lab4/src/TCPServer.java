import java.net.*;
import java.io.*;


public class TCPServer {
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
		    System.out.println("Received and echoed back \"" + data + "\" from " + clientSocket.getRemoteSocketAddress());
		    out.writeUTF(data);
		} catch (Exception e) {e.printStackTrace();}
	}
}