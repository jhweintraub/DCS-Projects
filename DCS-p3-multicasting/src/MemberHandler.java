import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MemberHandler implements Runnable{

	public File logFile;
	public ServerSocket socket;
	private BufferedReader in;
	private PrintWriter out;
	private DataOutputStream dOut;


	//TODO: Connect the MulticastSocket

	public MemberHandler(int Port, String log) {
		try {
			this.logFile = new File(log);
			this.socket = new ServerSocket(Port);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Socket listener = socket.accept();
			in = new BufferedReader(new InputStreamReader(listener.getInputStream()));
			out = new PrintWriter(listener.getOutputStream(), true);
			this.dOut = new DataOutputStream(listener.getOutputStream());
			
			while (true) {
				String message = in.readLine();
				
				//Log Message
				
			}//while()

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//MemberHandler
}