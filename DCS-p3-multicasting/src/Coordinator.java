import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Coordinator {
	private static int PORT = 0;
	public static ServerSocket listener;
	public static long TIME_THRESHOLD = 0;
	
	//list of clients being connected.
	public static ArrayList<CoordinatorHandler> participants = new ArrayList<>();
	
	//Fixed size pool of threads - 4
	public static ExecutorService pool = Executors.newFixedThreadPool(15);
	public static ArrayList<Message> msgList = new ArrayList<Message>() ;
	public static void main(String[] args) throws IOException {
		
		//Parse Config File Info
		File myObj = new File(args[0]);
		ArrayList<String> configInfo = new ArrayList<>();
		Scanner myReader = new Scanner(myObj);
		while (myReader.hasNextLine()) configInfo.add(myReader.nextLine());
		myReader.close();
		PORT = Integer.parseInt(configInfo.get(0));
		TIME_THRESHOLD = Integer.parseInt(configInfo.get(1));

		
		listener = new ServerSocket(PORT);
		System.out.println(System.getProperty("os.name"));

		while (true) {
			//TODO: Open Config File and read in information
			
			System.out.println("[SERVER] Waiting for client registration...");
			Socket member = listener.accept();

			BufferedReader in = new BufferedReader(new InputStreamReader(member.getInputStream()));
			String input = in.readLine();
			if(input.contains("register")){
				CoordinatorHandler memberThread = new CoordinatorHandler(member);

				memberThread.Port = Integer.parseInt(input.split(" ")[1]);
				memberThread.ID = Integer.parseInt(input.split(" ")[2]);
				memberThread.IPAddr = input.split(" ")[3];
				System.out.println("IP ADDRESS: " + input.split(" ")[3]);

				participants.add(memberThread);

				//execute the Runnable we just created - execute calls the ClientHandler's overrode run() method
				pool.execute(memberThread);
				System.out.println("Member " + memberThread.ID + " registered.");

			}
			System.out.println(participants.size());
		}
	}
	
	public static void Send(String message) {
		Message msg = new Message(message);
		msgList.add(msg);
		for (CoordinatorHandler x : participants) {
			//System.out.println(x.getisConnected());
			if (x.getisConnected()) {
				x.send(message);
			}//if
		}//for
	}//Send

}//Coordinator
