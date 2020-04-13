import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.*;

public class GroupMember {

	public static String COORDINATOR_SERVER_IP;
	public static int COORDINATOR_SERVER_PORT;
	public static boolean isConnected;
	public static long disconnectTime;
	public static long reconnectTime;
	
	

	public static void main(String[] args) throws IOException{
		//TODO --- link to Config File to get info about Host and Port
		
		
		//TODO: Hard Code Those In
		//COORDINATOR_SERVER_IP = args[0];
		//COORDINATOR_SERVER_PORT = Integer.parseInt(args[1]);

		//Specific Socket for sending to the Coordinator
		Socket socket = new Socket(COORDINATOR_SERVER_IP, COORDINATOR_SERVER_PORT);
		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
		
		
		//TODO: New Thread 
		

		// This is what writes to the socket - you use out.print() and whats inside the
		// method is what gets written to the socket
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

		while (true) {
			System.out.print("exec> ");

			// once connected to the server -- initiate the commands
			String command = keyboard.readLine();

			//out.println(command);
			switch(command.split(" ")[0]) {
				case "msend":
					//Send message to coordinator
					//MultiCast socket should be running in background thread
					break;
				case "register":
					//Send Message to Coordinator
					//Wait for Response
					//Start listening thread
					break;
				case "deregister":
					//kill the thread running on the multicast port
					break;
				case "reconnect":
					//re-activate the message receiving/logging - take the thread out of sleep
					//send request for missed messages to coordinator - use this thread
					//receive messages and log them - client side parsing
					break;
				case "disconnect":
					//put the thread to sleep and flip the boolean value
					//Keep track of disconnect time
					break;
			}//switch
			


		}//while
	}//main
}//GroupMember
