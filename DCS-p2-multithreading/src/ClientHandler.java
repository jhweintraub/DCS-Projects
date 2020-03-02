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
	
	private int fileIndex = -1;

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
		System.out.println("Starting Get");
		//determine the file
		String fileName = currDirectory + '/' + directory;
		File file = new File(fileName);


		//Add to the Job List
		addJob(fileName, Thread.currentThread().getId() );

		byte[] fileContent;
		
		
		
		try {
			//convert it to a byte array
			Server.files.getFiles().get(fileIndex).getLock().acquire();
			Server.files.getFiles().get(fileIndex).incrementRead();
			
			System.out.println("Got Lock");
			if(Server.files.getFiles().get(fileIndex).getReadcnt() == 1) {
				Server.files.getFiles().get(fileIndex).getWrt().acquire();
				System.out.println("got write lock");
			}
			
			
			fileContent = Files.readAllBytes(file.toPath());
			dOut.writeInt(fileContent.length); //first write the length of the file
			dOut.write(fileContent);//write the file contents itself
			
			//TODO: This needs to be completed, update the byte count somehow
			int writeByteCount = 0;
			boolean terminate = true; //this obviously is a dummy variable, read this somewhere else
			if(writeByteCount % 1000 == 0) {
				//check for terminate command
				if(terminate) {
					//were done reading, still have the lock so we can safely make this call
					Server.files.getFiles().get(fileIndex).decrementRead();
					if(Server.files.getFiles().get(fileIndex).getReadcnt() == 0) {
						//make sure we can drop this lock to allow others to write the file
						Server.files.getFiles().get(fileIndex).getWrt().release();
					}
					
					
					Server.files.getFiles().get(fileIndex).getLock().release();
					
					//TODO: Kill thread
					return;
				}
			}
			
			
			Server.files.getFiles().get(fileIndex).getLock().release();
			//THIS IS WHERE MULTIPLE READS COULD JUMP IN 
			
			Server.files.getFiles().get(fileIndex).getLock().acquire();
			Server.files.getFiles().get(fileIndex).decrementRead();
			if(Server.files.getFiles().get(fileIndex).getReadcnt() == 0) {
				Server.files.getFiles().get(fileIndex).getWrt().release();
			}
			
			Server.files.getFiles().get(fileIndex).getLock().release();
			
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//catch
 catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("TRANSFER COMPLETE FROM: " + Thread.currentThread().getId());
	}//run_get()



	public void run_put(String directory) {
		try {
			
			
			DataInputStream dIn = new DataInputStream(client.getInputStream());

			Process proc = Runtime.getRuntime().exec("touch " + (currDirectory + '/' + directory));//create the file
			String fileName = currDirectory + '/' + directory;
			//Add to the Job List
			
			addJob(fileName, Thread.currentThread().getId() );
			
			Server.files.getFiles().get(fileIndex).getWrt().acquire();

			OutputStream os = new FileOutputStream(currDirectory + '/' + directory);
			byte[] message = null;


			//read in the size of the byte array
			//	    	int length = dIn.readInt();                    // read length of incoming message
			//	    	if(length>0) {
			//	    	    message = new byte[length];
			//	    	    dIn.readFully(message, 0, message.length); // read the message
			//	    	}//if
			//	    	
			System.out.println("Transfer Starting in: " + Thread.currentThread().getId());
			message = new byte[8000];
			int count;
			while ((count = dIn.read(message)) > 0)
			{
				os.write(message, 0, count);
			}
			dIn.readFully(message, 0, message.length); // read the message

			//	    	os.write(message);//write the byte array to the file
			os.close();
			System.out.println("Transfer Ended in: " + Thread.currentThread().getId());
			Server.files.getFiles().get(fileIndex).getWrt().release();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}


	}

	public void run_delete(String file) {	
		String fileName = currDirectory + '/' + file;

		try {
			addJob(fileName, Thread.currentThread().getId() );
			Server.files.getFiles().get(fileIndex).getWrt().acquire();
			
			Process proc = Runtime.getRuntime().exec("rm " + currDirectory + '/' + file);
			out.println();
			
			Server.files.getFiles().get(fileIndex).getWrt().release();

		} catch (IOException | InterruptedException e) {
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

	public void addJob(String fileName, Long tid)  {
		try {
			Job temp = new Job(fileName, tid);


			//Get lock
			Server.files.getLock().acquire();
			Server.files.incrementRead();

			if(Server.files.getReadcnt() == 1) {
				Server.files.getWrt();
			}

			Server.files.getLock().release();
			boolean addFile = true;
			for(int i = 0; i < Server.files.getFiles().size(); i++) {
				//See if the file is already in use
				if(Server.files.getFiles().get(i).getPath().equals(fileName)) {
					addFile = false;

					System.out.println("I am Reading: " + Thread.currentThread().getId());
					temp.setFileIndex(i);
					
					//this is this class's copy: just makes it easier
					fileIndex = i;
					break;
					//TODO: CHECK IF THERES A WRITE GOING ON THE FILE NOW
					/*
					Server.jobsLock.acquire();

					//This is how we link it
					
					Server.jobs.add(temp);
					Server.jobsLock.release();
					Server.files.getLock().release();
					return;
					 */
				}
			}

			Server.files.getLock().acquire();
			Server.files.decrementRead();
			if(Server.files.getReadcnt() == 0) {
				Server.files.getWrt().release();
			}

			Server.files.getLock().release();
			System.out.println("Lock Released From: " + Thread.currentThread().getId());

			if(addFile) {
				//WRITE TO THE FILE THING NOW
				Server.files.getWrt().acquire();

				int index = Server.files.getFiles().size();
				fileIndex = index;
				Server.files.getFiles().add(new FileLock(fileName));

				//TODO: HANDLE THE JOB STUFF

				Server.files.getWrt().release();

			}
			
			Server.jobsLock.acquire();
			Server.jobs.add(temp);
			Server.jobsLock.release();



		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
