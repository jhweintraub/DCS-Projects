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
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupMember {

	public static String COORDINATOR_SERVER_IP;
	public static int COORDINATOR_SERVER_PORT;
	public static int GROUP_MEMBER_ID;
	public static String LOG_FILE;


	public static ArrayList<MemberHandler> members = new ArrayList<>();

	public static ExecutorService pool = Executors.newFixedThreadPool(15);


	public static void main(String[] args) throws IOException{
		
		//Parse Config File Info
		File myObj = new File(args[0]);
		ArrayList<String> configInfo = new ArrayList<>();
		Scanner myReader = new Scanner(myObj);
		while (myReader.hasNextLine()) configInfo.add(myReader.nextLine());
		myReader.close();
		
		GROUP_MEMBER_ID = Integer.parseInt(configInfo.get(0)); 
		LOG_FILE = configInfo.get(1);
		COORDINATOR_SERVER_IP = configInfo.get(2).split(" ")[0];
		COORDINATOR_SERVER_PORT = Integer.parseInt(configInfo.get(2).split(" ")[1]);

		//Specific Socket for sending to the Coordinator
		Socket socket = new Socket(COORDINATOR_SERVER_IP, COORDINATOR_SERVER_PORT);
		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));


		//Fixed size pool of threads - 4
		MemberHandler memberThread = new MemberHandler(socket, LOG_FILE); //placeholder info - remove later

		//Write through the Socket
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
				out.println(command);

				//We don't have the thread execute and start listening UNTIL it's been registered w the coordinator
				members.add(memberThread);
				pool.execute(memberThread);	
				break;
			case "deregister":
				//TODO: Send Message to the Coordinator
				
				//Kill the Thread
				members.remove(0);
				pool.shutdownNow(); //Kill the pool - we can Shut down the entire pool cause there's only one other thread
				break;
			default:
				System.out.println("Invalid Choice. Please Try Again");
				break;
			}//switch



		}//while
	}//main
}//GroupMember
