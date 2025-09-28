package com.expenseTracker.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.expenseTracker.dao.TrackerDAO;
import com.expenseTracker.model.Category;
import com.expenseTracker.model.Expense;

public class ExpenseManagementGUI extends JPanel 
{
    private MainGUI mainGUI;
    private TrackerDAO trackerDAO;
    private JTable expenseTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> filterComboBox;
    private JComboBox<String> categoryComboBox;
    private JTextArea notes;
    private JTextField amount;

    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton backButton;


    public ExpenseManagementGUI(MainGUI mainGUI, TrackerDAO trackerDAO) {
        this.mainGUI = mainGUI;
        this.trackerDAO = trackerDAO;
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadExpenses();
    }


    private void initializeComponents() {
        // setTitle("Expenses");
        // setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // setSize(800, 600); 
        // setLocationRelativeTo(mainGUI);

        this.add(new JLabel("Expense Management"));
        backButton = new JButton("Back");
        this.add(backButton);

        // ===== Table Setup =====
        String[] columnNames = {"ID", "Category", "Notes", "Amount", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make table read-only
            }
        };
        expenseTable = new JTable(tableModel);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ===== Categories =====
        String[] categories = giveCategories();

        filterComboBox = new JComboBox<>(categories);
        filterComboBox.insertItemAt("All", 0);
        filterComboBox.setSelectedIndex(0); // default to "All"

        categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.setPreferredSize(new Dimension(200, 25));

        // ===== Input Fields =====
        notes = new JTextArea(3, 20);
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);
        notes.setPreferredSize(new Dimension(200, 60));

        amount = new JTextField();
        amount.setPreferredSize(new Dimension(200, 25));

        // ===== Buttons =====
        addButton = new JButton("Add Expense");
        deleteButton =new JButton("Delete Expense");
        updateButton =new JButton("Update Expense");
    }


    private void setupLayout() {
        setLayout(new BorderLayout());

        // ===== Filter Panel =====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterComboBox);

        // ===== Input Panel =====
        JPanel inputPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.NONE; // keep preferred sizes
        gbc.anchor = GridBagConstraints.WEST;

        // Category
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(categoryComboBox, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Notes:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(new JScrollPane(notes), gbc);

        // Amount
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        inputPanel.add(amount, gbc);

        // Add Button just below Amount
        gbc.gridx = 0; gbc.gridy = 3; 
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        buttonPanel.add(addButton, gbc);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(new JLabel("Expense Management", SwingConstants.CENTER), BorderLayout.CENTER);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(inputPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);
        northPanel.add(filterPanel, BorderLayout.NORTH);
        northPanel.add(topPanel, BorderLayout.NORTH);

        add(northPanel, BorderLayout.NORTH);

        // ===== Table =====
        add(new JScrollPane(expenseTable), BorderLayout.CENTER);
    }

    private void setupEventListeners(){
        addButton.addActionListener(e->addExpense());
        deleteButton.addActionListener(e->deleteButton());
        updateButton.addActionListener(e->updateExpense());
        filterComboBox.addActionListener(e->filterExpense());
        backButton.addActionListener(e -> mainGUI.showHome());

        expenseTable.getSelectionModel().addListSelectionListener(e -> {
        int row = expenseTable.getSelectedRow();
        if (row != -1) {
            populateFieldsFromRow(row);
        }
    });
    }

    private void addExpense() {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        String notesText = notes.getText().trim();
        String amountText = amount.getText().trim();

        if (selectedCategory == null || selectedCategory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a category", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Amount cannot be empty", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double amountValue;
        try {
            amountValue = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Category category = trackerDAO.getCategoryByName(selectedCategory);
            if (category == null) {
                JOptionPane.showMessageDialog(this, "Selected category does not exist", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Expense newExpense = new Expense(category.getCatId(), amountValue);
            newExpense.setNotes(notesText);
            newExpense.setDate(LocalDateTime.now());
            trackerDAO.addExpense(newExpense);
            JOptionPane.showMessageDialog(this, "Expense added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            notes.setText("");
            amount.setText("");
            loadExpenses();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding expense: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void deleteButton() {
        int row = expenseTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this expense?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(row, 0);
            try {
                boolean deleted = trackerDAO.deleteExpense(id);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Expense deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete expense", "Error", JOptionPane.ERROR_MESSAGE);
                }
                loadExpenses();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting expense: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // // Call this method whenever you want to populate fields for a specific row
    private void populateFieldsFromRow(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) {
            return; // Invalid row, do nothing
        }

        // Assuming table columns: ID, Category, Notes, Amount, Date
        String category = (String) tableModel.getValueAt(row, 1);
        String notesText = (String) tableModel.getValueAt(row, 2);
        String amountText = String.valueOf(tableModel.getValueAt(row, 3));

        categoryComboBox.setSelectedItem(category);
        notes.setText(notesText);
        amount.setText(amountText);
    }


    private void updateExpense() {
        int row = expenseTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        String notesText = notes.getText().trim();
        String amountText = amount.getText().trim();

        if (selectedCategory == null || selectedCategory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a category", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Amount cannot be empty", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double amountValue;
        try {
            amountValue = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int expId = (int) tableModel.getValueAt(row, 0);
            Category category = trackerDAO.getCategoryByName(selectedCategory);
            if (category == null) {
                JOptionPane.showMessageDialog(this, "Selected category does not exist", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Expense updatedExpense = new Expense(expId, category.getCatId(), notesText, amountValue, LocalDateTime.now());
            boolean success = trackerDAO.updateExpense(updatedExpense);
            if (success) {
                JOptionPane.showMessageDialog(this, "Expense updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                notes.setText("");
                amount.setText("");
                loadExpenses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update expense", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating expense: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void filterExpense(){
        String selectedCategory =(String) filterComboBox.getSelectedItem();
        if(selectedCategory.equals("All")){
            loadExpenses();
            return;
        }
        try{
            List<Expense> getSelectedExpenses= trackerDAO.getSelectedExpenses(selectedCategory);
            tableModel.setRowCount(0);
            for(Expense exp:getSelectedExpenses){
                Category cat=trackerDAO.getCategoryById(exp.getCatId());
                String catName=(cat!=null)?cat.getCatName():"Unknown";
                Object[] row={exp.getExpId(),catName,exp.getNotes(),exp.getAmount(),exp.getDate().toString()};
                tableModel.addRow(row);
            }
            updateTable();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error filtering expenses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE); 

        }
    }

    private void updateTable(){
        tableModel.fireTableDataChanged();
    }
    private void loadExpenses() {
        try {
            List<Expense> expenses = trackerDAO.getAllExpenses();
            tableModel.setRowCount(0);
            for (Expense exp : expenses) {
                Category cat = trackerDAO.getCategoryById(exp.getCatId());
                String catName = (cat != null) ? cat.getCatName() : "Unknown";
                Object[] row = {exp.getExpId(), catName, exp.getNotes(), exp.getAmount(), exp.getDate().toString()};
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] giveCategories()  {
        try{
            List<Category> catList = trackerDAO.getAllCategories();
            List<String> names = new ArrayList<>();
            for (Category c : catList) {
                names.add(c.getCatName());
            }
            return names.toArray(new String[0]);
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

}