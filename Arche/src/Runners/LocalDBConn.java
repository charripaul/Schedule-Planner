package Runners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LocalDBConn {        
    static Connection con=null;
    static String localTag = "'local'";
    static String prodTag = "'production'";
    static {
    	//System.out.println("Local Database connection initalized");
    }
    public static void updateSavedUsername(String s) {
		PreparedStatement prep = null;
		try {
			prep = getConnection().prepareStatement("UPDATE preferences SET loginUsername = ? WHERE id = 1;");
			prep.setString(1, s);
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError LCN01:\n" + e.getMessage());
		}
	}
    public static String getSavedUsername() {
    	Statement stmt = null;
    	String username = "";
		try {
			stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM preferences;");
			username = rs.getString("loginUsername");
		}catch(SQLException e) {
			System.out.println("\nError LCN02:\n" + e.getMessage());
		}
		return username;
	}
    public static String getLocalDBUsername() {
    	Statement stmt = null;
    	String username = "";
    	String tag = "";
    	try {
    		stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM DatabaseCredentials WHERE Tag = " +
    		localTag +
    		";");
			username = rs.getString("DBUsername");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return username;
    }	
    public static String getLocalDBPassword() {
    	Statement stmt = null;
    	String password = "";
    	try {
    		stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM DatabaseCredentials WHERE Tag = "+
    		localTag+
    		";");
			password = rs.getString("DBPassword");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return password;
    }
    public static String getLocalDBAddress() {
    	Statement stmt = null;
    	String address = "";
    	try {
    		stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM DatabaseCredentials WHERE Tag = "+
    		localTag+
    		";");
			address = rs.getString("DBAddress");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return address;
    }
    public static String getProdDBUsername() {
    	Statement stmt = null;
    	String username = "";
    	try {
    		stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM DatabaseCredentials WHERE Tag = "+
    		prodTag+
    		";");
			username = rs.getString("DBUsername");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return username;
    }
    public static String getProdDBPassword() {
    	Statement stmt = null;
    	String password = "";
    	try {
    		stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM DatabaseCredentials WHERE Tag = "+
    		prodTag+
    		";");
			password = rs.getString("DBPassword");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return password;
    }
    public static String getProdDBAddress() {
    	Statement stmt = null;
    	String address = "";
    	try {
    		stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM DatabaseCredentials WHERE Tag = "+
    		prodTag+
    		";");
			address = rs.getString("DBAddress");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return address;
    }
    public static Connection getConnection() {
    	if(con != null) {
    		try {
    			con.close();
    		}catch(Exception e) {
    			System.out.println("Local Connection close test error: " + e.getMessage());
    		}
    	}
    	try
        {
    		Class.forName("org.sqlite.JDBC").newInstance();
			con = DriverManager.getConnection("jdbc:sqlite::resource:pref.sqlite");
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    	return con;
    }
    public static void closeConnection() {
    	if(con != null) {
    	    try {
    	      con.close();
    	      con = null;
    	      //System.out.println("Database connection terminated");
    	    } catch (SQLException e) {
    	        System.out.println("Local Static Close Destroy Command Error: " + e.getMessage());
    	    }
    	}
    }
}