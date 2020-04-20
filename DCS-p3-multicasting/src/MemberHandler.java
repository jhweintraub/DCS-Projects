import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MemberHandler implements Runnable{

	public File logFile;
	public Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private DataOutputStream dOut;


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


	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while (true) {
				String message = in.readLine();
				System.out.println(message);
//			    FileWriter myWriter = new FileWriter(logFile);
			    //TODO: Determine if the message should be configured and parsed before it gets sent through the socket
//			    myWriter.write(message); //Log Message
				
			    System.out.println("this is also a test");
			    
				
			}//while()

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//MemberHandler
}