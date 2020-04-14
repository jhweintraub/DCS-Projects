import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CoordinatorHandler implements Runnable{

	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private DataOutputStream dOut;
	private Message[] messages;
	
	//Send Information
	private int Port;
	private int ID;
	private String IPAddr;
	private boolean isConnected = false;
	
	//TODO: Temporal Information about Disconnection Time
	

	public CoordinatorHandler(Socket clientSocket) throws IOException {
		this.client = clientSocket;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(), true);
		this.dOut = new DataOutputStream(client.getOutputStream());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while (true) {
				String request = in.readLine();
				String command = request.split(" ")[0];
				switch (command) {
				case "msend":
					String message = request.substring(request.indexOf(" ")); //Take everything after msend command as msg
					Coordinator.Send(message); //Get the 
					//Log the Message to File
					break;
				case "disconnect":
					this.isConnected = false;
					break;
				case "reconnect":
					this.isConnected = true;
					//Get List of Messages from array
					//Send it back through the socket as one. Will be Parsed on the client side
					break;
				case "register":
					//make new Participant and add to list of Participants in Coordinator
					break;
				case "deregister":
					out.println(request);
					//Exit the Thread and kill it - this should take it out of the pool
					break;
				}// switch
			} // while
		} catch (IOException exception) {
			
			
			
		} finally {
			out.close();
			try {
				in.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} // finally

	}
	
	public boolean getisConnected() {
		return isConnected;
	}
	
	public void send(String message) {
		//Send the message through the socket
		out.println(message);
	}

}
