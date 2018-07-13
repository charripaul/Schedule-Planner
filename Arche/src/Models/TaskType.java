package Models;

public class TaskType {
	private int id;
	private String name, description;
	private int warningPeriod;
	
	//initialization constructor
	public TaskType(int i, String n, String d, int wp) {
		id = i;
		name = n;
		description = d;
		warningPeriod = wp;
	}
	//new type constructor
	public TaskType(String n, String d, int wp) {
		name = n;
		description = d;
		warningPeriod = wp;
	}
	public void setId(int i) {
		id = i;
	}
	public void setName(String n) {
		name = n;
	}
	public void setDescription(String d) {
		description = d;
	}
	public void setWarningPeriod(int wp) {
		warningPeriod = wp;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public int getWarningPeriod() {
		return warningPeriod;
	}
}
