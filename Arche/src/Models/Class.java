package Models;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
	public Class(String n, String a, String d, int ta, String dow, LocalTime st, LocalTime et) {
		name = n;
		abbreviation = a;
		details = d;
		totalAssignments = ta;
		daysOfWeek = dow;
		startTime = st;
		endTime = et;
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
	//for observable tables
	public String getDaysOfWeek() {
		String val = "";
		if(daysOfWeek.substring(0,1).equals("1")) {
			val+="Sunday, ";
		}
		if(daysOfWeek.substring(1,2).equals("1")) {
			val+="Monday, ";
		}
		if(daysOfWeek.substring(2,3).equals("1")) {
			val+="Tuesday, ";
		}
		if(daysOfWeek.substring(3,4).equals("1")) {
			val+="Wednesday, ";
		}
		if(daysOfWeek.substring(4,5).equals("1")) {
			val+="Thursday, ";
		}
		if(daysOfWeek.substring(5,6).equals("1")) {
			val+="Friday, ";
		}
		if(daysOfWeek.substring(6,7).equals("1")) {
			val+="Saturday, ";
		}
		return val.substring(0,val.length()-2);
	}
	//for data flow and database
	public String getDaysOfWeek(String s) {
		return daysOfWeek;
	}
	public int getTotalAssignments() {
		return totalAssignments;
	}
	public LocalTime getStartTime() {
		return startTime;
	}
	public String getStartTime(String s) {
		if(s.equalsIgnoreCase("format")) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a");
			return startTime.format(dtf).toString();
		}
		else {
			return startTime.toString();
		}
	}
	public LocalTime getEndTime() {
		return endTime;
	}
	public String getEndTime(String s) {
		if(s.equalsIgnoreCase("format")) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a");
			return endTime.format(dtf).toString();
		}
		else {
			return endTime.toString();
		}
	}
}
