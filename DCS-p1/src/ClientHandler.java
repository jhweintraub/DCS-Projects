import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;

public class ClientHandler implements Runnable {
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;

	private static String currDirectory = "/root/";
	private static String lastDirectory = null;

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
					run_get(wordParser(requestAsCharArr, 2));
					break;
				case "put":
					break;
				case "mkdir":
					run_mkdir(wordParser(requestAsCharArr, 2));
					//Sys Class
					break;
				case "delete":
					//Sys Call
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
		File dir = new File(System.getProperty("user.dir"));
		File[] childs = dir.listFiles();
		try{
			for(File child: childs){
				out.println(child.getName());
			}//for
		} catch (NullPointerException e){
			out.printf("\n");
		}
	}//run_ls

	public void run_pwd() {
		out.println(currDirectory);
	}

	public void run_get(String directory) {
		File fileToGet = new File("root/" + directory);
		File filetoDownload = new File(directory);
		String readLine = "";
		
		try {
			Process proc = Runtime.getRuntime().exec("touch " + directory);
		
			BufferedReader br = new BufferedReader(new FileReader(fileToGet));
			BufferedWriter bw = new BufferedWriter(new FileWriter(filetoDownload));
			
			String st; 
			  while ((st = br.readLine()) != null) {
			    out.println(st); 
			    bw.write(st + '\n');
			  } 
			  
			  bw.close();
			  
			return;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		 return; 
	}//run_get

	public void run_put(String directory) {

	}

	public void run_delete(String directory) {
		
	}

	public void run_cd(String directory) {

	}

	public void run_mkdir(String directory) {
		System.out.println(directory + '\n');
		try {
			Process process = Runtime.getRuntime().exec("mkdir " + directory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			for(int i = 0; spaceCount != wordNumber; i++){
				if(c[i] == ' ') spaceCount++;
				else if(spaceCount == wordNumber - 1) s = s + c[i];
			}
		}

		return s;
	}
}
