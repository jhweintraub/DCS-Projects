import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;

public class ClientHandler implements Runnable {
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;

	private static String currDirectory;
	private static String lastDirectory = null;
	private String homeDir;

	public ClientHandler(Socket clientSocket) throws IOException {
		this.client = clientSocket;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(), true);

		/*//changes format of directory depending on if server is running windows or linux, on a side note why do whe have this
			if(wordParser(System.getProperty("os.name").toCharArray(), 1).contentEquals("Windows")) {
				currDirectory = System.getProperty("user.dir") ;
			} else{
				currDirectory = System.getProperty("user.dir");
			}
		 */

		//Should be as simple as this
		currDirectory = System.getProperty("user.dir");

		//sets the home directory incase you do not enter an argument for CD
		homeDir = System.getProperty("user.dir");
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
					run_ls(true);
					break;
				case "cd":
					run_cd(request);
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

	public String run_ls(boolean print) {
		String s = "";
		//Why do we have to make that folder. This allows it to work no matter where the program is 
		File dir = new File(currDirectory);
		File[] childs = dir.listFiles();

		try{
			for(File child: childs){

				//Changed this to += so now it works, no hidden files in ls
				if(child.getName().charAt(0) != '.')
					s += child.getName() + ' ';
			}
			//added this for my benefit of your code
			if(print)
				out.println(s);
			return s;
		} catch (NullPointerException e) {
			out.println("You Fucked up\n");
			return s;
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
		//have to add a space to the end to get it to split easier
		directory += " ";
		String[] commandWords = directory.split(" ");

		//User wants to go back to home
		if(commandWords.length == 1){
			currDirectory = homeDir;
			//need these outs to make new prompt
			out.println();
		}else if(commandWords[1].equals("..")){
			parentDir();
			out.println();
		}else{
			if(isValidPath(commandWords[1])){
				if(commandWords[1].charAt(0) == '\\'){
					currDirectory = commandWords[1];
				}else{
					currDirectory += commandWords[1];
				}
				out.println();
			}else{
				out.println("cd: " + commandWords[1] + " No such file or directory");
			}
		}
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

	public void parentDir(){
		String[] dirArr = currDirectory.split("\\\\");
		String temp = "";
		//Loop till last item and do not add it
		for(int i = 0; i< dirArr.length -1; i++){
			temp+= dirArr[i] + "\\";
		}

		currDirectory = temp;
	}

	public boolean isValidPath(String path){
		//its an absolute path, check validity
		if(path.charAt(0) == '\\'){
			return new File(path).exists();
		}else{
			//looking for folder inside this folder
			return run_ls(false).contains(path);
		}

	}
}
