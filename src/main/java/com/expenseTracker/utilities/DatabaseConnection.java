package ExpenseTracker.src.main.java.com.expenseTracker.utilities;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static String URL="dbc:mysql://localhost:3306/todo";
    private static String USERNAME="root";
    private static String PASSWORD="Lakshana@123";
    static{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e){
            System.out.println("JDBC Driver not found: "+e);
        }
    }
    private static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL,USERNAME,PASSWORD);
    }
}
