package Runners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {        
    static Connection con=null;
    static boolean production = false;			//use production database or not
    
    static {
    	System.out.println("Database connection initalized");
    	if(production) {
    		printProd();
    	}
    	else {
    		printLocal();
    	}
    }
    public static Connection getConnection(){
    	if(con != null) {
    		try {
    			con.close();
    			con = null;
    		}catch(Exception e) {
    			System.out.println("Connection close test error: " + e.getMessage());
    		}
    	}
    	while(con == null) {
    		try
            {
            	Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            	if(production) {
            		//mysql, for production
            		//con = DriverManager.getConnection("jdbc:mysql://den1.mysql4.gear.host:3306/scheduleplannera","scheduleplannera","Watermelon77!");
            		con = DriverManager.getConnection(LocalDBConn.getProdDBAddress(),LocalDBConn.getProdDBUsername(),LocalDBConn.getProdDBPassword());
            	}else {
            		//mysql, for testing
            		//con = DriverManager.getConnection("jdbc:mysql://localhost:3306/scheduleplannerdb","root","trinity77");
            		con = DriverManager.getConnection(LocalDBConn.getLocalDBAddress(),LocalDBConn.getLocalDBUsername(),LocalDBConn.getLocalDBPassword());
            	}
            }
            catch(Exception e)
            {
            	System.out.println("DBConnect error: " + e.getMessage());
            	System.out.println("Attempting to reconnect");
            	try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
            }
    	}
        return con;
    }
    public static void closeConnection() {
    	boolean fail = false;
    	if(con != null) {
    	    try {
    	      con.close();
    	      con = null;
    	    } catch (SQLException e) {
    	        System.out.println("Static Close Destroy Command Error: " + e.getMessage());
    	        fail = true;
    	    }
    	}
    	if(fail == false) {
    		System.out.println("Database connection terminated");
    	}
    }
    private static void printLocal() {
    	System.out.println("Connecting to non-production database");
    }
    private static void printProd() {
    	System.out.println("Connecting to production database");
    }
}