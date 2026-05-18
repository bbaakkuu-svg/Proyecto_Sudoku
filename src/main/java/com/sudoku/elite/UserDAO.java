package com.sudoku.elite;

import java.sql.*;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

/** Data Access Object for User management. */
public class UserDAO {

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Creates a new user in the database.
     *
     * @param username the unique username
     * @param password the password (unhashed for this phase)
     * @return the generated user ID, or -1 if failed
     * @throws SQLException if a database error occurs
     */
    public int createUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt =
                        conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
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
     *
     * @param username the username
     * @param password the password
     * @return an Optional containing the user ID if authentication succeeds
     * @throws SQLException if a database error occurs
     */
    public Optional<Integer> login(String username, String password) throws SQLException {
        String sql = "SELECT id, password_hash FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (BCrypt.checkpw(password, storedHash)) {
                        return Optional.of(rs.getInt("id"));
                    }
                }
            }
        }
        return Optional.empty();
    }
}
