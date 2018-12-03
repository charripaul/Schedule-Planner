package Models;

public class Project {
	private int id, uid;
	private String name, description, currentStep;
	private int priorityLevel;
	
	//initialization constructor
	public Project(int i, int u, String n, String d, String cs, int pl) {
		this(u,n,d,cs,pl);
		id = i;
	}
	//new project constructor
	public Project(int u, String n, String d, String cs, int pl) {
		uid= u;
		name = n;
		description = d;
		currentStep = cs;
		priorityLevel = pl;
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
	public void setCurrentStep(String cs) {
		currentStep =cs;
	}
	public void setPriorityLevel(int pl) {
		priorityLevel = pl;
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
	public String getCurrentStep() {
		return currentStep;
	}
	public int getPriorityLevel() {
		return priorityLevel;
	}
}
