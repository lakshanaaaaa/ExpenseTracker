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

public class ExpenseManagementGUI extends JFrame 
{
    private MainGUI mainGUI;
    private TrackerDAO trackerDAO;
    private JTable expenseTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> categoryComboBox;
    private JTextArea notes;
    private JTextField amount;

    private JButton addButton;

    public ExpenseManagementGUI(MainGUI mainGUI, TrackerDAO trackerDAO) {
        this.mainGUI = mainGUI;
        this.trackerDAO = trackerDAO;
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadExpenses();
    }

    private void initializeComponents() {
        setTitle("Expenses");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600); 
        setLocationRelativeTo(mainGUI);

        String[] columnNames = {"ID", "Category", "Notes", "Amount", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        expenseTable = new JTable(tableModel);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        String [] categories = giveCategories();

        categoryComboBox = new JComboBox<>(categories);
        notes = new JTextArea(3, 20);
        amount = new JTextField(20);
        addButton = new JButton("Add Expense");


        // categoryComboBox.addActionListener((e)->{
        //     filterCategory();
        // });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Input Panel at the TOP
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Category"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(categoryComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Notes"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(new JScrollPane(notes), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Amount"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(amount, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(addButton, gbc);

        // Add Input panel at the TOP
        add(inputPanel, BorderLayout.NORTH);

        // Add Table at the BOTTOM (filling space)
        JScrollPane tableScrollPane = new JScrollPane(expenseTable);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    private void setupEventListeners(){
        addButton.addActionListener(e->addExpense());
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