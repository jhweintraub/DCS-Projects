import javax.sound.sampled.Port;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int NPORT = 0;
    private static int TPORT = 0;


    //list of clients being connected.
    public static ArrayList<Job> clients = new ArrayList<>();

    /*
    Should we have a class that handles the initial socket connections for nport and tport?
     */
    
    //Fixed size pool of threads - 4
    private static ExecutorService exec_pool = Executors.newFixedThreadPool(4);
    private static ExecutorService term_pool = Executors.newFixedThreadPool(4);
    
    public static void main(String[] args) throws IOException {
    	NPORT = Integer.parseInt(args[0]);
    	TPORT = Integer.parseInt(args[1]);

        ServerSocket listener = new ServerSocket(NPORT);
        ServerSocket listener_term = new ServerSocket(TPORT);
        
//        System.out.println(System.getProperty("os.name"));
        while (true) {
            System.out.println("[SERVER] Waiting for client connection...");
            Socket exec_client = listener.accept(); //TODO - this should be invoked by a thread
            Socket term_client = listener_term.accept(); //TODO - this should be invoked by a thread
            System.out.println("[SERVER] Connected to client.");

            //Create the new thread - Runnable implemented by ClientHandler
            ClientHandler clientThread = new ClientHandler(exec_client);
//          clients.add(clientThread);
            System.out.println(clients.size());

            //execute the Runnable we just created - execute calls the ClientHandler's overrode run() method
            exec_pool.execute(clientThread);
        }
    }//main
    
    //TODO - Delete Thread from Clients
    
    public static int getIndexofThreat(Long x) {
    	for(int y = 0; y < clients.size(); y++) {
    		if (clients.get(y).getThreadID() == x) return y;
    	}
    	return -1;
    }
}
