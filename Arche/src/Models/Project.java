package Models;

public class Project {
	private int id;
	private String name, description, currentStep;
	private int priorityLevel;
	
	//initialization constructor
	public Project(int i, String n, String d, String cs, int pl) {
		id = i;
		name = n;
		description = d;
		currentStep = cs;
		priorityLevel = pl;
	}
	//new project constructor
	public Project(String n, String d, String cs, int pl) {
		name = n;
		description = d;
		currentStep = cs;
		priorityLevel = pl;
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
	public void setCurrentStep(String cs) {
		currentStep =cs;
	}
	public void setPriorityLevel(int pl) {
		priorityLevel = pl;
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
	public String getCurrentStep() {
		return currentStep;
	}
	public int getPriorityLevel() {
		return priorityLevel;
	}
}
