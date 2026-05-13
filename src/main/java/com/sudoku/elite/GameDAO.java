package com.sudoku.elite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Game sessions and Rankings.
 */
public class GameDAO {

    /**
     * Persists the current state of a game.
     * @param userId the owner of the game
     * @param boardData serialized string representing the board
     * @param difficulty difficulty level string
     * @param score current game score
     * @throws SQLException if a database error occurs
     */
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

    /**
     * Records a new score in the global rankings.
     * @param userId the user who achieved the score
     * @param score the numerical score
     * @throws SQLException if a database error occurs
     */
    public void registerRanking(int userId, int score) throws SQLException {
        String sql = "INSERT INTO rankings (user_id, score) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, score);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves the top 10 scores from the rankings table.
     * @return a list of strings formatted as "username: score"
     * @throws SQLException if a database error occurs
     */
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
