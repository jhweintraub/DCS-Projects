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
	private static Long threadID;
	private static ClientHandler toTerminate;
	
	public TerminateHandler(Socket clientSocket) throws IOException {
		this.client = clientSocket;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(), true);
		this.dOut = new DataOutputStream(client.getOutputStream());
		this.threadID = null;

	}
	
	@Override
	public void run() {
		try{
			while(true){

				String input = in.readLine();
				threadID = Long.parseLong(ClientHandler.wordParser(input.toCharArray(), 2));

				String message = "Terminating Process: " + threadID;
				System.out.println(message);
				out.println(message);

				int  i = 0;
				for(ClientHandler client : Server.clients){
					if(threadID == client.ID){
						System.out.println("Terminating Thread");
						Server.clients.get(i).setTerminate(true);
					}
					i++;
				}
				
				String pathToDelete = toTerminate.filePath;
				out.println(pathToDelete);

			}//while
		}catch (IOException e){
			//do nothing
		}
	}//run

	public Long getThreadID(){ return threadID; }
	public void setClientHandler(ClientHandler c) { toTerminate = c; }

}//TerminateHandler
