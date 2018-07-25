package Models;

public class Admin {
	private int id;
	private String username, password;
	
	//initialization constructor
	public Admin(int i, String u, String p) {
		this(u,p);
		id = i;
	}
	//new admin constructor
	public Admin(String u, String p) {
		username = u;
		password = p;
	}
	public void setId(int i) {
		id = i;
	}
	public void setUsername(String u) {
		username = u;
	}
	public void setPassword(String p) {
		password = p;
	}
	public int getId() {
		return id;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
}
