
import java.rmi.*;
import java.rmi.server.*;
     
public class Printer extends UnicastRemoteObject implements PrintInterface {
     
    public Printer () throws RemoteException { }
     
    public void print(String s) throws Exception {
    	System.out.println(getClientHost().toString() + " asked to print \"" + s + "\"");
    }
}