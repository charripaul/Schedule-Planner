package Models;

public class Class {
	private int id;
	private String name, abbreviation, details, daysOfWeek, timeOfDay;
	private int totalAssignments;
	
	//initialization constructor
	public Class(int i, String n, String a, String d, int ta, String dow, String tod) {
		this(n,a,d,ta,dow,tod);
		id = i;
	}
	//new class constructor
	public Class(String n, String a, String d, int ta, String dow, String tod) {
		name = n;
		abbreviation = a;
		details = d;
		totalAssignments = ta;
		daysOfWeek = dow;
		timeOfDay = tod;
	}
	public void setId(int i) {
		id = i;
	}
	public void setName(String n) {
		name = n;
	}
	public void setAbbreviation(String a) {
		abbreviation = a;
	}
	public void setDetails(String d) {
		details = d;
	}
	public void setDaysOfWeek(String dow) {
		daysOfWeek = dow;
	}
	public void setTotalAssignments(int ta) {
		totalAssignments = ta;
	}
	public void setTimeOfDay(String tod) {
		timeOfDay = tod;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getAbbreviation() {
		return abbreviation;
	}
	public String getDetails() {
		return details;
	}
	public String getDaysOfWeek() {
		return daysOfWeek;
	}
	public int getTotalAssignments() {
		return totalAssignments;
	}
	public String getTimeOfDay() {
		return timeOfDay;
	}
}
