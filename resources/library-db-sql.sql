-- Create database
CREATE DATABASE IF NOT EXISTS library_management;
USE library_management;

-- Create tables
CREATE TABLE IF NOT EXISTS admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    category VARCHAR(100) NOT NULL,
    total_copies INT NOT NULL DEFAULT 1,
    available_copies INT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS issued_books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_name VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    issue_date DATE NOT NULL,
    return_date DATE,
    FOREIGN KEY (isbn) REFERENCES books(isbn) ON DELETE CASCADE
);

-- Insert test data
-- Default admin with username: admin, password: admin123
INSERT INTO admins (username, password) VALUES ('admin', 'admin123');

-- Sample books
INSERT INTO books (title, author, isbn, category, total_copies, available_copies) 
VALUES 
('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'Fiction', 5, 5),
('To Kill a Mockingbird', 'Harper Lee', '9780061120084', 'Fiction', 3, 3),
('1984', 'George Orwell', '9780451524935', 'Science Fiction', 4, 4),
('Pride and Prejudice', 'Jane Austen', '9780141439518', 'Romance', 2, 2),
('The Hobbit', 'J.R.R. Tolkien', '9780547928227', 'Fantasy', 3, 3);

-- No issued books initially
