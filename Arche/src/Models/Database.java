package Models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import Runners.DBConn;

public class Database {
	static Statement stmt = null;
	
	public static void addTask(Task t) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("INSERT INTO Tasks VALUES (?,?,?,?,?,?,?,?);");
			//prep.setInt(1, t.getId());
			prep.setString(2, t.getName());
			prep.setString(3, t.getDescription());
			prep.setLong(4, t.getDueDate());
			prep.setBoolean(5, t.getFinishFlag());
			prep.setBoolean(6, t.getOnFlag());
			prep.setString(7, t.getType());
			prep.setString(8, t.getClassAbr());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB01:\n" + e.getMessage());
		}
	}
	public static void addAdmin(Admin a) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("INSERT INTO Administrators VALUES (?,?,?);");
			//prep.setInt(1, a.getId());
			prep.setString(2, a.getUsername());
			prep.setString(3, a.getPassword());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB02:\n" + e.getMessage());
		}
	}
	public static void addClass(Class c) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("INSERT INTO Classes VALUES (?,?,?,?);");
			//prep.setInt(1, c.getId());
			prep.setString(2, c.getName());
			prep.setString(3, c.getAbbreviation());
			prep.setString(4, c.getNotes());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB03:\n" + e.getMessage());
		}
	}
	public static void addProject(Project p) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("INSERT INTO Projects VALUES (?,?,?,?,?);");
			//prep.setInt(1, p.getId());
			prep.setString(2, p.getName());
			prep.setString(3, p.getDescription());
			prep.setString(4, p.getCurrentStep());
			prep.setInt(5, p.getPriorityLevel());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB04:\n" + e.getMessage());
		}
	}
	public static void addTaskType(TaskType tt) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("INSERT INTO TaskTypes VALUES (?,?,?);");
			//prep.setInt(1, tt.getId());
			prep.setString(2, tt.getName());
			prep.setString(3, tt.getDescription());
			prep.setInt(4, tt.getWarningPeriod());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB05:\n" + e.getMessage());
		}
	}
	public static void updateTask(Task t) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("UPDATE Tasks SET name = ?, description = ?, "
					+ "dueDate = ?, finishFlag = ?, class = ?, type = ? WHERE id = ?;");
			prep.setString(1, t.getName());
			prep.setString(2, t.getDescription());
			prep.setLong(3, t.getDueDate());
			prep.setBoolean(4, t.getFinishFlag());
			prep.setString(5, t.getClassAbr());
			prep.setString(6, t.getType());
			prep.setInt(7, t.getId());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB06:\n" + e.getMessage());
		}
	}
	public static void updateAdmin(Admin a) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("UPDATE Administrators SET username = ?, password = ? WHERE id = ?;");
			prep.setString(1,  a.getUsername());
			prep.setString(2,  a.getPassword());
			prep.setInt(3,  a.getId());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB07:\n" + e.getMessage());
		}
	}
	public static void updateClass(Class c) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("UPDATE Administrators SET name = ?, abbreviation = ?, notes = ? "
					+ "WHERE id = ?;");
			prep.setString(1, c.getName());
			prep.setString(2, c.getAbbreviation());
			prep.setString(3, c.getNotes());
			prep.setInt(4, c.getId());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB08:\n" + e.getMessage());
		}
	}
	public static void updateProject(Project p) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("UPDATE Projects SET name = ?, description = ?, currentStep = ?,"
					+ "priorityLevel = ? WHERE id = ?;");
			prep.setString(1, p.getName());
			prep.setString(2, p.getDescription());
			prep.setString(3, p.getCurrentStep());
			prep.setInt(4, p.getPriorityLevel());
			prep.setInt(5,  p.getId());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB09:\n" + e.getMessage());
		}
	}
	public static void updateTaskType(TaskType tt) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("UPDATE TaskTypes SET name = ?, description = ?, warningPeriod = ? "
					+ "WHERE id = ?;");
			prep.setString(1, tt.getName());
			prep.setString(2, tt.getDescription());
			prep.setInt(3, tt.getWarningPeriod());
			prep.setInt(4,  tt.getId());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB10:\n" + e.getMessage());
		}
	}
	public static void deleteTask(Task t) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("DELETE FROM Tasks WHERE id = ?;");
			prep.setInt(1, t.getId());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB11:\n" + e.getMessage());
		}
	}
	public static void deleteAdmin(Admin a) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("DELETE From Administrators WHERE id = ?;");
			prep.setInt(1,  a.getId());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB12:\n" + e.getMessage());
		}
	}
	public static void deleteClass(Class c) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("DELETE FROM Classes WHERE id = ?;");
			prep.setInt(1, c.getId());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB13:\n" + e.getMessage());
		}
	}
	public static void deleteProject(Project p) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("DELETE FROM Projects WHERE id = ?;");
			prep.setInt(1, p.getId());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB14:\n" + e.getMessage());
		}
	}
	public static void deleteTaskType(TaskType tt) {
		PreparedStatement prep = null;
		try {
			prep = DBConn.getConnection().prepareStatement("DELETE FROM TaskTypes WHERE id = ?;");
			prep.setInt(1, tt.getId());
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError DB15:\n" + e.getMessage());
		}
	}
	public static ResultSet getTasks() {
		try {
			stmt = DBConn.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Tasks;");
			return rs;
		}catch(SQLException e) {
			System.out.println("\nError DB16:\n" + e.getMessage());
		}
		return null;
	}
	public static ResultSet getAdmins() {
		try {
			stmt = DBConn.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Administrators;");
			return rs;
		}catch(SQLException e) {
			System.out.println("\nError DB17:\n" + e.getMessage());
		}
		return null;
	}
	public static ResultSet getClasses() {
		try {
			stmt = DBConn.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Classes;");
			return rs;
		}catch(SQLException e) {
			System.out.println("\nError DB18:\n" + e.getMessage());
		}
		return null;
	}
	public static ResultSet getProjects() {
		try {
			stmt = DBConn.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Projects;");
			return rs;
		}catch(SQLException e) {
			System.out.println("\nError DB19:\n" + e.getMessage());
		}
		return null;
	}
	public static ResultSet getTaskTypes() {
		try {
			stmt = DBConn.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM TaskTypes;");
			return rs;
		}catch(SQLException e) {
			System.out.println("\nError DB20:\n" + e.getMessage());
		}
		return null;
	}
}
