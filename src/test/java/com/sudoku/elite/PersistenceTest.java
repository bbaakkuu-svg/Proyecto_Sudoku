package com.sudoku.elite;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Integration tests for DAOs using H2 database. */
public class PersistenceTest {

    @BeforeAll
    public static void setupDatabase() throws Exception {
        System.setProperty("useH2", "true");
        // Initialize schema in H2
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(
                    "CREATE TABLE users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) UNIQUE, password_hash VARCHAR(255))");
            stmt.execute(
                    "CREATE TABLE rankings (id INT AUTO_INCREMENT PRIMARY KEY, user_id INT, score INT)");
        }
    }

    @Test
    public void testUserCreationAndLogin() throws Exception {
        UserDAO userDAO = new UserDAO();
        int userId = userDAO.createUser("tester", "password123");
        assertTrue(userId > 0);

        var loginId = userDAO.login("tester", "password123");
        assertTrue(loginId.isPresent());
        assertEquals(userId, loginId.get());
    }

    @Test
    public void testRankings() throws Exception {
        UserDAO userDAO = new UserDAO();
        GameDAO gameDAO = new GameDAO();

        int userId = userDAO.createUser("ranker", "pass");
        gameDAO.registerRanking(userId, 1500);

        var tops = gameDAO.getTopRankings();
        assertFalse(tops.isEmpty());
        assertTrue(tops.get(0).contains("ranker") && tops.get(0).contains("1500"));
    }
}
