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
	
	public TerminateHandler(Socket clientSocket) throws IOException {
		this.client = clientSocket;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(), true);
		this.dOut = new DataOutputStream(client.getOutputStream());
		this.threadId = null;

	}
	
	@Override
	public void run() {
		try{
			while(true){
				System.out.println("Waiting for terminate command.");
				String input = in.readLine();
				threadId = Long.parseLong(input);

				out.println("Terminating Process: " + threadId);

				Job termJob = null;
				for(Job job : Server.jobs){
					if(job.getThreadID() == threadId) termJob = job;
				}//for

				termJob.getSem().release();

				while(true){
					try{
						Thread.sleep((long) 15000);
					}catch (InterruptedException e){
						//do nothing
					}
				}//while

			}//while
		}catch (IOException e){
			//do nothing
		}
	}//run

}//TerminateHandler
