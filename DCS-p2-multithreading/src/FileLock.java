import java.util.concurrent.Semaphore;

public class FileLock {

	private Semaphore lock;
	private Semaphore wrt;
	private String path;
	private int readcnt;
	
	
	public FileLock(String path) {
	
		lock = new Semaphore(1);
		wrt = new Semaphore(1);
		readcnt = 0;
		this.path = path;
	}


	public int getReadcnt() {
		return readcnt;
	}


	public void setReadcnt(int readcnt) {
		this.readcnt = readcnt;
	}


	public Semaphore getLock() {
		return lock;
	}


	public void setLock(Semaphore lock) {
		this.lock = lock;
	}


	public Semaphore getWrt() {
		return wrt;
	}


	public void setWrt(Semaphore wrt) {
		this.wrt = wrt;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}
	
	public void incrementRead() {
		readcnt++;
	}
	
	public void decrementRead() {
		readcnt--;
	}
	
	
	
}