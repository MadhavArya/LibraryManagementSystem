
// IssuedBook.java
package main.java.com.library.model;

import java.time.LocalDate;

public class IssuedBook {
    private int id;
    private String studentName;
    private String isbn;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private String bookTitle; // To display in tables

    public IssuedBook() {
    }

    public IssuedBook(int id, String studentName, String isbn, LocalDate issueDate, LocalDate returnDate) {
        this.id = id;
        this.studentName = studentName;
        this.isbn = isbn;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    
    public boolean isReturned() {
        return returnDate != null;
    }
    
    public boolean isOverdue() {
        return !isReturned() && issueDate.plusDays(14).isBefore(LocalDate.now());
    }
}
