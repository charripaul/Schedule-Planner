package Models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import jfxtras.icalendarfx.properties.component.descriptive.Status;

public class Task {
	private int id;
	private String name, description;
	private boolean finishFlag;
	private boolean onFlag;					//true if appointment at this specific time, false if due by date
	private String type;
	private long dueDate, scheduledStartTime, scheduledEndTime;
	private String classAbr;
	private int noticePeriod, timeToComplete;

	public Task(String n) {
		name = n;			//for tree items on treetableview
	}
	//initialization constructor
	public Task(int i, String n, String d, long dd, boolean ff, boolean of, String t,
			String ca, int np, int ttc, long sst, long set) {
		this(n,d,dd,of,t,ca,np,ttc);
		id = i;
		finishFlag = ff;
		scheduledStartTime = sst;
		scheduledEndTime = set;
	}
	//new task constructor
	public Task(String n, String d, long dd, boolean of, String t, String ca, int np, int ttc) {
		name = n;
		description = d;
		dueDate = dd;
		finishFlag = false;
		onFlag = of;
		type = t;
		classAbr = ca;
		noticePeriod = np;
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
	public void setScheduledStartTime(long sst) {
		scheduledStartTime = sst;
	}
	public void setScheduledEndTime(long set) {
		scheduledEndTime = set;
	}
	public void setNoticePeriod(int np) {
		noticePeriod = np;
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
	public long getScheduledStartTime() {
		return scheduledStartTime;
	}
	public ObjectProperty<LocalDateTime> getScheduledStartTime(String s) {
		LocalDateTime dd = LocalDateTime.ofInstant(Instant.ofEpochMilli(scheduledStartTime), 
                TimeZone.getDefault().toZoneId());
		ObjectProperty<LocalDateTime> ldt = new SimpleObjectProperty<LocalDateTime>();
		ldt.set(dd);
		
		return ldt;
	}
	public long getScheduledEndTime() {
		return scheduledEndTime;
	}
	public ObjectProperty<LocalDateTime> getScheduledEndTime(String s) {
		LocalDateTime dd = LocalDateTime.ofInstant(Instant.ofEpochMilli(scheduledEndTime), 
                TimeZone.getDefault().toZoneId());
		ObjectProperty<LocalDateTime> ldt = new SimpleObjectProperty<LocalDateTime>();
		ldt.set(dd);
		
		return ldt;
	}
	public int getNoticePeriod() {
		return noticePeriod;
	}
	public int getTimeToComplete() {
		return timeToComplete;
	}
	public boolean isScheduled() {
		if(scheduledStartTime == 0L || scheduledEndTime == 0L) {
			return false;
		}
		else {
			return true;
		}
	}
	public String toString() {
		return "id: " + id + "\nname: " + name + "\ntype: "
				+ type + "\nclass: " + classAbr + "\ndue date: "
				+dueDate + "\ndescription: " + description;
	}
	private Status.StatusType getStatus() {
		if(finishFlag == false) {
			return Status.StatusType.NEEDS_ACTION;
		}
		else {
			return Status.StatusType.COMPLETED;
		}
	}
}
