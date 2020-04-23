import java.time.*;
import java.util.Date;
public class Message {
	
	private String message;
	private Date time;
	
	public Message(String message) {
		this.message = message;
		this.time   = new Date();
	}
	
	public String getMessage() {
		return message;
	}
	
	public Date getDateTime() {
		return time;
	}

}
