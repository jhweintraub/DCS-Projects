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
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupMember {

	public static String COORDINATOR_SERVER_IP;
	public static int COORDINATOR_SERVER_PORT;

	public static ArrayList<MemberHandler> members = new ArrayList<>();

	public static ExecutorService pool = Executors.newFixedThreadPool(15);


	public static void main(String[] args) throws IOException{
		//TODO --- link to Config File to get info about Host and Port


		//TODO: Hard Code Those In
		//COORDINATOR_SERVER_IP = args[0];
		//COORDINATOR_SERVER_PORT = Integer.parseInt(args[1]);

		//Specific Socket for sending to the Coordinator
		Socket socket = new Socket(COORDINATOR_SERVER_IP, COORDINATOR_SERVER_PORT);
		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));



		//Fixed size pool of threads - 4
		MemberHandler memberThread = new MemberHandler(1234, "test.txt"); //placeholder info - remove later
//		members.add(memberThread);
		//execute the Runnable we just created - execute calls the ClientHandler's overrode run() method
//		pool.execute(memberThread);


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
			case "reconnect":
			case "disconnect":
				out.println(command);
				//Send message to coordinator
				break;
			case "register":
				//assemble datagram
				//send it to the coordinator

				//We don't have the thread execute and start listening UNTIL it's been registered w the coordinator
				members.add(memberThread);
				pool.execute(memberThread);	
				break;
			case "deregister":
				//Send Message to the Coordinator
				//Kill the Thread
				pool.shutdownNow(); //Kill the pool. 
				break;

			}//switch



		}//while
	}//main
}//GroupMember
