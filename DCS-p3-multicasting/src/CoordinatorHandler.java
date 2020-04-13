import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CoordinatorHandler implements Runnable{

	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private DataOutputStream dOut;
	private Message[] messages;
	
	//TODO: Connect the MulticastSocket

	public CoordinatorHandler(Socket clientSocket) throws IOException {
		this.client = clientSocket;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(), true);
		this.dOut = new DataOutputStream(client.getOutputStream());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while (true) {
				String request = in.readLine();
				switch (request.split(" ")[0]) {
				case "msend":
					//MulticastSocket Send
					break;
				case "reconnect":
					//Get List of Messages from array
					//Send it back through the socket as one. Will be Parsed on the client side
					
					break;
				}// switch
			} // while
		} catch (IOException exception) {
			
			
			
		} finally {
			out.close();
			try {
				in.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} // finally

	}

}
