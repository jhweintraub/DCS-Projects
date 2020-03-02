public class Job {
	private String fileName;
	private Long threadID;
	//Index in the file array that were using
	private int fileIndex;
	
	
	public Job(String fileName, Long threadId) {
		
		this.fileName = fileName;
		this.threadID = threadId;
		this.fileIndex = -1;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Long getThreadID() {
		return threadID;
	}
	public void setThreadId(Long threadId) {
		this.threadID = threadId;
	}
	public int getFileIndex() {
		return fileIndex;
	}
	public void setFileIndex(int fileIndex) {
		this.fileIndex = fileIndex;
	}

	
	
}//Job
