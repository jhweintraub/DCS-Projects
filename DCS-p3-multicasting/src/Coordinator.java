import java.io.File;
import java.io.IOException;
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

	public static void main(String[] args) throws IOException {
		
		//Parse Config File Info
		File myObj = new File(args[0]);
		ArrayList<String> configInfo = new ArrayList<>();
		Scanner myReader = new Scanner(myObj);
		while (myReader.hasNextLine()) configInfo.add(myReader.nextLine());
		myReader.close();
		PORT = Integer.parseInt(configInfo.get(0));
		TIME_THRESHOLD = Long.parseLong(configInfo.get(1));

		
		
		listener = new ServerSocket(PORT);
		System.out.println(System.getProperty("os.name"));

		while (true) {
			//TODO: Open Config File and read in information
			
			System.out.println("[SERVER] Waiting for client connection...");
			Socket member = listener.accept();
			System.out.println("[SERVER] Connected to client.");


			//Create the new thread - Runnable implemented by MemberHandler
			//TODO: Pass MulticastSocket to Handler
			CoordinatorHandler memberThread = new CoordinatorHandler(member);
			participants.add(memberThread);
			System.out.println(participants.size());

			//execute the Runnable we just created - execute calls the ClientHandler's overrode run() method
			pool.execute(memberThread);

			//Print the number of connected clients - when the socket is closed it gets removed from the ArrayList

		}
	}
	
	public static void Send(String message) {
		for (CoordinatorHandler x : participants) {
			System.out.println(x.getisConnected());
			if (x.getisConnected()) {
				x.send(message);
			}
		}
	}//Send

}
