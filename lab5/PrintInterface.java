import java.rmi.Remote;

public interface PrintInterface extends Remote {
	
	public void print(String s) throws Exception;

}
