package com.expenseTracker.gui;

import javax.swing.*;
import java.awt.*;
import com.expenseTracker.dao.TrackerDAO;

public class MainGUI extends JFrame {
    private TrackerDAO trackerDAO;
    private JButton categoryButton;
    private JButton expenseButton;

    // Panels
    private JPanel mainPanel;   // container with CardLayout
    private JPanel homePanel;   // home with buttons
    private CategoryManagementGUI categoryPanel;
    private ExpenseManagementGUI expensePanel;

    private CardLayout cardLayout;

    public MainGUI() {
        this.trackerDAO = new TrackerDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        this.setTitle("Expense Tracker");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // Main container with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Home panel with buttons
        homePanel = new JPanel(null);
        categoryButton = new JButton("Manage Categories");
        expenseButton = new JButton("Manage Expenses");
        categoryButton.setBounds(215, 50, 140, 40);
        expenseButton.setBounds(450, 50, 140, 40);
        homePanel.add(categoryButton);
        homePanel.add(expenseButton);

        // Add panels to CardLayout
        mainPanel.add(homePanel, "HOME");
    }

    private void setupLayout() {
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        categoryButton.addActionListener(e -> {
            if (categoryPanel == null) {
                categoryPanel = new CategoryManagementGUI(this, trackerDAO);
                mainPanel.add(categoryPanel, "CATEGORY");
            }
            cardLayout.show(mainPanel, "CATEGORY");
        });

        expenseButton.addActionListener(e -> {
            if (expensePanel == null) {
                expensePanel = new ExpenseManagementGUI(this, trackerDAO);
                mainPanel.add(expensePanel, "EXPENSE");
            }
            cardLayout.show(mainPanel, "EXPENSE");
        });
    }

    // Method to go back home
    public void showHome() {
        cardLayout.show(mainPanel, "HOME");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}
