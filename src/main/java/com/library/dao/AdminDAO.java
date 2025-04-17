package main.java.com.library.dao;

import main.java.com.library.db.DBConnection;
import main.java.com.library.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    public boolean adminExists(Connection connection) {
        String query = "SELECT COUNT(*) FROM admins";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if admin exists: " + e.getMessage());
        }

        return false;
    }

    public void addAdmin(Connection connection, Admin admin) {
        String query = "INSERT INTO admins (username, password) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, admin.getUsername());
            statement.setString(2, admin.getPassword());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding admin: " + e.getMessage());
        }
    }

    public boolean validateLogin(Connection connection, String username, String password) {
        String query = "SELECT * FROM admins WHERE username = ? AND password = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // Returns true if admin exists
            }
        } catch (SQLException e) {
            System.err.println("Error validating login: " + e.getMessage());
            return false;
        }
    }

    public Admin getAdmin(Connection connection, String username) {
        String query = "SELECT * FROM admins WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Admin admin = new Admin();
                    admin.setId(resultSet.getInt("id"));
                    admin.setUsername(resultSet.getString("username"));
                    admin.setPassword(resultSet.getString("password"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting admin: " + e.getMessage());
        }

        return null;
    }
}
