import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;

public class ClientHandler implements Runnable {
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private DataOutputStream dOut;

	private static String currDirectory;
	private static String lastDirectory = null;
	private String homeDir;

	public ClientHandler(Socket clientSocket) throws IOException {
		this.client = clientSocket;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(), true);
		this.dOut = new DataOutputStream(client.getOutputStream());


		
		//Should be as simple as this
		currDirectory = System.getProperty("user.dir");

		//sets the home directory in case you do not enter an argument for CD
		homeDir = System.getProperty("user.dir");
	}

	@Override
	public void run() {
		try {
			while (true) {
				String request = in.readLine();
				char [] requestAsCharArr = request.toCharArray();
				
				//Return the Process ID to the User
				out.println(Thread.currentThread().getId());

				
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
					run_get(wordParser(requestAsCharArr, 2));
					break;
				case "put":
					run_put(wordParser(requestAsCharArr, 2));
					break;
				case "mkdir":
					//Sys Class
					run_mkdir(wordParser(requestAsCharArr, 2));
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
			out.println("Error\n");
			return s;
		}
	}//run_ls

	public void run_pwd() {
		out.println(currDirectory);
	}

	public void run_get(String directory) {
	
		//determine the file
		String fileName = currDirectory + '/' + directory;
		File file = new File(fileName);
		
		//Add to the Job List
		Server.clients.add(new Job(fileName, Thread.currentThread().getId()));
		byte[] fileContent;
		
		try {
			//convert it to a byte array
			fileContent = Files.readAllBytes(file.toPath());
			dOut.writeInt(fileContent.length); //first write the length of the file
			dOut.write(fileContent);//write the file contents itself
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//catch
	}//run_get()
	
	

	public void run_put(String directory) {
		try {
			DataInputStream dIn = new DataInputStream(client.getInputStream());
	    	
	    	Process proc = Runtime.getRuntime().exec("touch " + (currDirectory + '/' + directory));//create the file
	    	String fileName = currDirectory + '/' + directory;
	    	//Add to the Job List
			Server.clients.add(new Job(fileName, Thread.currentThread().getId()));

	        OutputStream os = new FileOutputStream(currDirectory + '/' + directory);
	       byte[] message = null;

	    
	    	//read in the size of the byte array
	    	int length = dIn.readInt();                    // read length of incoming message
	    	if(length>0) {
	    	    message = new byte[length];
	    	    dIn.readFully(message, 0, message.length); // read the message
	    	}//if
	    	
	    	os.write(message);//write the byte array to the file
	    	os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

	public void run_delete(String file) {		
		try {
			Process proc = Runtime.getRuntime().exec("rm " + currDirectory + '/' + file);
			out.println();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				}
				
				else if(commandWords[1].equals("..")){
					parentDir();
				out.println();
				} 
				else{
					if(isValidPath(commandWords[1])){
						if(commandWords[1].charAt(0) == '/'){
							currDirectory = commandWords[1];
						}else{
							currDirectory += ('/' + commandWords[1]);
						}
						out.println();
					}else{
						out.println("cd: " + commandWords[1] + " No such file or directory");
					}
				}
	}

	public void run_mkdir(String directory) {
		try {
			Process proc = Runtime.getRuntime().exec("mkdir " + (currDirectory + '/' + directory));
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			out.println("Directory Created");
		}
	}

	//returns a given String from a char array
	private static String wordParser(char [] c, int wordNumber){
		if (c[0] == 'l' && c[1] == 's') return "ls";
		
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
		String[] dirArr = currDirectory.split("/");
		String temp = "";
		//Loop till last item and do not add it
		for(int i = 0; i< dirArr.length -1; i++){
			temp+= dirArr[i] + "/";
		}

		currDirectory = temp;
	}

	public boolean isValidPath(String path){
		//its an absolute path, check validity
		if(path.charAt(0) == '/'){
			return new File(path).exists();
		}else{
			//looking for folder inside this folder
			return run_ls(false).contains(path);
		}

	}
}
