package main.java.com.library.ui;

import main.java.com.library.dao.BookDAO;
import main.java.com.library.dao.IssuedBookDAO;
import main.java.com.library.model.Book;
import main.java.com.library.model.IssuedBook;
import main.java.com.library.util.DateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class IssueReturnPanel extends JPanel {
    private JTextField isbnField, studentNameField, issueDateField;
    private JButton issueButton, returnButton, searchButton;
    private JTable issueRecordsTable;
    private DefaultTableModel tableModel;
    private JLabel bookTitleLabel;
    private JLabel availableCopiesLabel;
    
    private BookDAO bookDAO;
    private IssuedBookDAO issuedBookDAO;
    
    public IssueReturnPanel() {
        bookDAO = new BookDAO();
        issuedBookDAO = new IssuedBookDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        layoutComponents();
        loadIssuedBooks();
    }
    
    private void initComponents() {
        // Form components
        isbnField = new JTextField(15);
        studentNameField = new JTextField(20);
        issueDateField = new JTextField(10);
        issueDateField.setText(DateUtil.formatDate(LocalDate.now())); // Default to today
        
        bookTitleLabel = new JLabel("Book: ");
        availableCopiesLabel = new JLabel("Available Copies: ");
        
        // Buttons
        searchButton = new JButton("Search");
        issueButton = new JButton("Issue Book");
        returnButton = new JButton("Return Book");
        
        // Table components
        String[] columns = {"ID", "Student Name", "Book Title", "ISBN", "Issue Date", "Return Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        issueRecordsTable = new JTable(tableModel);
        issueRecordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Custom renderer for status column
        issueRecordsTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = (String) value;
                if ("Returned".equals(status)) {
                    c.setForeground(new Color(0, 128, 0)); // Green
                } else if ("Overdue".equals(status)) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(new Color(0, 0, 128)); // Blue
                }
                
                return c;
            }
        });
        
        // Add action listeners
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBook();
            }
        });
        
        issueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                issueBook();
            }
        });
        
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnBook();
            }
        });
    }
    
    private void layoutComponents() {
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Issue a Book"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // ISBN label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ISBN:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(isbnField, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 0;
        formPanel.add(searchButton, gbc);
        
        // Book title
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        formPanel.add(bookTitleLabel, gbc);
        
        // Available copies
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(availableCopiesLabel, gbc);
        
        // Student name
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Student Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(studentNameField, gbc);
        
        // Issue date
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Issue Date:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(issueDateField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(issueButton);
        buttonPanel.add(returnButton);
        
        // Table panel
        JScrollPane tableScrollPane = new JScrollPane(issueRecordsTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Issued Books"));
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Add panels to main panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }
    
    private void loadIssuedBooks() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Load issued books from database
        List<IssuedBook> issuedBooks = issuedBookDAO.getAllIssuedBooks();
        
        // Add issued books to table
        for (IssuedBook issuedBook : issuedBooks) {
            String status = issuedBook.getReturnDate() != null ? "Returned" : 
                           issuedBook.isOverdue() ? "Overdue" : "Issued";
            
            Object[] row = {
                issuedBook.getId(),
                issuedBook.getStudentName(),
                issuedBook.getBookTitle(),
                issuedBook.getIsbn(),
                DateUtil.formatDate(issuedBook.getIssueDate()),
                issuedBook.getReturnDate() != null ? DateUtil.formatDate(issuedBook.getReturnDate()) : "",
                status
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchBook() {
        String isbn = isbnField.getText().trim();
        
        if (isbn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an ISBN", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Book book = bookDAO.getBookByIsbn(isbn);
        
        if (book == null) {
            bookTitleLabel.setText("Book: Not found");
            availableCopiesLabel.setText("Available Copies: 0");
            JOptionPane.showMessageDialog(this, "Book not found", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            bookTitleLabel.setText("Book: " + book.getTitle() + " by " + book.getAuthor());
            availableCopiesLabel.setText("Available Copies: " + book.getAvailableCopies());
        }
    }
    
    private void issueBook() {
        String isbn = isbnField.getText().trim();
        String studentName = studentNameField.getText().trim();
        String issueDateStr = issueDateField.getText().trim();
        
        // Validate inputs
        if (isbn.isEmpty() || studentName.isEmpty() || issueDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if book exists and is available
        Book book = bookDAO.getBookByIsbn(isbn);
        if (book == null) {
            JOptionPane.showMessageDialog(this, "Book not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (book.getAvailableCopies() <= 0) {
            JOptionPane.showMessageDialog(this, "No copies available for this book", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Parse issue date
        LocalDate issueDate;
        try {
            issueDate = DateUtil.parseDate(issueDateStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use dd-MM-yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create issued book
        IssuedBook issuedBook = new IssuedBook();
        issuedBook.setIsbn(isbn);
        issuedBook.setStudentName(studentName);
        issuedBook.setIssueDate(issueDate);
        issuedBook.setBookTitle(book.getTitle());
        
        // Save to database
        boolean success = issuedBookDAO.addIssuedBook(issuedBook);
        
        if (success) {
            // Update available copies
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookDAO.updateBook(book);
            
            // Refresh UI
            loadIssuedBooks();
            clearForm();
            JOptionPane.showMessageDialog(this, "Book issued successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to issue book", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void returnBook() {
        int selectedRow = issueRecordsTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to return", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get record ID
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String isbn = (String) tableModel.getValueAt(selectedRow, 3);
        String status = (String) tableModel.getValueAt(selectedRow, 6);
        
        // Check if already returned
        if ("Returned".equals(status)) {
            JOptionPane.showMessageDialog(this, "This book has already been returned", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm return
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to return this book?", "Confirm Return", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Return book
            boolean success = issuedBookDAO.returnBook(id);
            
            if (success) {
                // Update available copies
                Book book = bookDAO.getBookByIsbn(isbn);
                if (book != null) {
                    book.setAvailableCopies(book.getAvailableCopies() + 1);
                    bookDAO.updateBook(book);
                }
                
                // Refresh UI
                loadIssuedBooks();
                JOptionPane.showMessageDialog(this, "Book returned successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to return book", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearForm() {
        isbnField.setText("");
        studentNameField.setText("");
        issueDateField.setText(DateUtil.formatDate(LocalDate.now()));
        bookTitleLabel.setText("Book: ");
        availableCopiesLabel.setText("Available Copies: ");
    }
}
