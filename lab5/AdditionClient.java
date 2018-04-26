import java.rmi.*;

public class AdditionClient {
	
	public static void main (String[] args) throws Exception {
		AdderInterface obj;
		System.setSecurityManager(new SecurityManager());
		obj = (AdderInterface)Naming.lookup("rmi://10.7.162.179/ourAdder");
		int i = 1, j = 1;
		i = obj.add(i, j); System.out.println("Result is: " + i);
		while (true) {
		i = obj.add(i, j); System.out.println("Result is: " + i);
		j = obj.add(i, j); System.out.println("Result is: " + j);

		}
	}
}