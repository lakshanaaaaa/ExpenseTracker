package com.expenseTracker.dao;
// import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.expenseTracker.model.Category;
import com.expenseTracker.utilities.DatabaseConnection;

import com.expenseTracker.model.Expense;
import java.time.LocalDateTime;

import javax.swing.JOptionPane;



public class TrackerDAO {
    private static final String ADD_CATEGORY= "INSERT INTO category (catName) VALUES (?)";
    private static final String GET_ALL_CATEGORIES= "SELECT * FROM category ORDER BY catId";
    private static final String GET_CATEGORY_BY_ID= "SELECT * FROM category WHERE catId = ?";
    private static final String GET_CATEGORY_BY_NAME= "SELECT * FROM category WHERE catName = ?";
    private static final String DELETE_CATEGORY="DELETE FROM category WHERE catId=?";
    private static final String UPDATE_CATEGORY="UPDATE category SET catName=? WHERE catId=?";


    private static final String ADD_EXPENSE= "INSERT INTO expense (catId, amount, notes, expense_date) VALUES (?, ?, ?, ?)";
    private static final String GET_ALL_EXPENSES= "SELECT * FROM expense ORDER BY expenseId";
    



    public void addCategory(Category category) throws SQLException{
        try(Connection conn=DatabaseConnection.getDBConnection();
            PreparedStatement pstmt=conn.prepareStatement(ADD_CATEGORY)){
                pstmt.setString(1, category.getCatName());
                int update=pstmt.executeUpdate();
                if(update>0){
                    JOptionPane.showMessageDialog(null,"Category added successfully","Success",JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(null,"Failed to add category","Error",JOptionPane.ERROR_MESSAGE);
                }
        }
    }


    public List<Category> getAllCategories() throws SQLException{
        List<Category> categories=new ArrayList<>();
        try(Connection conn=DatabaseConnection.getDBConnection();
            PreparedStatement stmt=conn.prepareStatement(GET_ALL_CATEGORIES);
            ResultSet rs=stmt.executeQuery())
            {
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

    public boolean deleteCategory(int catId) throws SQLException{
        try(Connection conn=DatabaseConnection.getDBConnection();
        PreparedStatement stmt=conn.prepareStatement(DELETE_CATEGORY);){
            stmt.setInt(1,catId);
            int row=stmt.executeUpdate();
            return row>0;
        }
    }

    public boolean updateCategory(Category category) throws SQLException{
        try(Connection conn=DatabaseConnection.getDBConnection();
        PreparedStatement stmt=conn.prepareStatement(UPDATE_CATEGORY);){
            stmt.setString(1,category.getCatName());
            stmt.setInt(2,category.getCatId());
            int row=stmt.executeUpdate();
            if(row>0){
                return true;
            }
            else{
                JOptionPane.showMessageDialog(null,"Failed to update category","Error",JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }



















    public Category getCategoryById(int catId) throws SQLException {;
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_CATEGORY_BY_ID)) {
            pstmt.setInt(1, catId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String catName = rs.getString("catName");
                    return new Category(catId,catName);
                }
            }
        }
        return null; // or throw an exception if preferred
    }

    public List<Expense> getAllExpenses() throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_EXPENSES);
             ResultSet rs = stmt.executeQuery()) {
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

    public boolean addExpense(Expense expense) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
        PreparedStatement pstmt = conn.prepareStatement(ADD_EXPENSE)) {   

            pstmt.setInt(1, expense.getCatId());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getNotes());  
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(expense.getDate()));
            int row=pstmt.executeUpdate();
            return row>0;
        }
    }

    public boolean deleteExpense(int expId) throws SQLException {
        String DELETE_EXPENSE = "DELETE FROM expense WHERE expenseId = ?";
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_EXPENSE)) {
            pstmt.setInt(1, expId);
            int row=pstmt.executeUpdate();
            return row>0;
        }
    }

    public boolean updateExpense(Expense expense) throws SQLException {
        String UPDATE_EXPENSE = "UPDATE expense SET catId = ?, amount = ?, notes = ?, expense_date = ? WHERE expenseId = ?";
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_EXPENSE)) {
            pstmt.setInt(1, expense.getCatId());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getNotes());
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(expense.getDate()));
            pstmt.setInt(5, expense.getExpId());
            int row=pstmt.executeUpdate();
            return row>0;
        }
    }

    public List<Expense> getSelectedExpenses(String catName) throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        int catId= getCategoryByName(catName).getCatId();
        String GET_EXPENSES_BY_CATEGORY = "SELECT * FROM expense WHERE catId = ? ORDER BY expenseId";
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_EXPENSES_BY_CATEGORY)) {
            pstmt.setInt(1, catId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int expId = rs.getInt("expenseId");
                    String notes = rs.getString("notes");
                    double amount = rs.getDouble("amount");
                    LocalDateTime date = rs.getTimestamp("expense_date").toLocalDateTime();
                    expenses.add(new Expense(expId, catId, notes, amount, date));
                }
            }
        }
        return expenses;
    }

    
} 



