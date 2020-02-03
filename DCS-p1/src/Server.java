import javax.sound.sampled.Port;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int PORT = 0;

    //list of clients being connected.
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    
    //Fixed size pool of threads - 4
    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws IOException {
    	PORT = Integer.parseInt(args[0]);
        ServerSocket listener = new ServerSocket(PORT);
        System.out.println(System.getProperty("os.name"));
        while (true) {
            System.out.println("[SERVER] Waiting for client connection...");
            Socket client = listener.accept();
            System.out.println("[SERVER] Connected to client.");

            //Create the new thread - Runnable implemented by ClientHandler
            ClientHandler clientThread = new ClientHandler(client);
            clients.add(clientThread);
            System.out.println(clients.size());

            //execute the Runnable we just created - execute calls the ClientHandler's overrode run() method
            pool.execute(clientThread);
            
            //Print the number of connected clients - when the socket is closed it gets removed from the ArrayList

        }
    }
}
