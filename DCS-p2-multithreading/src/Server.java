import javax.sound.sampled.Port;
import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int NPORT = 0;
    private static int TPORT = 0;


    //list of clients being connected.

    public static ArrayList<Job> jobs = new ArrayList<>();
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
//    public static ArrayList<File> files = new ArrayList<>(); //TODO - Keep Track of Files
    
    //Fixed size pool of threads - 4
    private static ExecutorService exec_pool = Executors.newFixedThreadPool(15);
    private static ExecutorService term_pool = Executors.newFixedThreadPool(15);

    public static void main(String[] args) throws IOException {
    	NPORT = Integer.parseInt(args[0]);
    	TPORT = Integer.parseInt(args[1]);

        ServerSocket listener = new ServerSocket(NPORT);
        ServerSocket listener_term = new ServerSocket(TPORT);


        
//        System.out.println(System.getProperty("os.name"));
        while (true) {
            System.out.println("[SERVER] Waiting for client connection...");
            Socket exec_client = listener.accept();
            Socket term_client = listener_term.accept();
            System.out.println("[SERVER] Connected to client.");

            BufferedReader term_in = new BufferedReader(new InputStreamReader(term_client.getInputStream()));
            PrintWriter term_out = new PrintWriter(term_client.getOutputStream());
            TerminateHandler termThread = null;

            if(term_in.ready()){
                System.out.println("here");
                Long commandID = Long.parseLong(term_in.readLine());
                System.out.println(commandID);
                //search through jobs to find matching ThreadID
                for(Job job : jobs){
                    if(job.getThreadID().equals(commandID)){
                        termThread = new TerminateHandler(term_client, commandID);
                        term_out.print("Terminating Process: " + job.getThreadID());
                        break;
                    }
                }//for
            }
            //Create the new thread - Runnable implemented by ClientHandler
            ClientHandler clientThread = new ClientHandler(exec_client);
            clients.add(clientThread);
            System.out.println(clients.size());

            //TODO - Spawn off new terminate thread

            //execute the Runnable we just created - execute calls the ClientHandler's overrode run() method
            exec_pool.execute(clientThread);
            if(termThread != null) term_pool.execute(termThread);

        }//while
    }//main
    
    //TODO - Delete Thread from Clients
    
    public static int getIndexOfThread(Long x) {
    	for(int y = 0; y < clients.size(); y++) {
    		if (jobs.get(y).getThreadID() == x) return y;
    	}
    	return -1;
    }
}
