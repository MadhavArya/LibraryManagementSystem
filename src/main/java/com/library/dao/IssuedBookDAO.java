package main.java.com.library.dao;

import main.java.com.library.db.DBConnection;
import main.java.com.library.model.Book;
import main.java.com.library.model.IssuedBook;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IssuedBookDAO {
    private Connection connection;
    private BookDAO bookDAO;
    
    public IssuedBookDAO() {
        this.connection = DBConnection.getConnection();
        this.bookDAO = new BookDAO();
    }
    
    public boolean issueBook(IssuedBook issuedBook) {
        // First check if the book is available
        Book book = bookDAO.getBookByIsbn(issuedBook.getIsbn());
        if (book == null || book.getAvailableCopies() <= 0) {
            return false;
        }
        
        // If available, update the available copies
        if (!bookDAO.updateBookAvailability(issuedBook.getIsbn(), false)) {
            return false;
        }
        
        // Then issue the book
        String query = "INSERT INTO issued_books (student_name, isbn, issue_date, return_date) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, issuedBook.getStudentName());
            statement.setString(2, issuedBook.getIsbn());
            statement.setDate(3, Date.valueOf(issuedBook.getIssueDate()));
            statement.setDate(4, issuedBook.getReturnDate() != null ? Date.valueOf(issuedBook.getReturnDate()) : null);
            
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error issuing book: " + e.getMessage());
            // If there was an error, revert the book availability
            bookDAO.updateBookAvailability(issuedBook.getIsbn(), true);
            return false;
        }
    }
    
    public boolean returnBook(int issuedBookId) {
        // First get the issued book details
        IssuedBook issuedBook = getIssuedBookById(issuedBookId);
        if (issuedBook == null || issuedBook.getReturnDate() != null) {
            return false;
        }
        
        // Update the return date
        String query = "UPDATE issued_books SET return_date = ? WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(LocalDate.now()));
            statement.setInt(2, issuedBookId);
            
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                // If successful, update the book availability
                return bookDAO.updateBookAvailability(issuedBook.getIsbn(), true);
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            return false;
        }
    }
    // In IssuedBookDAO.java

    // Method to fetch currently issued books (books with no return date)
    public List<IssuedBook> getCurrentlyIssuedBooks() {
        List<IssuedBook> issuedBooks = new ArrayList<>();
        String query = "SELECT ib.*, b.title FROM issued_books ib " +
                "JOIN books b ON ib.isbn = b.isbn " +
                "WHERE ib.return_date IS NULL " +
                "ORDER BY ib.issue_date DESC";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                IssuedBook issuedBook = new IssuedBook();
                issuedBook.setId(resultSet.getInt("id"));
                issuedBook.setStudentName(resultSet.getString("student_name"));
                issuedBook.setIsbn(resultSet.getString("isbn"));
                issuedBook.setBookTitle(resultSet.getString("title"));

                Date issueDate = resultSet.getDate("issue_date");
                if (issueDate != null) {
                    issuedBook.setIssueDate(issueDate.toLocalDate());
                }

                issuedBooks.add(issuedBook);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving currently issued books: " + e.getMessage());
        }

        return issuedBooks;
    }

    // Method to fetch returned books (books with a return date)
    public List<IssuedBook> getReturnedBooks() {
        List<IssuedBook> returnedBooks = new ArrayList<>();
        String query = "SELECT ib.*, b.title FROM issued_books ib " +
                "JOIN books b ON ib.isbn = b.isbn " +
                "WHERE ib.return_date IS NOT NULL " +
                "ORDER BY ib.issue_date DESC";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                IssuedBook issuedBook = new IssuedBook();
                issuedBook.setId(resultSet.getInt("id"));
                issuedBook.setStudentName(resultSet.getString("student_name"));
                issuedBook.setIsbn(resultSet.getString("isbn"));
                issuedBook.setBookTitle(resultSet.getString("title"));

                Date issueDate = resultSet.getDate("issue_date");
                if (issueDate != null) {
                    issuedBook.setIssueDate(issueDate.toLocalDate());
                }

                Date returnDate = resultSet.getDate("return_date");
                if (returnDate != null) {
                    issuedBook.setReturnDate(returnDate.toLocalDate());
                }

                returnedBooks.add(issuedBook);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving returned books: " + e.getMessage());
        }

        return returnedBooks;
    }

    public IssuedBook getIssuedBookById(int id) {
        String query = "SELECT ib.*, b.title FROM issued_books ib " +
                      "JOIN books b ON ib.isbn = b.isbn " +
                      "WHERE ib.id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    IssuedBook issuedBook = new IssuedBook();
                    issuedBook.setId(resultSet.getInt("id"));
                    issuedBook.setStudentName(resultSet.getString("student_name"));
                    issuedBook.setIsbn(resultSet.getString("isbn"));
                    issuedBook.setBookTitle(resultSet.getString("title"));
                    
                    Date issueDate = resultSet.getDate("issue_date");
                    if (issueDate != null) {
                        issuedBook.setIssueDate(issueDate.toLocalDate());
                    }
                    
                    Date returnDate = resultSet.getDate("return_date");
                    if (returnDate != null) {
                        issuedBook.setReturnDate(returnDate.toLocalDate());
                    }
                    
                    return issuedBook;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving issued book: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<IssuedBook> getAllIssuedBooks() {
        List<IssuedBook> issuedBooks = new ArrayList<>();
        String query = "SELECT ib.*, b.title FROM issued_books ib " +
                      "JOIN books b ON ib.isbn = b.isbn " +
                      "ORDER BY ib.issue_date DESC";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            while (resultSet.next()) {
                IssuedBook issuedBook = new IssuedBook();
                issuedBook.setId(resultSet.getInt("id"));
                issuedBook.setStudentName(resultSet.getString("student_name"));
                issuedBook.setIsbn(resultSet.getString("isbn"));
                issuedBook.setBookTitle(resultSet.getString("title"));
                
                Date issueDate = resultSet.getDate("issue_date");
                if (issueDate != null) {
                    issuedBook.setIssueDate(issueDate.toLocalDate());
                }
                
                Date returnDate = resultSet.getDate("return_date");
                if (returnDate != null) {
                    issuedBook.setReturnDate(returnDate.toLocalDate());
                }
                
                issuedBooks.add(issuedBook);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving issued books: " + e.getMessage());
        }
        
        return issuedBooks;
    }
    public boolean addIssuedBook(IssuedBook issuedBook) {
        // First, check if the book is available
        Book book = bookDAO.getBookByIsbn(issuedBook.getIsbn());
        if (book == null || book.getAvailableCopies() <= 0) {
            return false;  // Book is not available
        }

        // Insert the issued book into the database
        String query = "INSERT INTO issued_books (student_name, isbn, issue_date, return_date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, issuedBook.getStudentName());
            statement.setString(2, issuedBook.getIsbn());
            statement.setDate(3, Date.valueOf(issuedBook.getIssueDate()));
            statement.setDate(4, issuedBook.getReturnDate() != null ? Date.valueOf(issuedBook.getReturnDate()) : null);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                // Update the available copies of the book
                return bookDAO.updateBookAvailability(issuedBook.getIsbn(), false);  // Mark the book as unavailable
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error issuing book: " + e.getMessage());
            return false;
        }
    }

    public List<IssuedBook> getOverdueBooks() {
        List<IssuedBook> overdueBooks = new ArrayList<>();
        // Get books that were issued more than 14 days ago and have not been returned
        String query = "SELECT ib.*, b.title FROM issued_books ib " +
                      "JOIN books b ON ib.isbn = b.isbn " +
                      "WHERE ib.return_date IS NULL AND ib.issue_date <= ? " +
                      "ORDER BY ib.issue_date";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            LocalDate cutoffDate = LocalDate.now().minusDays(14);
            statement.setDate(1, Date.valueOf(cutoffDate));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    IssuedBook issuedBook = new IssuedBook();
                    issuedBook.setId(resultSet.getInt("id"));
                    issuedBook.setStudentName(resultSet.getString("student_name"));
                    issuedBook.setIsbn(resultSet.getString("isbn"));
                    issuedBook.setBookTitle(resultSet.getString("title"));
                    
                    Date issueDate = resultSet.getDate("issue_date");
                    if (issueDate != null) {
                        issuedBook.setIssueDate(issueDate.toLocalDate());
                    }
                    
                    overdueBooks.add(issuedBook);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving overdue books: " + e.getMessage());
        }
        
        return overdueBooks;
    }
}
