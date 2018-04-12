import java.net.*;
import java.io.IOException;

public class UDPServer {

    public static void main(String args[]) throws IOException {
        DatagramSocket aSocket = new DatagramSocket(6789);
        byte[] buffer = new byte[1000];
        for (int i=0; i<99999; i++) {
            DatagramPacket request =  new DatagramPacket(buffer, buffer.length);
            aSocket.receive(request);
            System.out.print("got " + request.getLength() + " bytes from " + request.getAddress().getHostName() + "/" + request.getAddress().getHostAddress() + ", ");   
            DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
            aSocket.send(reply);
            System.out.println("sent " + reply.getLength() + " bytes back");
        }
        aSocket.close();
    }
}