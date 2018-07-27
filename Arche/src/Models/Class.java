package Models;

import java.time.LocalTime;

public class Class {
	private int id;
	private String name, abbreviation, details, daysOfWeek;
	private LocalTime startTime, endTime;
	private int totalAssignments;
	
	//initialization constructor
	public Class(int i, String n, String a, String d, int ta, String dow, String st, String et) {
		this(n,a,d,ta,dow,st,et);
		id = i;
	}
	//new class constructor
	public Class(String n, String a, String d, int ta, String dow, String st, String et) {
		name = n;
		abbreviation = a;
		details = d;
		totalAssignments = ta;
		daysOfWeek = dow;
		startTime = LocalTime.parse(st);
		endTime = LocalTime.parse(et);
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
	public void setStartTime(String st) {
		startTime = LocalTime.parse(st);
	}
	public void setStartTime(LocalTime st) {
		startTime = st;
	}
	public void setEndTime(String et) {
		endTime = LocalTime.parse(et);
	}
	public void setEndTime(LocalTime et) {
		endTime = et;
	}
	public void addOneToTA() {
		totalAssignments+=1;
	}
	public void removeOneFromTA() {
		totalAssignments-=1;
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
	public LocalTime getStartTime() {
		return startTime;
	}
	public String getStartTime(String s) {
		return startTime.toString();
	}
	public LocalTime getEndTime() {
		return endTime;
	}
	public String getEndTime(String s) {
		return endTime.toString();
	}
}
