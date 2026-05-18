package com.sudoku.elite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Data Access Object for Game sessions and Rankings. */
public class GameDAO {

    /**
     * Persists the current state of a game.
     *
     * @param userId the owner of the game
     * @param board current board state
     * @param solution solution board state
     * @param fixed fixed cells bitstring
     * @param difficulty difficulty level
     * @throws SQLException if a database error occurs
     */
    public void saveGame(int userId, String board, String solution, String fixed, String difficulty)
            throws SQLException {
        // First, check if a game already exists for this user to update it, or just insert new
        String checkSql = "SELECT id FROM games WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, userId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    String updateSql =
                            "UPDATE games SET board_data = ?, solution_data = ?, fixed_data = ?, difficulty = ? WHERE user_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, board);
                        updateStmt.setString(2, solution);
                        updateStmt.setString(3, fixed);
                        updateStmt.setString(4, difficulty);
                        updateStmt.setInt(5, userId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    String insertSql =
                            "INSERT INTO games (user_id, board_data, solution_data, fixed_data, difficulty, score) VALUES (?, ?, ?, ?, ?, 0)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setString(2, board);
                        insertStmt.setString(3, solution);
                        insertStmt.setString(4, fixed);
                        insertStmt.setString(5, difficulty);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }

    /**
     * Retrieves the saved game for a user.
     *
     * @param userId the user ID
     * @return a ResultSet-like object or a Map with game data
     * @throws SQLException if a database error occurs
     */
    public java.util.Map<String, String> getSavedGame(int userId) throws SQLException {
        String sql =
                "SELECT board_data, solution_data, fixed_data, difficulty FROM games WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    java.util.Map<String, String> data = new java.util.HashMap<>();
                    data.put("board", rs.getString("board_data"));
                    data.put("solution", rs.getString("solution_data"));
                    data.put("fixed", rs.getString("fixed_data"));
                    data.put("difficulty", rs.getString("difficulty"));
                    return data;
                }
            }
        }
        return null;
    }

    /**
     * Records a new score in the global rankings.
     *
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
     *
     * @return a list of strings formatted as "username: score"
     * @throws SQLException if a database error occurs
     */
    public List<String> getTopRankings() throws SQLException {
        List<String> results = new ArrayList<>();
        String sql =
                "SELECT u.username, r.score FROM rankings r "
                        + "JOIN users u ON r.user_id = u.id "
                        + "ORDER BY r.score DESC LIMIT 10";
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
