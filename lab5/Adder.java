
import java.rmi.*;
import java.rmi.server.*;
     
public class Adder extends UnicastRemoteObject implements AdderInterface {
     
    public Adder () throws RemoteException { }
     
    public int add(int a, int b) throws Exception {
    	int result=a+b;
    	System.out.println(getClientHost().toString() + " asked for the sum of " + a + " and " + b);
    	return result;
    }
}