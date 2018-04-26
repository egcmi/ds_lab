import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
     
public class PrintServer {
	
    public static void main (String[] args) throws RemoteException, MalformedURLException, AlreadyBoundException {
    		System.setSecurityManager(new SecurityManager());
    		// Alternatively: start rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false
    		LocateRegistry.createRegistry(1099);
    		Printer p = new Printer();	
    		Naming.bind("rmi://localhost:1099/myPrinter", p);
    		System.out.println("Print Server is ready.");
   	}
}