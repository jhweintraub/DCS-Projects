import java.io.File;
import java.util.concurrent.*; 

public class Job {
	private String fileName;
	private Semaphore sem;
	private Long threadId;
	
	public Job(String file_descript, Long id) {
		try {
			this.fileName = file_descript;
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
	
	public String getFile() {
		return fileName;
	}//getFile
	
	//TODO - Delete Method
	
}//Job
