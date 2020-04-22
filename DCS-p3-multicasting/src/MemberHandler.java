import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MemberHandler implements Runnable{

	public File logFile;
	public Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private DataOutputStream dOut;
	private DataInputStream dIn;


	//TODO: Connect the MulticastSocket

	public MemberHandler(Socket socket, String log) {
		try {
			this.socket = socket;
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			this.dOut = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}

		logFile = new File(log);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while (true) {
				if (Thread.interrupted()) return;
	
				String message = in.readLine();
				
				if (message == null) continue;
				
				else if (message.equals("ACK")) {
					GroupMember.WAITING_FOR_ACK = false;
					continue;
				}
				
				else {
//					System.out.println(message);
					try{
						FileWriter myWriter = new FileWriter(logFile, true);
						myWriter.write("\n" + message); //Log Message
						myWriter.close();
					}catch(IOException e){
						e.printStackTrace();
					}//write message to logfile
				}//else
				
			}//while()

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//catch
	}//run()
}//MemberHandler()