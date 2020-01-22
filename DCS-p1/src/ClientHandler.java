import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket clientSocket)throws IOException{
        this.client = clientSocket;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream());
    }


    @Override
    public void run() {
        try {
            while(true) {
                String request = in.readLine();
                out.println(request);
                System.out.println("this is a test");
                for (int i = 0; i <= DateServer.options.length; i++) {
                    if(i == DateServer.options.length){
                        out.println("This command is not valid");
                    }
                    else if (request.equals(DateServer.options[i])) {
                        out.println("This command is valid.");
                    }

                }
            }
        } catch (IOException e) {
            System.out.println("IO Exception in client handler.");
            e.printStackTrace();
        } finally {
            out.close();
            try {
                in.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
