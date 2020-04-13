import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MemberHandler implements Runnable{
	
	public File logFile;
	

	//TODO: Connect the MulticastSocket

	public MemberHandler(Socket clientSocket, File log) {
		this.logFile = log;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
			while (true) {
				//Get message from MulticastSocket
				//Log it to the log File
				
				
			}//while()
		}//run()
	}//MemberHandler
