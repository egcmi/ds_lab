import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
     
public class AdditionServer {
	
    public static void main (String[] args) throws RemoteException, MalformedURLException, AlreadyBoundException {
    		System.setSecurityManager(new SecurityManager());
    		// Alternatively: start rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false
    		LocateRegistry.createRegistry(1099);
    		Adder a = new Adder();	
    		Naming.bind("rmi://localhost:1099/ourAdder", a);
    		System.out.println("Addition Server is ready.");
   	}
}