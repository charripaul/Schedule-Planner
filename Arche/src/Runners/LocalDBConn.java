package Runners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LocalDBConn {        
    static Connection con=null;
    static {
    	//System.out.println("Local Database connection initalized");
    }
    public static void updateLoginUsername(String s) {
		PreparedStatement prep = null;
		try {
			prep = getConnection().prepareStatement("UPDATE preferences SET loginUsername = ? WHERE id = 1;");
			prep.setString(1,  s);
			prep.execute();
			prep.close();
		}catch(SQLException e) {
			System.out.println("\nError LCN01:\n" + e.getMessage());
		}
	}
    public static String getLoginUsername() {
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
			con = DriverManager.getConnection("jdbc:sqlite::resource:pref.sqlite");		//sqlite, for production
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