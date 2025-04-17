package main.java.com.library;

import main.java.com.library.dao.AdminDAO;
import main.java.com.library.db.DBConnection;
import main.java.com.library.model.Admin;
import main.java.com.library.ui.LoginFrame;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Test database connection
        try {
            Connection connection = DBConnection.getConnection();
            if (connection != null) {
                System.out.println("Database connection successful");

                // Check if admin exists, if not create default admin
                AdminDAO adminDAO = new AdminDAO();
                if (!adminDAO.adminExists(connection)) {
                    Admin defaultAdmin = new Admin();
                    defaultAdmin.setUsername("admin");
                    defaultAdmin.setPassword("admin123");
                    adminDAO.addAdmin(connection, defaultAdmin);
                    System.out.println("Default admin created: admin/admin123");
                }

                // Close connection after operations
                connection.close();

                // Start application
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        LoginFrame loginFrame = new LoginFrame();
                        loginFrame.setVisible(true);
                    }
                });
            } else {
                System.err.println("Failed to establish database connection");
                JOptionPane.showMessageDialog(null,
                        "Failed to connect to the database.\nPlease check your database configuration.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "An error occurred while connecting to the database:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
