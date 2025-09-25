package com.expenseTracker;

import com.expenseTracker.gui.MainGUI;
import com.expenseTracker.utilities.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getDBConnection();)
        {
            System.out.println("Database connected successfully");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }

        // 2️⃣ Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Could not set look and feel: " + e.getMessage());
        }

        // 3️⃣ Launch the GUI in the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                new MainGUI().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error starting the application: " + e.getLocalizedMessage());
            }
        });
    }
}
