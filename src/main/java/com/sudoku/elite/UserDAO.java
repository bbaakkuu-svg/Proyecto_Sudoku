package com.sudoku.elite;

import java.sql.*;
import java.util.Optional;

/**
 * Data Access Object for User management.
 */
public class UserDAO {
    
    /**
     * Creates a new user in the database.
     * @param username the unique username
     * @param password the password (unhashed for this phase)
     * @return the generated user ID, or -1 if failed
     * @throws SQLException if a database error occurs
     */
    public int createUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // Note: Should be hashed in production
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    /**
     * Authenticates a user and retrieves their ID.
     * @param username the username
     * @param password the password
     * @return an Optional containing the user ID if authentication succeeds
     * @throws SQLException if a database error occurs
     */
    public Optional<Integer> login(String username, String password) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ? AND password_hash = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("id"));
                }
            }
        }
        return Optional.empty();
    }
}
