package client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.util.Arrays;

import server.ServerCommInterface;

public class Client {

	public static void main(String[] args) throws Exception {
		
		System.out.println("Client starting...");
		
		// Initially we have no problem :)
				byte[] problemHash = null;
				
				// Lookup the server
				// Note: Insert the IP-address or the domain name of the host 
				// where your server is running
				
				//ServerCommInterface sci = (ServerCommInterface)Naming.lookup("rmi://actarus.inf.unibz.it/server");
				ServerCommInterface sci = (ServerCommInterface)Naming.lookup("server");
				
				
				// Create a communication handler and register it with the server
				// The communication handler is the object that will receive the tasks from the server
				ClientCommHandler cch = new ClientCommHandler();
				System.out.println("Client registers with the server");

				// Note: This is a dull client written for testing purposes
				// This is an example of what a registration can look like
				sci.register("Team1", "1", cch);
				
				MessageDigest md = MessageDigest.getInstance("MD5");
				
				// Now forever solve tasks given by the server
				while (true) {
					// Wait until there is a problem from the server
				    // that is different from the one the client has worked on
				        while (cch.currProblem==null || Arrays.equals(cch.currProblem, problemHash)) {Thread.sleep(5);}
		                        // Copy the currentProblem to be able to detect a change later on
					problemHash = cch.currProblem.clone();  
					// Then bruteforce try all integers till problemsize
					for (Integer i=0; i<=cch.currProblemSize; i++) {
						// Calculate their hash
						byte[] currentHash = md.digest(i.toString().getBytes());
						// If the calculated hash equals the one given by the server, submit the integer as solution
						if (Arrays.equals(currentHash, problemHash)) {
							System.out.println("Client submits solution: " + i);  
							sci.submitSolution("TheCoolTeam", "Cool1234", i.toString());
							break;
						}
					}
				}

	}

}
