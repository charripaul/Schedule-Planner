package Models;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Task {
	private int id;
	private String name, description;
	private boolean finishFlag;
	private boolean onFlag;					//true if appointment at this specific time, false if due by date
	private String type;
	private long dueDate;
	private String classAbr, scheduledWorkTime;
	
	public Task(String n) {
		name = n;			//for tree items on treetableview
	}
	//initialization constructor
	public Task(int i, String n, String d, long dd, boolean ff, boolean of, String t, String ca, String swt) {
		this(n,d,dd,of,t,ca);
		id = i;
		finishFlag = ff;
		scheduledWorkTime = swt;
	}
	//new task constructor
	public Task(String n, String d, long dd, boolean of, String t, String ca) {
		name = n;
		description = d;
		dueDate = dd;
		finishFlag = false;
		onFlag = of;
		type = t;
		classAbr = ca;
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
	public void setDueDate(long d) {
		dueDate = d;
	}
	public void setFinishFlag(boolean ff) {
		finishFlag = ff;
	}
	public void setOnFlag(Boolean of) {
		onFlag = of;
	}
	public void setType(String t) {
		type = t;
	}
	public void setClassAbr(String ca) {
		classAbr = ca;
	}
	public void setScheduledWorkTime(String swt) {
		scheduledWorkTime = swt;
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
	//last resort if cant nest tree items
	public String getInformation() {
		String info = "";
		info += "";
		return info;
	}
	public long getDueDate() {
		return dueDate;
	}
	public ObjectProperty<LocalDateTime> getDueDate(String s) {
		LocalDateTime dd = LocalDateTime.ofInstant(Instant.ofEpochMilli(dueDate), 
                TimeZone.getDefault().toZoneId());
		ObjectProperty<LocalDateTime> ldt = new SimpleObjectProperty<LocalDateTime>();
		ldt.set(dd);
		
		return ldt;
	}
	public boolean getFinishFlag() {
		return finishFlag;
	}
	public boolean getOnFlag() {
		return onFlag;
	}
	public String getType() {
		return type;
	}
	public String getClassAbr() {
		return classAbr;
	}
	public String getScheduledWorkTime() {
		return scheduledWorkTime;
	}
	public String toString() {
		return "id: " + id + "\nname: " + name + "\ntype: "
				+ type + "\nclass: " + classAbr + "\ndue date: "
				+dueDate + "\ndescription: " + description;
	}
}
