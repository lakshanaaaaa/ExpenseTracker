package com.expenseTracker.utilities;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;


public class DatabaseConnection {
    private static String URL="jdbc:mysql://localhost:3306/expensetracker";
    private static String USERNAME="root";
    private static String PASSWORD="lakshana@916";
    static{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("JDBC Driver loaded successfully");
        }
        catch(ClassNotFoundException e){
            System.out.println("JDBC Driver not found: "+e);
        }
    }
    public static Connection getDBConnection() throws SQLException{
        return DriverManager.getConnection(URL,USERNAME,PASSWORD);
    }
}
