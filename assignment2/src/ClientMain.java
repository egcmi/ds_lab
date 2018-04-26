import java.util.Scanner;

public class ClientMain {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		// ask user for username, host address and port
		System.out.print("Type your username: ");
		String username = scanner.nextLine();
		System.out.print("Type server address: ");
		String host = scanner.nextLine();
		System.out.print("Type port number: ");
		int port = scanner.nextInt();
		scanner.close();
		
		ClientConnection conn = new ClientConnection(username, host, port);
		conn.startChat();
	}
}