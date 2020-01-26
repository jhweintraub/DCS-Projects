import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;

	private static String currDirectory = "/root/";
	private static String lastDirectory;

	public ClientHandler(Socket clientSocket) throws IOException {
		this.client = clientSocket;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(), true);
	}

	@Override
	public void run() {
		try {
			while (true) {
				// So idk why but if you use print() instead of printf() it will execute
				// asynchronously
				out.printf("sftp> ");
				String request = in.readLine();
				switch (request) {
				case "pwd":
					run_pwd();
					break;
				case "ls":
					run_ls();
					break;
				case "cd":
					break;
				case "get":
					break;
				case "put":
					break;
				case "mkdir":
					break;
				case "delete":
					break;
				case "quit":
					in.close();
					client.close();
					break;
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

	public void run_ls() {
		File dir = new File(System.getProperty("user.dir") + "/src" + currDirectory);
		File[] childs = dir.listFiles();
		for(File child: childs){
		    out.println(child.getName());
		}//for
		out.println("..\n.");
	}//run_ls

	public void run_pwd() {
		out.println(currDirectory);
	}

	public void run_get(String directory) {

	}

	public void run_put(String directory) {

	}

	public void run_delete(String directory) {

	}

	public void run_cd(String directory) {

	}

	public void run_mkdir(String directory) {

	}

}
