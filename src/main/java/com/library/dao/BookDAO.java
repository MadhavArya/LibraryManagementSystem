package main.java.com.library.dao;

import main.java.com.library.db.DBConnection;
import main.java.com.library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private Connection connection;
    
    public BookDAO() {
        this.connection = DBConnection.getConnection();
    }
    
    public boolean addBook(Book book) {
        String query = "INSERT INTO books (title, author, isbn, category, total_copies, available_copies) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getIsbn());
            statement.setString(4, book.getCategory());
            statement.setInt(5, book.getTotalCopies());
            statement.setInt(6, book.getAvailableCopies());
            
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            return false;
        }
    }
    
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            while (resultSet.next()) {
                Book book = new Book();
                book.setId(resultSet.getInt("id"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
                book.setIsbn(resultSet.getString("isbn"));
                book.setCategory(resultSet.getString("category"));
                book.setTotalCopies(resultSet.getInt("total_copies"));
                book.setAvailableCopies(resultSet.getInt("available_copies"));
                
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving books: " + e.getMessage());
        }
        
        return books;
    }
    
    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + searchTerm + "%");
            statement.setString(2, "%" + searchTerm + "%");
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Book book = new Book();
                    book.setId(resultSet.getInt("id"));
                    book.setTitle(resultSet.getString("title"));
                    book.setAuthor(resultSet.getString("author"));
                    book.setIsbn(resultSet.getString("isbn"));
                    book.setCategory(resultSet.getString("category"));
                    book.setTotalCopies(resultSet.getInt("total_copies"));
                    book.setAvailableCopies(resultSet.getInt("available_copies"));
                    
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        }
        
        return books;
    }
    
    public Book getBookByIsbn(String isbn) {
        String query = "SELECT * FROM books WHERE isbn = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, isbn);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Book book = new Book();
                    book.setId(resultSet.getInt("id"));
                    book.setTitle(resultSet.getString("title"));
                    book.setAuthor(resultSet.getString("author"));
                    book.setIsbn(resultSet.getString("isbn"));
                    book.setCategory(resultSet.getString("category"));
                    book.setTotalCopies(resultSet.getInt("total_copies"));
                    book.setAvailableCopies(resultSet.getInt("available_copies"));
                    return book;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving book: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean updateBook(Book book) {
        String query = "UPDATE books SET title = ?, author = ?, category = ?, total_copies = ?, available_copies = ? WHERE isbn = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());
            statement.setInt(4, book.getTotalCopies());
            statement.setInt(5, book.getAvailableCopies());
            statement.setString(6, book.getIsbn());
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteBook(String isbn) {
        String query = "DELETE FROM books WHERE isbn = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, isbn);
            
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateBookAvailability(String isbn, boolean increase) {
        String query = "UPDATE books SET available_copies = available_copies " + (increase ? "+ 1" : "- 1") + 
                      " WHERE isbn = ? AND " + (increase ? "available_copies < total_copies" : "available_copies > 0");
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, isbn);
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book availability: " + e.getMessage());
            return false;
        }
    }
}
