package lostfound;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Opens MySQL connections using environment variables or local defaults. */
public final class Db {
    private static final String URL = setting("LOSTFOUND_DB_URL",
            "jdbc:mysql://localhost:3306/campus_lost_found?serverTimezone=UTC");
    private static final String USER = setting("LOSTFOUND_DB_USER", "root");
    private static final String PASSWORD = setting("LOSTFOUND_DB_PASSWORD", "2608");

    private Db() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String setting(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
