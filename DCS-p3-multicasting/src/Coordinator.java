import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Coordinator {
	private static int PORT = 0;
	
	//TODO: Establish MulticastSocket

	//list of clients being connected.
	private static ArrayList<CoordinatorHandler> members = new ArrayList<>();

	//Fixed size pool of threads - 4
	private static ExecutorService pool = Executors.newFixedThreadPool(4);

	public static void main(String[] args) throws IOException {
		PORT = Integer.parseInt(args[0]);
		ServerSocket listener = new ServerSocket(PORT);
		System.out.println(System.getProperty("os.name"));

		while (true) {
			System.out.println("[SERVER] Waiting for client connection...");
			Socket member = listener.accept();
			System.out.println("[SERVER] Connected to client.");

			//Create the new thread - Runnable implemented by MemberHandler
			//TODO: Pass MulticastSocket to Handler
			CoordinatorHandler memberThread = new CoordinatorHandler(member);
			members.add(memberThread);
			System.out.println(members.size());

			//execute the Runnable we just created - execute calls the ClientHandler's overrode run() method
			pool.execute(memberThread);

			//Print the number of connected clients - when the socket is closed it gets removed from the ArrayList

		}
	}

}
