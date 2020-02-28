import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThreadHandler implements Runnable {
	
	private Socket execSocket;
	private Socket termSocket;
	private BufferedReader exec_input;
	private BufferedReader term_input;
	private PrintWriter out;
	private PrintWriter out_term;
	private String command;


	BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

	// This is what writes to the socket - you use out.print() and whats inside the
	// method is what gets written to the socket
	

	/*This Class is where the new thread gets executed when the user appends their command with &. It's basically
	the same as the Client.java but it's in a thread*/
	
	public ClientThreadHandler(Socket execSocket, Socket termSocket, String command) throws IOException {
		this.execSocket = execSocket;
		this.termSocket = termSocket;
		
		
		this.exec_input = new BufferedReader(new InputStreamReader(execSocket.getInputStream()));
		this.term_input = new BufferedReader(new InputStreamReader(termSocket.getInputStream()));
		
		this.out = new PrintWriter(execSocket.getOutputStream(), true);
		this.out_term = new PrintWriter(termSocket.getOutputStream(), true);
		
		this.command = command;
	}
	
	@Override
	public void run() {
		//TODO - Copy the Code from the Client.Java Class Essentially
	}

}
