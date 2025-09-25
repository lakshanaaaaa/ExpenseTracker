package com.expenseTracker.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import com.expenseTracker.dao.TrackerDAO;
import com.expenseTracker.model.Category;

public class CategoryManagementGUI extends JFrame 
{
    private MainGUI mainGUI;
    private TrackerDAO trackerDAO;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JTextField categoryField;
    private JButton addButton;

    public CategoryManagementGUI(MainGUI mainGUI, TrackerDAO trackerDAO) {
        this.mainGUI = mainGUI;
        this.trackerDAO = trackerDAO;
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadCategories();
    }

    private void initializeComponents() {
        setTitle("Categories");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600); 
        setLocationRelativeTo(mainGUI);

        String[] columnNames = {"ID", "Category"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        categoryField = new JTextField(20);
        addButton = new JButton("Add Category");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Category"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(categoryField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(inputPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(categoryTable), BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Select a Category to Edit or Delete"));
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        addButton.addActionListener(e -> addCategory());
    }

    private void addCategory() {
        String catName = categoryField.getText().trim();
        if (catName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category name cannot be empty", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Category newCategory = new Category(catName);
            trackerDAO.addCategory(newCategory);
            JOptionPane.showMessageDialog(this, "Category added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            categoryField.setText("");
            loadCategories();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding category: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCategories() {
        try {
            List<Category> categories = trackerDAO.getAllCategories();
            tableModel.setRowCount(0);
            for (Category cat : categories) {
                Object[] row = {cat.getCatId(), cat.getCatName()};
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
