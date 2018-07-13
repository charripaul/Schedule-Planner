package Runners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {        
    static Connection con=null;
    static {
    	System.out.println("Database connection initalized");
    }
    public static Connection getConnection() {
    	if(con != null) {
    		try {
    			con.close();
    		}catch(Exception e) {
    			System.out.println("Connection close test error: " + e.getMessage());
    		}
    	}
    	try
        {
        	Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
        }
        catch(Exception e)
        {
        	System.out.println("DBConnect error: " + e.getMessage());
        }
        return con;
    }
    public static void closeConnection() {
    	if(con != null) {
    	    try {
    	      con.close();
    	      con = null;
    	      System.out.println("Database connection terminated");
    	    } catch (SQLException e) {
    	        System.out.println("Static Close Destroy Command Error: " + e.getMessage());
    	    }
    	}
    }
} 