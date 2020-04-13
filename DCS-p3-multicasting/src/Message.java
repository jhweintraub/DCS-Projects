import java.time.*;

public class Message {
	
	private String message;
	private Long time;
	
	public Message(String message, Long time) {
		this.message = message;
		this.time = time;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Long getTime() {
		return time;
	}

}
