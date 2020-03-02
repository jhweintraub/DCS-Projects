import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;

public class ClientThreadHandler implements Runnable {

	private Socket execSocket;
	private Socket termSocket;
	private BufferedReader exec_input;
	private BufferedReader term_input;
	private PrintWriter out;
	private PrintWriter out_term;
	private String command;
	public static byte[] message;



	BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

	// This is what writes to the socket - you use out.print() and whats inside the
	// method is what gets written to the socket


	/*This Class is where the new thread gets executed when the user appends their command with &. It's basically
	the same as the Client.java but it's in a thread*/

	//	public ClientThreadHandler(Socket execSocket, Socket termSocket, String command) throws IOException {
	public ClientThreadHandler(String hostName, int execPort, int termPort, String command) throws IOException {

		this.execSocket = new Socket(hostName, execPort);
		this.termSocket = new Socket(hostName, termPort);



		this.exec_input = new BufferedReader(new InputStreamReader(execSocket.getInputStream()));
		this.term_input = new BufferedReader(new InputStreamReader(termSocket.getInputStream()));

		this.out = new PrintWriter(execSocket.getOutputStream(), true);
		this.out_term = new PrintWriter(termSocket.getOutputStream(), true);

		this.command = command;
	}

	@Override
	public void run() {
		//TODO - Copy the Code from the Client.Java Class Essentially
		if (command.contains("terminate")) {
			out_term.println(ClientHandler.wordParser(command.toCharArray(), 2));
			try {
				System.out.println(term_input.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}//if

		//if not terminate command - need to write to execute socket
		else {
			try {
				//				System.out.println("\ncommand");
				out.println(command);

				//Show Process ID to the User

				System.out.println("Process ID: " + exec_input.readLine());

				if (command.equals("quit"))
					return;

				if (command.equals("ls") || command.equals("cd")) {
					String serverResponse = exec_input.readLine();
					System.out.println(serverResponse);
				} // else

				else if (command.substring(0, 3).equals("get")) {

					// The Input Stream is where you read data incoming from the socket
					DataInputStream dIn = new DataInputStream(execSocket.getInputStream());
					String filename = Client.wordParser(command.toCharArray(), 2);// determine the new file name

					Process proc = Runtime.getRuntime().exec("touch " + filename);// create the file

					OutputStream os = new FileOutputStream(filename);

					// read in the size of the byte array
					//					long length = dIn.readLong(); // read length of incoming message
					//					if (length > 0) {
					message = new byte[8000];
					int count;
					while ((count = dIn.read(message)) > 0)
					{
						os.write(message, 0, count);
					}
					dIn.readFully(message, 0, message.length); // read the message
					//					} // if
//					os.write(message);// write the byte array to the file
					os.close();
				} // if get command

				else if (command.substring(0, 3).equals("put")) {
					String filename = Client.wordParser(command.toCharArray(), 2);// determine the new file name
					DataOutputStream dOut = new DataOutputStream(execSocket.getOutputStream());

					// determine the file
					File file = new File(filename);
					byte[] fileContent;

					try {
						// convert it to a byte array
						fileContent = Files.readAllBytes(file.toPath());
						dOut.writeInt(fileContent.length); // first write the length of the file
						dOut.write(fileContent);// write the file contents itself
					} catch (IOException e1) {	
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} // catch
				} // else if

				else {
					String serverResponse = exec_input.readLine();
					System.out.println(serverResponse);
				} // else
			}//try
			catch (Exception e) {
				e.printStackTrace();
			}//catch
		}//else if not terminate command
		return;
	}//run()
}
