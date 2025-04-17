package main.java.com.library.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private BookManagementPanel bookManagementPanel;
    private IssueReturnPanel issueReturnPanel;
    private ReportsPanel reportsPanel;
    private String username;
    
    public DashboardFrame(String username) {
        this.username = username;
        
        setTitle("Library Management System - Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        layoutComponents();
        
        setVisible(true);
    }
    
    private void initComponents() {
        // Initialize components
        tabbedPane = new JTabbedPane();
        bookManagementPanel = new BookManagementPanel();
        issueReturnPanel = new IssueReturnPanel();
        reportsPanel = new ReportsPanel();
        
        // Add tabs
        tabbedPane.addTab("Book Management", new ImageIcon(), bookManagementPanel, "Manage books");
        tabbedPane.addTab("Issue/Return Books", new ImageIcon(), issueReturnPanel, "Issue or return books");
        tabbedPane.addTab("Reports", new ImageIcon(), reportsPanel, "View reports");
    }
    
    private void layoutComponents() {
        // Create welcome panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.add(logoutButton);
        
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);
        welcomePanel.add(topRightPanel, BorderLayout.EAST);
        
        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add main panel to frame
        getContentPane().add(mainPanel);
    }
    
    private void logout() {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", 
                "Logout Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginFrame();
        }
    }
}
