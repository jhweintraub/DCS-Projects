import com.sun.jdi.request.StepRequest;

import javax.sound.sampled.Port;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Server{
    private static int NPORT = 0;
    private static int TPORT = 0;


    //list of clients being connected.
    public static ArrayList<Job> jobs = new ArrayList<>();
    public static Semaphore jobsLock = new Semaphore(1);

    public static ArrayList<ClientHandler> clients = new ArrayList<>();
//    public static ArrayList<File> files = new ArrayList<>(); //TODO - Keep Track of Files

    public static FileList files = new FileList();

    //Fixed size pool of threads - 4
    private static ExecutorService exec_pool = Executors.newFixedThreadPool(15);
    private static ExecutorService term_pool = Executors.newFixedThreadPool(15);


    public static void main(String[] args) throws IOException{
        NPORT = Integer.parseInt(args[0]);
        TPORT = Integer.parseInt(args[1]);

        ServerSocket listener = new ServerSocket(NPORT);
        ServerSocket listener_term = new ServerSocket(TPORT);

//        System.out.println(System.getProperty("os.name"));
        while(true){
            System.out.println("[SERVER] Waiting for client connection...");
            Socket exec_client = listener.accept();
            Socket term_client = listener_term.accept();
            System.out.println("[SERVER] Connected to client.");

            //Create the new thread - Runnable implemented by ClientHandler
            ClientHandler clientThread = new ClientHandler(exec_client);
            TerminateHandler termThread = new TerminateHandler(term_client);

            //pairs term thread and client thread with each other
            termThread.setClientHandler(clientThread);
            clientThread.setTermHandler(termThread);

            clients.add(clientThread);
            System.out.println(clients.size());

            //execute the Runnable we just created - execute calls the ClientHandler's overrode run() method
            exec_pool.execute(clientThread);
            term_pool.execute(termThread);


        }
    }
}
