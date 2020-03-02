import java.util.concurrent.Semaphore;

public class FileLock {

	private Semaphore sem;
	private String path;
	
	
	public FileLock(Semaphore sem, String path) {
		super();
		this.sem = sem;
		this.path = path;
	}
	public Semaphore getSem() {
		return sem;
	}
	public void setSem(Semaphore sem) {
		this.sem = sem;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
}