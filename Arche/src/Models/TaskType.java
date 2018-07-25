package Models;

public class TaskType {
	private int id;
	private String name, description;
	private int warningPeriod, timeToComplete;
	
	//initialization constructor
	public TaskType(int i, String n, String d, int wp, int ttc) {
		this(n,d,wp, ttc);
		id = i;
	}
	//new type constructor
	public TaskType(String n, String d, int wp, int ttc) {
		name = n;
		description = d;
		warningPeriod = wp;
		timeToComplete = ttc;
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
	public void setTimeToComplete(int ttc) {
		timeToComplete = ttc;
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
	public int getTimeToComplete() {
		return timeToComplete;
	}
}
