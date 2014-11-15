package project2;

import java.io.Serializable;

public class DataPackage implements Serializable {

	private static final long serialVersionUID = 1L;
	private String username = "";
	private int state = 0;
	private String message = "";

	public DataPackage() {
		super();
	}
	public DataPackage(String name, int state, String msg) {
		super();
		username = name;
		this.state = state;
		setMessage(msg);
	}

	public String getUsername() {
		return username;
	}

	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}