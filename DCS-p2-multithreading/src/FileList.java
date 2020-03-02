import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class FileList {
	private ArrayList<FileLock> files;
	private Semaphore lock;
	private Semaphore wrt;
	private int readcnt;
	
	public ArrayList<FileLock> getFiles() {
		return files;
	}
	public void setFiles(ArrayList<FileLock> files) {
		this.files = files;
		
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
	public FileList() {
		files = new ArrayList<>();
		lock = new Semaphore(1);
		wrt = new Semaphore(1);
		readcnt = 0;
	}
	public int getReadcnt() {
		return readcnt;
	}
	public void setReadcnt(int readcnt) {
		this.readcnt = readcnt;
	}
	
	public void incrementRead() {
		readcnt++;
	}
	
	public void decrementRead() {
		readcnt--;
	}
	
	
	
}
