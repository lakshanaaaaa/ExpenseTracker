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
    private static final String ADD_EXPENSE= "INSERT INTO expense (catId, amount, notes, expense_date) VALUES (?, ?, ?, ?)";
    private static final String GET_ALL_EXPENSES= "SELECT * FROM expense ORDER BY expenseId";
    private static final String GET_CATEGORY_BY_ID= "SELECT * FROM category WHERE catId = ?";
    private static final String GET_CATEGORY_BY_NAME= "SELECT * FROM category WHERE catName = ?";


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












    public Category getCategoryById(int catId) throws SQLException {;
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_CATEGORY_BY_ID)) {
            pstmt.setInt(1, catId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String catName = rs.getString("catName");
                    return new Category(catId, catName);
                }
            }
        }
        return null; // or throw an exception if preferred
    }

    public List<Expense> getAllExpenses() throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_EXPENSES)) {
            while (rs.next()) {
                int expId = rs.getInt("expenseId");
                int catId = rs.getInt("catID");
                String notes = rs.getString("notes");
                double amount = rs.getDouble("amount");
                LocalDateTime date = rs.getTimestamp("expense_date").toLocalDateTime();
                expenses.add(new Expense(expId, catId, notes, amount, date));
            }
        }
        return expenses;
    }

    public Category getCategoryByName(String catName) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_CATEGORY_BY_NAME)) {
            pstmt.setString(1, catName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int catId = rs.getInt("catId");
                    return new Category(catId, catName);
                }
            }
        }
        return null; // or throw an exception if preferred
    }

    public void addExpense(Expense expense) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
        PreparedStatement pstmt = conn.prepareStatement(ADD_EXPENSE)) {   

            pstmt.setInt(1, expense.getCatId());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getNotes());  
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(expense.getDate()));
            pstmt.executeUpdate();
        }
    }
    
} 



