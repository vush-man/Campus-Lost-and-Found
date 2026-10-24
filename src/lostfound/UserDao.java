package lostfound;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    public User login(String email, String password) throws SQLException {
        String sql = "SELECT id, name, email, role FROM users WHERE email = ? AND password = ?";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email.trim());
            statement.setString(2, hashPassword(password));
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) return null;
                return new User(result.getInt("id"), result.getString("name"),
                        result.getString("email"), result.getString("role"));
            }
        }
    }

    public void register(String name, String email, String password) throws SQLException {
        String sql = "INSERT INTO users(name, email, password, role) VALUES (?, ?, ?, 'STUDENT')";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name.trim());
            statement.setString(2, email.trim());
            statement.setString(3, hashPassword(password));
            statement.executeUpdate();
        }
    }

    private static String hashPassword(String password) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256")
                    .digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte value : bytes) hex.append(String.format("%02x", value & 0xff));
            return hex.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
