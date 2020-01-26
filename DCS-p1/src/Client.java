import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;

public class Client {

    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 9090;

    public static void main(String[] args) throws IOException{
    	
    	//Create a new client-side socket to connect to the server and the specified port
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);

        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        while(true) {
        	//once connected to the server -- initiate the commands
            System.out.print("sftp> ");
            String command = keyboard.readLine();

            out.println(command);
            if(command.equals("close")) break;

            String serverResponse = input.readLine();
            JOptionPane.showMessageDialog(null, serverResponse);
        }

        socket.close(); //close the socket when you're done with it. 
        System.exit(0);
    }
}
