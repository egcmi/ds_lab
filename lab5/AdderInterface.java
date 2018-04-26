
import java.rmi.Remote;

public interface AdderInterface extends Remote {
	
	public int add(int a,int b) throws Exception;

}