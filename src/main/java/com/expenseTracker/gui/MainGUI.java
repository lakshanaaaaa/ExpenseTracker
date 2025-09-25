package com.expenseTracker.gui;

import javax.swing.*;
import com.expenseTracker.dao.TrackerDAO;

public class MainGUI extends JFrame {
    private TrackerDAO trackerDAO;
    private JButton categoryButton;
    private JButton expenseButton;

    public MainGUI() {
        this.trackerDAO = new TrackerDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        this.categoryButton = new JButton("Manage Categories");
        this.expenseButton = new JButton("Manage Expenses");
        this.setTitle("Expense Tracker");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // Center the window
    }

    private void setupLayout() {
        this.setLayout(null);
        categoryButton.setBounds(215, 50, 140, 40);
        expenseButton.setBounds(450, 50, 140, 40);
        this.add(categoryButton);
        this.add(expenseButton);
    }

    private void setupEventListeners() {
        categoryButton.addActionListener(e -> {
            new CategoryManagementGUI(this, trackerDAO).setVisible(true);
        });
        expenseButton.addActionListener(e->{
            new ExpenseManagementGUI(this,trackerDAO).setVisible(true);
        });
    }
}
