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

	private static String currDirectory;
	private static String lastDirectory = null;

	public ClientHandler(Socket clientSocket) throws IOException {
		this.client = clientSocket;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(), true);

		//changes format of directory depending on if server is running windows or linux
		if(wordParser(System.getProperty("os.name").toCharArray(), 1).contentEquals("Windows")) {
			currDirectory = System.getProperty("user.dir") + "\\DCS-p1\\root\\";
		} else{
			currDirectory = System.getProperty("user.dir") + "/DCS-p1/root/";
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				// So idk why but if you use print() instead of printf() it will execute
				// asynchronously
				String request = in.readLine();
				char [] requestAsCharArr = request.toCharArray();

				switch (wordParser(requestAsCharArr, 1)) {
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
					//Sys Class
					break;
				case "delete":
					run_delete(wordParser(requestAsCharArr, 2));
					break;
				case "quit":
					in.close();
					client.close();
					break;
				default:
					out.println("This command is invalid.");
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
		String s = null;
		File dir = new File(System.getProperty("user.dir")+"\\DCS-p1\\root");
		File[] childs = dir.listFiles();
		try{
			for(File child: childs){
				s = child.getName() + ' ';
			}//for
			out.println(s);
		} catch (NullPointerException e) {
			out.println("\n");
		}
	}//run_ls

	public void run_pwd() {
		out.println(currDirectory);
	}

	public void run_get(String directory) {

	}

	public void run_put(String directory) {

	}

	public void run_delete(String file) {
		out.println("in delete");
		File pathName = new File(currDirectory+file);

	}

	public void run_cd(String directory) {

	}

	public void run_mkdir(String directory) {

	}

	//returns a given String from a char array
	private static String wordParser(char [] c, int wordNumber){
		String s = "";
		int spaceCount = 0;

		if(wordNumber == 1){
			for(int i = 0; i < c.length && c[i] != ' '; i++){
				s = s+c[i];
			}
		} else {
			try{
				for(int i = 0; spaceCount != wordNumber; i++){
					if(c[i] == ' ') spaceCount++;
					else if(spaceCount == wordNumber - 1) s = s + c[i];
				}
			} catch(ArrayIndexOutOfBoundsException e){
				//just continue to let s be output
			}
		}

		return s;
	}
}
