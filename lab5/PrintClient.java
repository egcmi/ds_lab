import java.rmi.*;

public class PrintClient {
	
	public static void main (String[] args) {
		PrintInterface printer;
		try {
			System.setSecurityManager(new SecurityManager());
			printer = (PrintInterface)Naming.lookup("rmi://10.7.162.179/myPrinter");
			int i = 0;
			while(true){
				printer.print("Hello" + i);
				i++;
			}
			//System.out.println("Client finished printing something on the server side");
		}catch (Exception e) {
			System.out.println("HelloClient exception: " + e);
		}
	}
}