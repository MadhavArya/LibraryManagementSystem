package main.java.com.library.ui;

import main.java.com.library.dao.BookDAO;
import main.java.com.library.model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class BookManagementPanel extends JPanel {
    private JTextField searchField;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private JTextField titleField, authorField, isbnField, categoryField;
    private JSpinner totalCopiesSpinner;
    private BookDAO bookDAO;
    
    public BookManagementPanel() {
        bookDAO = new BookDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        layoutComponents();
        loadBooks();
    }
    
    private void initComponents() {
        // Search panel components
        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchBooks();
            }
        });
        
        // Table components
        String[] columns = {"ID", "Title", "Author", "ISBN", "Category", "Total Copies", "Available Copies"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        booksTable = new JTable(tableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Form components
        titleField = new JTextField(20);
        authorField = new JTextField(20);
        isbnField = new JTextField(20);
        categoryField = new JTextField(20);
        totalCopiesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        
        // Buttons
        addButton = new JButton("Add Book");
        editButton = new JButton("Edit Book");
        deleteButton = new JButton("Delete Book");
        refreshButton = new JButton("Refresh");
        
        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editBook();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBooks();
            }
        });
    }
    
    private void layoutComponents() {
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(refreshButton);
        
        // Table panel
        JScrollPane tableScrollPane = new JScrollPane(booksTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(titleField, gbc);
        
        // Author
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Author:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(authorField, gbc);
        
        // ISBN
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("ISBN:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(isbnField, gbc);
        
        // Category
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Category:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(categoryField, gbc);
        
        // Total Copies
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Total Copies:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(totalCopiesSpinner, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Add form and buttons to form container
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.add(formPanel, BorderLayout.CENTER);
        formContainer.add(buttonPanel, BorderLayout.SOUTH);
        
        // Main layout
        add(searchPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(formContainer, BorderLayout.EAST);
    }
    
    private void loadBooks() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Load books from database
        List<Book> books = bookDAO.getAllBooks();
        
        // Add books to table
        for (Book book : books) {
            Object[] row = {
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCategory(),
                book.getTotalCopies(),
                book.getAvailableCopies()
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchBooks() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadBooks();
            return;
        }
        
        // Clear table
        tableModel.setRowCount(0);
        
        // Search books from database
        List<Book> books = bookDAO.searchBooks(searchTerm);
        
        // Add books to table
        for (Book book : books) {
            Object[] row = {
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCategory(),
                book.getTotalCopies(),
                book.getAvailableCopies()
            };
            tableModel.addRow(row);
        }
    }
    
    private void addBook() {
        // Validate form
        if (!validateForm()) {
            return;
        }
        
        // Create book object
        Book book = new Book();
        book.setTitle(titleField.getText().trim());
        book.setAuthor(authorField.getText().trim());
        book.setIsbn(isbnField.getText().trim());
        book.setCategory(categoryField.getText().trim());
        book.setTotalCopies((Integer) totalCopiesSpinner.getValue());
        book.setAvailableCopies((Integer) totalCopiesSpinner.getValue());
        
        // Check if book with ISBN already exists
        if (bookDAO.getBookByIsbn(book.getIsbn()) != null) {
            JOptionPane.showMessageDialog(this, "A book with this ISBN already exists.", "Duplicate ISBN", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Add book to database
        if (bookDAO.addBook(book)) {
            JOptionPane.showMessageDialog(this, "Book added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadBooks();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editBook() {
        // Check if a row is selected
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get selected book
        String isbn = tableModel.getValueAt(selectedRow, 3).toString();
        Book book = bookDAO.getBookByIsbn(isbn);
        
        if (book == null) {
            JOptionPane.showMessageDialog(this, "Failed to load book details.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Populate form with book details
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        isbnField.setText(book.getIsbn());
        isbnField.setEnabled(false); // Disable ISBN field to prevent changes
        categoryField.setText(book.getCategory());
        totalCopiesSpinner.setValue(book.getTotalCopies());
        
        // Change add button to update
        addButton.setText("Update Book");
        addButton.removeActionListener(addButton.getActionListeners()[0]);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBook(book);
            }
        });
        
        // Add cancel button functionality to edit button
        editButton.setText("Cancel");
        editButton.removeActionListener(editButton.getActionListeners()[0]);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });
    }
    
    private void updateBook(Book book) {
        // Validate form
        if (!validateForm()) {
            return;
        }
        
        // Update book object
        book.setTitle(titleField.getText().trim());
        book.setAuthor(authorField.getText().trim());
        book.setCategory(categoryField.getText().trim());
        int newTotalCopies = (Integer) totalCopiesSpinner.getValue();
        
        // Check if total copies can be reduced
        if (newTotalCopies < book.getTotalCopies() && 
            (book.getTotalCopies() - book.getAvailableCopies()) > newTotalCopies) {
            JOptionPane.showMessageDialog(this, 
                "Cannot reduce total copies below the number of issued books.", 
                "Invalid Total Copies", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update available copies proportionally
        int issuedCopies = book.getTotalCopies() - book.getAvailableCopies();
        book.setTotalCopies(newTotalCopies);
        book.setAvailableCopies(newTotalCopies - issuedCopies);
        
        // Update book in database
        if (bookDAO.updateBook(book)) {
            JOptionPane.showMessageDialog(this, "Book updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadBooks();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteBook() {
        // Check if a row is selected
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get selected book's ISBN
        String isbn = tableModel.getValueAt(selectedRow, 3).toString();
        
        // Confirm deletion
        int option = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this book? This will also delete all issue records for this book.", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            // Delete book from database
            if (bookDAO.deleteBook(isbn)) {
                JOptionPane.showMessageDialog(this, "Book deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBooks();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete book.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateForm() {
        // Check if all fields are filled
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter book title.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            titleField.requestFocus();
            return false;
        }
        
        if (authorField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter author name.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            authorField.requestFocus();
            return false;
        }
        
        if (isbnField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter ISBN.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            isbnField.requestFocus();
            return false;
        }
        
        if (categoryField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter category.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            categoryField.requestFocus();
            return false;
        }
        
        // Validate ISBN format (simple validation)
        String isbn = isbnField.getText().trim();
        if (!isbn.matches("^[0-9-]{10,17}$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid ISBN (10-13 digits with optional hyphens).", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            isbnField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        categoryField.setText("");
        totalCopiesSpinner.setValue(1);
    }
    
    private void resetForm() {
        clearForm();
        isbnField.setEnabled(true);
        
        // Reset button text and listeners
        addButton.setText("Add Book");
        JButtonExtension.removeActionListeners(addButton);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });
        
        editButton.setText("Edit Book");
        JButtonExtension.removeActionListeners(editButton);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editBook();
            }
        });
    }
}

