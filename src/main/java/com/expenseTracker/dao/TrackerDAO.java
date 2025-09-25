package com.expenseTracker.dao;
// import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// import java.time.LocalDateTime;
import java.util.ArrayList;
import java.sql.Statement;
import java.util.List;
import com.expenseTracker.model.Category;
import com.expenseTracker.utilities.DatabaseConnection;

import com.expenseTracker.model.Expense;
import java.time.LocalDateTime;



public class TrackerDAO {
    private static final String ADD_CATEGORY= "INSERT INTO category (catName) VALUES (?)";
    private static final String GET_ALL_CATEGORIES= "SELECT * FROM category ORDER BY catId";

    public void addCategory(Category category) throws SQLException{
        try(Connection conn=DatabaseConnection.getDBConnection();
            PreparedStatement pstmt=conn.prepareStatement(ADD_CATEGORY)){
                pstmt.setString(1, category.getCatName());
                pstmt.executeUpdate();
        }
    }


    public List<Category> getAllCategories() throws SQLException{
        List<Category> categories=new ArrayList<>();
        try(Connection conn=DatabaseConnection.getDBConnection();
            Statement stmt=conn.createStatement();
            ResultSet rs=stmt.executeQuery(GET_ALL_CATEGORIES)){
                while(rs.next()){
                    int catId=rs.getInt("catId");
                    String catName=rs.getString("catName");
                    categories.add(new Category(catId, catName));
                }
        }
        return categories;
    }

    public void loadCategories() throws SQLException {
        getAllCategories();
    }

    
} 



