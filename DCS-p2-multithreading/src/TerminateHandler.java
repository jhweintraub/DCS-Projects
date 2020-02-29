import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TerminateHandler implements Runnable {
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private DataOutputStream dOut;
	private Long threadId;
	
	public TerminateHandler(Socket clientSocket, Long threadID) throws IOException {
		this.client = clientSocket;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(), true);
		this.dOut = new DataOutputStream(client.getOutputStream());
		this.threadId = threadID;

	}
	
	@Override
	public void run() {
		try {

			Job term_job = Server.jobs.get(Server.getIndexOfThread(threadId));
			//TODO - Terminate the Thread in the Correct Way

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
