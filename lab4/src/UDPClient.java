import java.net.*;
import java.io.IOException;

public class UDPClient {

    public static void main(String args[]) throws IOException {
        DatagramSocket aSocket = new DatagramSocket();
        byte [] m = new String("Testmessage").getBytes();
        InetAddress aHost = InetAddress.getByName("localhost");
        DatagramPacket request = new DatagramPacket(m, m.length, aHost, 6789);
        aSocket.send(request);
        byte[] buffer = new byte[1000];
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
        aSocket.receive(reply);
        System.out.println("Got message back: \"" + new String(reply.getData(), 0, reply.getLength()) + "\"");
        aSocket.close();
    }

}