import java.io.File;
import java.util.concurrent.*; 

public class Job {
	private File file;
	private Semaphore sem;
	private Long threadId;
	
	public Job(String filename, Long id) {
		try {
			this.file = new File(filename);
			this.sem = new Semaphore(1);
			this.threadId = id;
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//Job
	
	public Long getThreadID() {
		return threadId;
	}//getThreadID
	
	public Semaphore getSem() {
		return sem;
	}//getSem
	
	public File getFile() {
		return file;
	}//getFile
	
	//TODO - Delete Method
	
}//Job
