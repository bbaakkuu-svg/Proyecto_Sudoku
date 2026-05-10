package com.sudoku.elite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Game sessions and Rankings.
 */
public class GameDAO {

    public void saveGame(int userId, String boardData, String difficulty, int score) throws SQLException {
        String sql = "INSERT INTO games (user_id, board_data, difficulty, score) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, boardData);
            stmt.setString(3, difficulty);
            stmt.setInt(4, score);
            stmt.executeUpdate();
        }
    }

    public void registerRanking(int userId, int score) throws SQLException {
        String sql = "INSERT INTO rankings (user_id, score) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, score);
            stmt.executeUpdate();
        }
    }

    public List<String> getTopRankings() throws SQLException {
        List<String> results = new ArrayList<>();
        String sql = "SELECT u.username, r.score FROM rankings r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "ORDER BY r.score DESC LIMIT 10";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(rs.getString("username") + ": " + rs.getInt("score"));
            }
        }
        return results;
    }
}
