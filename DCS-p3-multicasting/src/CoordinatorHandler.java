import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
public class CoordinatorHandler implements Runnable{

	//ServerSocket Information
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private DataOutputStream dOut;
	private Message[] messages;
	
	//SendSocket Information
	public int Port;
	public int ID;
	public String IPAddr;
	public boolean isConnected = true;	

	public CoordinatorHandler(Socket clientSocket) throws IOException {
		//ServerSocket Info
		this.client = clientSocket;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(), true);
		this.dOut = new DataOutputStream(client.getOutputStream());
		messages = new Message[100];
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while (true) {
				String request = in.readLine();
				String command = request.split(" ")[0];
				switch (command) {
				case "msend":
					String message = request.substring(request.indexOf(" ")); //Take everything after msend command as msg
					
					message = "------\n"
							+ "Sender ID: " + this.ID + '\n'
							+ "Time: " + new Date().toString() + '\n'
							+ "Message: " + message + '\n'
							+ "------";
					
					//	System.out.println(message);
							
					Coordinator.Send(message);


					//to clear participant blocking call
					out.println("ACK");
					break;
				case "disconnect":
					this.isConnected = false;
					break;
				case "reconnect":
					this.isConnected = true;
					Date now = new Date();
					
					for(int i = 0; i < Coordinator.msgList.size(); i++){
						//See how many seconds 
						long seconds = (now.getTime() - Coordinator.msgList.get(i).getDateTime().getTime())/1000;
//						System.out.println("Time Elapsed: " + seconds);
//						System.out.println("Threshold: " + Coordinator.TIME_THRESHOLD);
						if (seconds < Coordinator.TIME_THRESHOLD ){
							this.send(Coordinator.msgList.get(i).getMessage());
						}
						else continue;
					}

					//Get List of Messages from array
					//Send it back through the socket as one. Will be Parsed on the client side
					break;
				case "deregister":
					out.println(request);
					//Exit the Thread and kill it - this should take it out of the pool
					Coordinator.participants.remove(this);
					Coordinator.participants.trimToSize();
					Thread.currentThread().interrupt();
					System.out.println("Member " + this.ID + " has been deregistered.");
					return;
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
	
	public boolean getisConnected() {
		return isConnected;
	}

	public void send(String message) {
		out.println(message);
	}

	public int getLastIndex(){
		int i;
		for(i = 0; i < messages.length; i++)
			if(messages[i] == null){
			  break;
			}
		return i;
	}

}
