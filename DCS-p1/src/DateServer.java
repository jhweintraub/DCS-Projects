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

public class DateServer {
    public static final String [] options = {"ls", "print", "mkdir", "cd"};
    private static final int PORT = 9090;

    //list of clients being connected.
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    
    //Fixed size pool of threads - 4
    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);

        while (true) {
            System.out.println("[SERVER] Waiting for client connection...");
            Socket client = listener.accept();
            System.out.println("[SERVER] Connected to client.");
            
            //Create the new thread - Runnable implemented by ClientHandler
            ClientHandler clientThread = new ClientHandler(client);
            clients.add(clientThread);

            //execute the Runnable we just created - execute calls the ClientHandler's overrode run() method
            pool.execute(clientThread);
            
            //Print the number of connected clients - when the socket is closed it gets removed from the ArrayList
            System.out.println(clients.size());
        }
    }
}
