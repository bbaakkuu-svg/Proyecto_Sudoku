package com.sudoku.elite;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/** Singleton Database Connection Manager with Connection Pooling. */
public class DatabaseConnection {
    private static HikariDataSource dataSource;
    private static boolean offlineMode = false;

    static {
        Properties props = new Properties();
        try (InputStream input =
                DatabaseConnection.class
                        .getClassLoader()
                        .getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Sorry, unable to find config.properties");
            } else {
                props.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        HikariConfig config = new HikariConfig();

        // Detect environment (Testing with H2 or Production with MySQL)
        String useH2 = System.getProperty("useH2", "false");

        if (Boolean.parseBoolean(useH2)) {
            config.setJdbcUrl("jdbc:h2:mem:sudoku_db;DB_CLOSE_DELAY=-1;MODE=MySQL");
            config.setDriverClassName("org.h2.Driver");
            config.setUsername("sa");
            config.setPassword("");
        } else {
            config.setJdbcUrl(props.getProperty("db.url", "jdbc:mysql://localhost:3306/sudoku_db"));
            config.setDriverClassName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
            config.setUsername(props.getProperty("db.user", "root"));
            config.setPassword(props.getProperty("db.password", ""));
        }

        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(3000); // 3 seconds timeout
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        try {
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            System.err.println(
                    "Database connection failed. Falling back to offline mode (H2 in-memory).");
            HikariConfig fallbackConfig = new HikariConfig();
            fallbackConfig.setJdbcUrl("jdbc:h2:mem:sudoku_offline;DB_CLOSE_DELAY=-1;MODE=MySQL");
            fallbackConfig.setDriverClassName("org.h2.Driver");
            fallbackConfig.setUsername("sa");
            fallbackConfig.setPassword("");
            fallbackConfig.setMaximumPoolSize(5);
            dataSource = new HikariDataSource(fallbackConfig);
            offlineMode = true;
        }
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

    public static boolean isOfflineMode() {
        return offlineMode;
    }
}
