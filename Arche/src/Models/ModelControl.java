package Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import Runners.DBConn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jfxtras.icalendarfx.components.VEvent;

public class ModelControl {
	private static ArrayList<Task> tasks = new ArrayList<Task>();
	private static ArrayList<Admin> admins = new ArrayList<Admin>();
	private static ArrayList<Class> classes = new ArrayList<Class>();
	private static ArrayList<Project> projects = new ArrayList<Project>();
	private static ArrayList<TaskType> taskTypes = new ArrayList<TaskType>();
	private static ArrayList<User> users = new ArrayList<User>();
	public static Calendar dayOfReference = Calendar.getInstance();
	public static Calendar today = Calendar.getInstance();
	public static int mainUID;
	
	static {
		initializeAuthentication();
		System.out.println("ModelControl layer initialized");
	}
	private static void initializeAuthentication() {
		ResultSet rs;
		try {
			rs = Database.getAdmins();
			while(rs.next()) {
				admins.add(new Admin(rs.getInt("id"), rs.getString("username"), rs.getString("password")));
			}
		}catch(SQLException e) {
			System.out.println("\nError Code: Pong\n" + e.getMessage());
		}
		try {
			rs = Database.getUsers();
			while(rs.next()) {
				users.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("password")));
			}
		}catch(SQLException e) {
			System.out.println("\nError Code: Ferocity\n" + e.getMessage());
		}
	}
	public static void initialize() {
		ResultSet rs;
		try {
			rs = Database.getClasses();
			while(rs.next()) {
				int uid = rs.getInt("uid");
				if(uid == mainUID || mainUID == -1) {
					classes.add(new Class(rs.getInt("id"), uid, rs.getString("name"), rs.getString("abbreviation"),
							rs.getString("details"), rs.getInt("totalAssignments"), rs.getString("daysOfWeek"),
							rs.getString("startTime"), rs.getString("endTime")));
				}
			}
		}catch(SQLException e) {
			System.out.println("\nError Code: Swab\n" + e.getMessage());
		}
		try {
			rs = Database.getProjects();
			while(rs.next()) {
				int uid = rs.getInt("uid");
				if(uid == mainUID || mainUID == -1) {
					projects.add(new Project(rs.getInt("id"), uid, rs.getString("name"), rs.getString("description"),
							 rs.getString("currentStep"), rs.getInt("priorityLevel")));
				}
			}
		}catch(SQLException e) {
			System.out.println("\nError Code: Puff\n" + e.getMessage());
		}
		try {
			rs = Database.getTaskTypes();
			while(rs.next()) {
				int uid = rs.getInt("uid");
				if(uid == mainUID || mainUID == -1) {
					taskTypes.add(new TaskType(rs.getInt("id"), uid, rs.getString("name"), rs.getString("description"),
							  rs.getInt("warningPeriod"), rs.getInt("timeToComplete"), rs.getInt("totalAssignments")));
				}
			}
		}catch(SQLException e) {
			System.out.println("\nError Code: Center\n" + e.getMessage());
		}
		//has to be last
		try {
			rs = Database.getTasks();
			while(rs.next()) {
				int uid = rs.getInt("uid");
				if(uid == mainUID || mainUID == -1) {
					int classID = rs.getInt("classID");
					int typeID = rs.getInt("typeID");
					tasks.add(new Task(rs.getInt("id"), uid, rs.getString("name"), rs.getString("description"),
							 rs.getLong("dueDate"), rs.getBoolean("finishFlag"), rs.getBoolean("onFlag"),
							 getTypeName(typeID), getClassAbr(classID), rs.getInt("noticePeriod"), 
							 rs.getInt("timeToComplete"), rs.getLong("scheduledStartTime"), rs.getLong("scheduledEndTime")));
				}
			}
		}catch(SQLException e) {
			System.out.println("\nError Code: Solar\n" + e.getMessage());
		}
	}
	//TODO: need to test to see whether identical task already exists excluding description
	public static void addTask(Task t) {
		String classAbr = t.getClassAbr();
		String type = t.getType();
		if(tasks.size() == 0) {
			t.setId(1);
		}
		else {
			t.setId(tasks.get(tasks.size()-1).getId()+1);
		}
		if(!t.isScheduled()) {
			autoSchedule(t);
		}
		
		tasks.add(t);
		Database.addTask(t);
		for(int count = 0;count<classes.size();count++) {
			if(classes.get(count).getAbbreviation().equals(classAbr)) {
				classes.get(count).addOneToTA();
				Database.updateClass(classes.get(count));
			}
		}
		
		for(int count=0;count<taskTypes.size();count++) {
			if(taskTypes.get(count).getName().equals(type)) {
				taskTypes.get(count).addOneToTA();;
				Database.updateTaskType(taskTypes.get(count));
			}
		}
	}
	public static void addAdmin(Admin a) {
		if(admins.size() == 0) {
			a.setId(1);
		}
		else {
			a.setId(admins.get(admins.size()-1).getId()+1);
		}
		admins.add(a);
		Database.addAdmin(a);
	}
	public static void addClass(Class c) {
		if(classes.size() == 0) {
			c.setId(1);
		}
		else {
			c.setId(classes.get(classes.size()-1).getId()+1);
		}
		classes.add(c);
		Database.addClass(c);
	}
	public static void addProject(Project p) {
		if(projects.size() == 0) {
			p.setId(1);
		}
		else {
			p.setId(projects.get(projects.size()-1).getId()+1);
		}
		projects.add(p);
		Database.addProject(p);
	}
	public static void addTaskType(TaskType tt) {
		if(taskTypes.size() == 0) {
			tt.setId(1);
		}
		else {
			tt.setId(taskTypes.get(taskTypes.size()-1).getId()+1);
		}
		taskTypes.add(tt);
		Database.addTaskType(tt);
	}
	public static void addUser(User u) {
		if(users.size() == 0) {
			u.setId(1);
		}
		else {
			u.setId(users.get(users.size()-1).getId()+1);
		}
		users.add(u);
		Database.addUser(u);
	}
	public static void updateTask(Task t) {
		for(int count = 0;count<tasks.size();count++) {
			if(tasks.get(count).getId() == t.getId()) {
				tasks.set(count, t);
				break;
			}
		}
		Database.updateTask(t);
	}
	//update total assignments count
	//if class for task changes
	public static void updateTaskAndClassDependency(Task t, String oldClassAbr, String newClassAbr) {
		updateTask(t);
		for(int count=0;count<classes.size();count++) {
			if(classes.get(count).getAbbreviation().equals(oldClassAbr)) {
				classes.get(count).removeOneFromTA();
			}
			else if(classes.get(count).getAbbreviation().equals(newClassAbr)) {
				classes.get(count).addOneToTA();
			}
		}
	}
	//update total assignments count
	//if type for task changes
	public static void updateTaskAndTypeDependency(Task t, String oldTypeName, String newTypeName) {
		updateTask(t);
		for(int count=0;count<taskTypes.size();count++) {
			if(taskTypes.get(count).getName().equals(oldTypeName)) {
				taskTypes.get(count).removeOneFromTA();
			}
			else if(taskTypes.get(count).getName().equals(newTypeName)) {
				taskTypes.get(count).addOneToTA();
			}
		}
	}
	public static void updateAdmin(Admin a) {
		for(int count = 0;count<admins.size();count++) {
			if(admins.get(count).getId() == a.getId()) {
				admins.set(count, a);
				break;
			}
		}
		Database.updateAdmin(a);
	}
	public static void updateClass(Class c) {
		for(int count = 0;count<classes.size();count++) {
			if(classes.get(count).getId() == c.getId()) {
				classes.set(count, c);
				break;
			}
		}
		Database.updateClass(c);
	}
	//update abbreviations for tasks
	public static void updateClassAndTaskDependency(Models.Class c, String oldClassAbr, String newClassAbr) {
		updateClass(c);
		for(int count=0;count<tasks.size();count++) {
			if(tasks.get(count).getClassAbr().equals(oldClassAbr)) {
				tasks.get(count).setClassAbr(newClassAbr);
			}
		}
	}
	public static void updateProject(Project p) {
		for(int count = 0;count<projects.size();count++) {
			if(projects.get(count).getId() == p.getId()) {
				projects.set(count, p);
				break;
			}
		}
		Database.updateProject(p);
	}
	public static void updateTaskType(TaskType tt) {
		for(int count = 0;count<taskTypes.size();count++) {
			if(taskTypes.get(count).getId() == tt.getId()) {
				taskTypes.set(count, tt);
				break;
			}
		}
		Database.updateTaskType(tt);
	}
	//update task type name for tasks
	public static void updateTaskTypeAndTaskDependency(TaskType tt, String oldTypeName, String newTypeName) {
		updateTaskType(tt);
		for(int count = 0;count<tasks.size();count++) {
			if(tasks.get(count).getType().equals(oldTypeName)) {
				tasks.get(count).setType(newTypeName);
			}
		}
	}
	public static void updateUser(User u) {
		for(int count = 0;count<users.size();count++) {
			if(users.get(count).getId() == u.getId()) {
				users.set(count, u);
				break;
			}
		}
		Database.updateUser(u);
	}
	public static void deleteTask(Task t) {
		String classAbr = t.getClassAbr();
		String type = t.getType();
		
		for(int count = 0;count<tasks.size();count++) {
			if(tasks.get(count).getId() == t.getId()) {
				tasks.remove(count);
				break;
			}
		}
		Database.deleteTask(t);
		//update total assignment count
		for(int count = 0;count<classes.size();count++) {
			if(classes.get(count).getAbbreviation().equals(classAbr)) {
				classes.get(count).removeOneFromTA();
				Database.updateClass(classes.get(count));
			}
		}
		for(int count = 0;count<taskTypes.size();count++) {
			if(taskTypes.get(count).getName().equals(type)) {
				taskTypes.get(count).removeOneFromTA();
				Database.updateTaskType(taskTypes.get(count));
			}
		}
	}
	public static void deleteAdmin(Admin a) {
		for(int count = 0;count<admins.size();count++) {
			if(admins.get(count).getId() == a.getId()) {
				admins.remove(count);
				break;
			}
		}
		Database.deleteAdmin(a);
	}
	public static void deleteClass(Class c) {
		for(int count = 0;count<classes.size();count++) {
			if(classes.get(count).getId() == c.getId()) {
				classes.remove(count);
				break;
			}
		}
		Database.deleteClass(c);
	}
	public static void deleteProject(Project p) {
		for(int count = 0;count<projects.size();count++) {
			if(projects.get(count).getId() == p.getId()) {
				projects.remove(count);
				break;
			}
		}
		Database.deleteProject(p);
	}
	public static void deleteTaskType(TaskType tt) {
		for(int count = 0;count<taskTypes.size();count++) {
			if(taskTypes.get(count).getId() == tt.getId()) {
				taskTypes.remove(count);
				break;
			}
		}
		Database.deleteTaskType(tt);
	}
	public static void deleteUser(User u) {
		for(int count = 0;count<users.size();count++) {
			if(users.get(count).getId() == u.getId()) {
				users.remove(count);
				break;
			}
		}
		Database.deleteUser(u);
	}
	public static boolean isAdmin(String username, String password) {
		for(int count = 0;count<admins.size();count++) {
			if(username.equals(admins.get(count).getUsername())) {
				if(password.equals(DataLock.decrypt(admins.get(count).getPassword()))) {
					mainUID = -1;		//superusers sign uid as -1
					return true;
				}
			}
		}
		return false;
	}
	public static boolean isUser(String username, String password) {
		for(int count = 0;count<users.size();count++) {
			if(username.equals(users.get(count).getUsername())) {
				if(password.equals(DataLock.decrypt(users.get(count).getPassword()))) {
					mainUID = users.get(count).getId();
					return true;
				}
			}
		}
		return false;
	}
	public static boolean usernameExists(String u) {
		for(int count=0;count<users.size();count++) {
			if(users.get(count).getUsername().equals(u)) {
				return true;
			}
		}
		return false;
	}
	//for reference day on home page, stays locked to day of reference (arrowed date)
	public static ArrayList<Task> getUrgentTasks(){
		long day = getReferenceDateVal();
		ArrayList<Task> vals = new ArrayList<Task>();
		for(int count=0;count<tasks.size();count++) {
			String n = tasks.get(count).getType();
			int numOfDays = 0;
			for(int x = 0;x<taskTypes.size();x++) {
				if(taskTypes.get(x).getName().equals(n)) {
					numOfDays = taskTypes.get(x).getWarningPeriod();
				}
			}
			long startOfWarningPeriod = tasks.get(count).getDueDate() - getDayValue(numOfDays);
			
			Calendar cal1 = (Calendar) dayOfReference.clone();
			Calendar cal2 = Calendar.getInstance();
			cal2.setTimeInMillis(startOfWarningPeriod);
			
			boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
			                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
			
			if((day >= startOfWarningPeriod || sameDay) && tasks.get(count).getDueDate() >= day) {
				vals.add(tasks.get(count));
			}
		}
		return vals;
	}
	public static ArrayList<Task> getOverdueTasks(){
		long today = getReferenceDateVal();
		ArrayList<Task> vals = new ArrayList<Task>();
		for(int count = 0;count<tasks.size();count++) {
			if((tasks.get(count).getDueDate() <= today) && (tasks.get(count).getFinishFlag() == false)) {
				vals.add(tasks.get(count));
			}
		}
		return vals;
	}
	public static ArrayList<Task> getApproachingTasks(){
		long day = getReferenceDateVal();
		ArrayList<Task> vals = new ArrayList<Task>();
		for(int count=0;count<tasks.size();count++) {
			String n = tasks.get(count).getType();
			int numOfDays = 0;
			int adjustment = 0;
			for(int x = 0;x<taskTypes.size();x++) {
				if(taskTypes.get(x).getName().equals(n)) {
					numOfDays = taskTypes.get(x).getWarningPeriod();
					if(numOfDays == 1) {
						adjustment = 1;
					}
				}
			}
			//add extra half a period(time between warning period and due date) as a warning
			long startOfWarningPeriod = tasks.get(count).getDueDate() - getDayValue(numOfDays);
			long extraWarningPeriod = tasks.get(count).getDueDate() - getDayValue(numOfDays) - getDayValue((numOfDays/2)+adjustment);
			
			Calendar cal1 = (Calendar) dayOfReference.clone();
			Calendar cal2 = Calendar.getInstance();
			cal2.setTimeInMillis(extraWarningPeriod);
			
			boolean sameDayExtra = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
	                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
			
			Calendar cal3 = (Calendar) dayOfReference.clone();
			Calendar cal4 = Calendar.getInstance();
			cal4.setTimeInMillis(startOfWarningPeriod);
			
			boolean sameDayStart = cal3.get(Calendar.YEAR) == cal4.get(Calendar.YEAR) &&
			                  cal3.get(Calendar.DAY_OF_YEAR) == cal4.get(Calendar.DAY_OF_YEAR);
			
			if(((day >= extraWarningPeriod || sameDayExtra) && (day < startOfWarningPeriod)) &&
					!sameDayStart && tasks.get(count).getDueDate() >= day) {
				vals.add(tasks.get(count));
			}
		}
		return vals;
	}
	//for today labels on left sidebar, stays locked to today
	public static ArrayList<Task> getUrgentTasks(String s){
		long day = getTodaysDateVal();
		ArrayList<Task> vals = new ArrayList<Task>();
		for(int count=0;count<tasks.size();count++) {
			String n = tasks.get(count).getType();
			int numOfDays = 0;
			for(int x = 0;x<taskTypes.size();x++) {
				if(taskTypes.get(x).getName().equals(n)) {
					numOfDays = taskTypes.get(x).getWarningPeriod();
				}
			}
			long startOfWarningPeriod = tasks.get(count).getDueDate() - getDayValue(numOfDays);
			
			Calendar cal1 = (Calendar) dayOfReference.clone();
			Calendar cal2 = Calendar.getInstance();
			cal2.setTimeInMillis(startOfWarningPeriod);
			
			boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
			                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
			
			if((day >= startOfWarningPeriod || sameDay) && tasks.get(count).getDueDate() >= day &&
					tasks.get(count).getFinishFlag() == false) {
				vals.add(tasks.get(count));
			}
		}
		return vals;
	}
	public static ArrayList<Task> getOverdueTasks(String s){
		long today = getTodaysDateVal();
		ArrayList<Task> vals = new ArrayList<Task>();
		for(int count = 0;count<tasks.size();count++) {
			if((tasks.get(count).getDueDate() <= today) && (tasks.get(count).getFinishFlag() == false)) {
				vals.add(tasks.get(count));
			}
		}
		return vals;
	}
	public static ArrayList<Task> getApproachingTasks(String s){
		long day = getTodaysDateVal();
		ArrayList<Task> vals = new ArrayList<Task>();
		for(int count=0;count<tasks.size();count++) {
			String n = tasks.get(count).getType();
			int numOfDays = 0;
			int adjustment = 0;
			for(int x = 0;x<taskTypes.size();x++) {
				if(taskTypes.get(x).getName().equals(n)) {
					numOfDays = taskTypes.get(x).getWarningPeriod();
					if(numOfDays == 1) {
						adjustment = 1;
					}
				}
			}
			//add extra half a period(time between warning period and due date) as a warning
			long startOfWarningPeriod = tasks.get(count).getDueDate() - getDayValue(numOfDays);
			long extraWarningPeriod = tasks.get(count).getDueDate() - getDayValue(numOfDays) - getDayValue((numOfDays/2)+adjustment);
			
			Calendar cal1 = (Calendar) dayOfReference.clone();
			Calendar cal2 = Calendar.getInstance();
			cal2.setTimeInMillis(startOfWarningPeriod);
			
			boolean sameDayExtra = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
	                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
			
			Calendar cal3 = (Calendar) dayOfReference.clone();
			Calendar cal4 = Calendar.getInstance();
			cal4.setTimeInMillis(startOfWarningPeriod);
			
			boolean sameDayStart = cal3.get(Calendar.YEAR) == cal4.get(Calendar.YEAR) &&
			                  cal3.get(Calendar.DAY_OF_YEAR) == cal4.get(Calendar.DAY_OF_YEAR);
			
			if(((day >= extraWarningPeriod || sameDayExtra) && (day < startOfWarningPeriod)) &&
					!sameDayStart && tasks.get(count).getDueDate() >= day && tasks.get(count).getFinishFlag() == false) {
				vals.add(tasks.get(count));
			}
		}
		return vals;
	}
	//for gui calendar days
	public static ArrayList<Task> getDayTasks(Calendar cal){
		ArrayList<Task> t = new ArrayList<Task>();
		Calendar calTest = Calendar.getInstance();
		
		for(int count=0;count<tasks.size();count++) {
			calTest.setTimeInMillis(tasks.get(count).getDueDate());
			if((cal.get(Calendar.YEAR) == calTest.get(Calendar.YEAR)) &&
					(cal.get(Calendar.MONTH) == calTest.get(Calendar.MONTH)) &&
					(cal.get(Calendar.DATE) == calTest.get(Calendar.DATE))) {
				t.add(tasks.get(count));
			}
		}
		return t;
	}
	public static void addDayToDayOfReference() {
		//dayOfReference = dayOfReference.plusDays(1);
		dayOfReference.add(Calendar.DATE, 1);
	}
	public static void removeDayFromDayOfReference() {
		//dayOfReference = dayOfReference.minusDays(1);
		dayOfReference.add(Calendar.DATE, -1);
	}
	public static ArrayList<Task> getTasks(){
		return tasks;
	}
	public static ArrayList<Admin> getAdmins(){
		return admins;
	}
	public static ArrayList<Class> getClasses(){
		return classes;
	}
	public static ArrayList<Project> getProjects(){
		return projects;
	}
	public static ArrayList<TaskType> getTaskTypes(){
		return taskTypes;
	}
	public static ObservableList<Task> getTasks(String s){
		ObservableList<Task> t = FXCollections.observableArrayList();
		
		for(int count = 0;count<tasks.size();count++) {
			t.add(tasks.get(count));
		}
		return t;
	}
	public static ObservableList<Admin> getAdmins(String s){
		ObservableList<Admin> a = FXCollections.observableArrayList();
		
		for(int count = 0;count<admins.size();count++) {
			a.add(admins.get(count));
		}
		return a;
	}
	public static ObservableList<Class> getClasses(String s){
		ObservableList<Class> c = FXCollections.observableArrayList();
		
		for(int count = 0;count<classes.size();count++) {
			c.add(classes.get(count));
		}
		return c;
	}
	public static ObservableList<Project> getProjects(String s){
		ObservableList<Project> p = FXCollections.observableArrayList();
		
		for(int count = 0;count<projects.size();count++) {
			p.add(projects.get(count));
		}
		return p;
	}
	public static ObservableList<TaskType> getTaskTypes(String s){
		ObservableList<TaskType> tt = FXCollections.observableArrayList();
		
		for(int count = 0;count<taskTypes.size();count++) {
			tt.add(taskTypes.get(count));
		}
		return tt;
	}
	public static String getTypeName(int id) {
		for(int count=0;count<taskTypes.size();count++) {
			if(id == taskTypes.get(count).getId()) {
				return taskTypes.get(count).getName();
			}
		}
		System.out.println("Error MCT01");
		return "Error Retrieving Data";
	}
	public static String getClassAbr(int id) {
		for(int count=0;count<classes.size();count++) {
			if(id == classes.get(count).getId()) {
				return classes.get(count).getAbbreviation();
			}
		}
		System.out.println("Error MCT02");
		return "Error Retrieving Data";
	}
	public static int getTypeID(String name) {
		for(int count=0;count<taskTypes.size();count++) {
			if(name == taskTypes.get(count).getName()) {
				return taskTypes.get(count).getId();
			}
		}
		System.out.println("Error MCT03");
		return -1;
	}
	public static int getClassID(String abr) {
		for(int count=0;count<classes.size();count++) {
			if(abr == classes.get(count).getAbbreviation()) {
				return classes.get(count).getId();
			}
		}
		System.out.println("Error MCT04");
		return -1;
	}
	public static void closeConnection() {
		DBConn.closeConnection();
	}
	public static boolean isDuplicate() {
		//TODO: write for class abbreviations and tasktype names when adding or updating
		return false;
	}
	public static boolean isBeingUsed(Models.Class c) {
		for(int count=0;count<tasks.size();count++) {
			if(tasks.get(count).getClassAbr().equals(c.getAbbreviation())) {
				return true;
			}
		}
		return false;
	}
	public static boolean isBeingUsed(TaskType tt) {
		for(int count=0;count<tasks.size();count++) {
			if(tasks.get(count).getType().equals(tt.getName())) {
				return true;
			}
		}
		return false;
	}
	//returns false if no space in schedule
	private static boolean autoSchedule(Task t) {
		//TODO: write
		return false;
	}
	//convert number of days into long value for milliseconds
	private static long getDayValue(int numOfDays) {
		long day = 60*60*24*1000;
		day *= numOfDays;
		return day;
	}
	private static long getReferenceDateVal() {
		/*//Date t = Calendar.getInstance().getTime();
		//Date t = java.sql.Date.valueOf(dayOfReference);
		Date date = Date.from(dayOfReference.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		long val = date.getTime();
		return val;*/
		return dayOfReference.getTimeInMillis();
	}
	private static long getTodaysDateVal() {
		Calendar cal = Calendar.getInstance();
		long val = cal.getTimeInMillis();
		return val;
	}
}
