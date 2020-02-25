import javax.swing.*;
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
import java.nio.Buffer;
import java.nio.file.Files;

public class Client {

	public static String SERVER_IP;
	public static int SERVER_PORT;
	public static byte[] message;

	public static void main(String[] args) throws IOException {

		// Create a new client-side socket to connect to the server and the specified
		// port
		SERVER_IP = args[0];
		SERVER_PORT = Integer.parseInt(args[1]);
		
		Socket socket = new Socket(SERVER_IP, SERVER_PORT);

		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

		// This is what writes to the socket - you use out.print() and whats inside the
		// method is what gets written to the socket
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

		while (true) {
			System.out.print("myftp> ");

			// once connected to the server -- initiate the commands
			String command = keyboard.readLine();

			out.println(command);

			if (command.equals("quit"))
				break;

			if (command.equals("ls") || command.equals("cd")) {
				String serverResponse = input.readLine();
				System.out.println(serverResponse);
			} // else

			else if (command.substring(0, 3).equals("get")) {

				// The Input Stream is where you read data incoming from the socket
				DataInputStream dIn = new DataInputStream(socket.getInputStream());
				String filename = wordParser(command.toCharArray(), 2);// determine the new file name

				Process proc = Runtime.getRuntime().exec("touch " + filename);// create the file

				OutputStream os = new FileOutputStream(filename);

				// read in the size of the byte array
				int length = dIn.readInt(); // read length of incoming message
				if (length > 0) {
					message = new byte[length];
					dIn.readFully(message, 0, message.length); // read the message
				} // if
				os.write(message);// write the byte array to the file
				os.close();
			} // if get command

			else if (command.substring(0, 3).equals("put")) {
				String filename = wordParser(command.toCharArray(), 2);// determine the new file name
				DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

				// determine the file
				File file = new File(filename);
				byte[] fileContent;

				try {
					// convert it to a byte array
					fileContent = Files.readAllBytes(file.toPath());
					dOut.writeInt(fileContent.length); // first write the length of the file
					dOut.write(fileContent);// write the file contents itself
				} catch (IOException e1) {
					e1.printStackTrace();
				} // catch
			} // else if

			else {
				String serverResponse = input.readLine();
				System.out.println(serverResponse);
			} // else

		} // while

		socket.close(); // close the socket when you're done with it.
		System.exit(0);
	}//main

	private static String wordParser(char[] c, int wordNumber) {
		String s = "";
		int spaceCount = 0;

		if (wordNumber == 1) {
			for (int i = 0; i < c.length && c[i] != ' '; i++) {
				s = s + c[i];
			}
		} else {
			try {
				for (int i = 0; spaceCount != wordNumber; i++) {
					if (c[i] == ' ')
						spaceCount++;
					else if (spaceCount == wordNumber - 1)
						s = s + c[i];
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				// just continue to let s be output
			}
		}

		return s;
	}//wordParser

}//Client
