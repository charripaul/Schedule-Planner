package Models;

public class TaskType {
	private int id, uid;
	private String name, description;
	private int warningPeriod, timeToComplete, totalAssignments;
	
	//initialization constructor
	public TaskType(int i, int u, String n, String d, int wp, int ttc, int ta) {
		this(u,n,d,wp,ttc,ta);
		id = i;
	}
	//new type constructor
	public TaskType(int u, String n, String d, int wp, int ttc, int ta) {
		uid = u;
		name = n;
		description = d;
		warningPeriod = wp;
		timeToComplete = ttc;
		totalAssignments = ta;
	}
	public void setId(int i) {
		id = i;
	}
	public void setUid(int u) {
		uid = u;
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
	//stored in minutes
	public void setTimeToComplete(int ttc) {
		timeToComplete = ttc;
	}
	public void setTotalAssignments(int ta) {
		totalAssignments = ta;
	}
	public int getId() {
		return id;
	}
	public int getUid() {
		return uid;
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
	//stored in minutes
	public int getTimeToComplete() {
		return timeToComplete;
	}
	//for observable table
	public String getTimeToComplete(String s) {
		if(timeToComplete < 60) {
			return timeToComplete + " mins";
		}
		else {
			int hours = timeToComplete/60;
    		int mins = timeToComplete%60;
    		return hours + " Hr(s) " + mins + " min(s)";
		}
	}
	public int getTotalAssignments() {
		totalAssignments = ModelControl.getTaskTypeTACount(name);
		return totalAssignments;
	}
}
