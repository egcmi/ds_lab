package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientCommHandler extends UnicastRemoteObject implements ClientCommInterface {

	private static final long serialVersionUID = 1L;
	
	protected ClientCommHandler() throws RemoteException {
	}

	public byte[] currProblem = null;
	int currProblemSize = 0;
	
	@Override
	public void publishProblem(byte[] hash, int problemsize) throws Exception {
		if(hash==null) {
			System.out.println("No Problem available");
		}
		else {
			System.out.println("Client recieved a new problem of size "+problemsize);
			currProblem = hash;
			currProblemSize = problemsize;
		}
		
	}

}
