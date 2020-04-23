import java.io.*;
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
	private static boolean isRegistered = false;
	private static boolean isConnected = false;
	public static boolean WAITING_FOR_ACK = false;

	public static Socket socket;
	public static DataOutputStream dOut;
	public static DataInputStream dIn;
	public static BufferedReader input;
	public static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
	public static PrintWriter out;


	public static ArrayList<MemberHandler> members = new ArrayList<>();



	public static void main(String[] args) throws IOException{
		
		//Parse Config File Info
		File myObj = new File(args[0]);
		ArrayList<String> configInfo = new ArrayList<>();
		Scanner myReader = new Scanner(myObj);
		while (myReader.hasNextLine()) configInfo.add(myReader.nextLine());
		myReader.close();
		
		ExecutorService pool = Executors.newFixedThreadPool(2);

		
		GROUP_MEMBER_ID = Integer.parseInt(configInfo.get(0)); 
		LOG_FILE = configInfo.get(1);
		COORDINATOR_SERVER_IP = configInfo.get(2).split(" ")[0];
		COORDINATOR_SERVER_PORT = Integer.parseInt(configInfo.get(2).split(" ")[1]);

		while (true) {	
			System.out.print("exec> ");

			// once connected to the server -- initiate the commands
			String command = keyboard.readLine();

			//out.println(command);
			switch(command.split(" ")[0]) {
			case "msend":

				if (!isConnected) {
					System.out.println("Error. You Are Not Connected. Please Reconnect and Try Again");
					continue;
				}
				
				else if (!isRegistered) {
					System.out.println("Error. You Are Not Registed. Please Register and Try Again");
					continue;
				}
				
				out.println(command);
				WAITING_FOR_ACK = true;

				//blocking call that waits for coordinator acknowledgment
				while(WAITING_FOR_ACK)
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				break;
			case "reconnect":	
				isConnected = true;
				out.println(command);
				break;
				
			case "disconnect":
				isConnected = false;
				out.println(command);
				//Send message to coordinator
				break;
			case "register":
				
				//Make Sure the Port number is included so the coordinator doesn't get screwed up
				if (command.split(" ").length == 1) {
					System.out.println("Error. You Forgot to specify a port");
					continue;
				}
				
				//Execute Thread B before sending to the coordinator
				socket = new Socket(COORDINATOR_SERVER_IP, COORDINATOR_SERVER_PORT);
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				keyboard = new BufferedReader(new InputStreamReader(System.in));
				out = new PrintWriter(socket.getOutputStream(), true);
				
				MemberHandler memberThread = new MemberHandler(socket, LOG_FILE);
				members.add(memberThread);
				pool.execute(memberThread);	
				
				isRegistered = true;
				isConnected = true;
				out.println(command + " " + GROUP_MEMBER_ID + " " + COORDINATOR_SERVER_IP);
			

				break;
			case "deregister":				
				//Kill the Thread
				members.get(0).Interrupt = true;
				members.remove(0);
				isRegistered = false;
				isConnected = false;
				
				out.println(command);
				break;
			default:
				System.out.println("Invalid Choice. Please Try Again");
				break;
			}//switch



		}//while
	}//main
}//GroupMember
