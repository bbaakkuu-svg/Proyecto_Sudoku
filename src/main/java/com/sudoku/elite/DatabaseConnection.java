package com.sudoku.elite;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Singleton Database Connection Manager with Connection Pooling.
 */
public class DatabaseConnection {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        
        // Detect environment (Testing with H2 or Production with MySQL)
        String useH2 = System.getProperty("useH2", "false");
        
        if (Boolean.parseBoolean(useH2)) {
            config.setJdbcUrl("jdbc:h2:mem:sudoku_db;DB_CLOSE_DELAY=-1;MODE=MySQL");
            config.setDriverClassName("org.h2.Driver");
            config.setUsername("sa");
            config.setPassword("");
        } else {
            config.setJdbcUrl("jdbc:mysql://localhost:3306/sudoku_db?useSSL=false&serverTimezone=UTC");
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setUsername("root"); // Default for dev environment
            config.setPassword("");     // Default for dev environment
        }

        config.setMaximumPoolSize(10);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
