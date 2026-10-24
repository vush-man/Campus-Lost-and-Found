package lostfound;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ItemDao {
    private static final String COLUMNS = "id,type,title,description,category,location,event_date,status,reported_by,claimed_by,claim_note";

    public void add(String type, String title, String description, String category,
                    String location, Date eventDate, int reporterId) throws SQLException {
        String sql = "INSERT INTO items(type,title,description,category,location,event_date,reported_by) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = Db.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, type); p.setString(2, title); p.setString(3, description);
            p.setString(4, category); p.setString(5, location); p.setDate(6, eventDate);
            p.setInt(7, reporterId); p.executeUpdate();
        }
    }

    public List<Item> search(String text, String type) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM items WHERE (? = 'ALL' OR type = ?) "
                + "AND (title LIKE ? OR description LIKE ? OR category LIKE ? OR location LIKE ?) ORDER BY event_date DESC, id DESC";
        String query = "%" + text.trim() + "%";
        try (Connection c = Db.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, type); p.setString(2, type);
            for (int i = 3; i <= 6; i++) p.setString(i, query);
            try (ResultSet r = p.executeQuery()) { return readItems(r); }
        }
    }

    public boolean claim(int itemId, int studentId, String note) throws SQLException {
        String sql = "UPDATE items SET status='CLAIMED', claimed_by=?, claim_note=? WHERE id=? AND status='OPEN' AND reported_by<>?";
        try (Connection c = Db.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, studentId); p.setString(2, note.trim()); p.setInt(3, itemId); p.setInt(4, studentId);
            return p.executeUpdate() == 1;
        }
    }

    public List<Item> listClaimed() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM items WHERE status='CLAIMED' ORDER BY id DESC";
        try (Connection c = Db.getConnection(); PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) { return readItems(r); }
    }

    public boolean markReturned(int itemId) throws SQLException {
        String sql = "UPDATE items SET status='RETURNED' WHERE id=? AND status='CLAIMED'";
        try (Connection c = Db.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement p = c.prepareStatement(sql)) {
                p.setInt(1, itemId);
                boolean changed = p.executeUpdate() == 1;
                c.commit();
                return changed;
            } catch (SQLException exception) {
                c.rollback();
                throw exception;
            }
        }
    }

    public boolean rejectClaim(int itemId) throws SQLException {
        String sql = "UPDATE items SET status='OPEN', claimed_by=NULL, claim_note=NULL WHERE id=? AND status='CLAIMED'";
        try (Connection c = Db.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, itemId); return p.executeUpdate() == 1;
        }
    }

    private List<Item> readItems(ResultSet r) throws SQLException {
        List<Item> items = new ArrayList<>();
        while (r.next()) {
            int claimer = r.getInt("claimed_by");
            Integer claimedBy = r.wasNull() ? null : claimer;
            items.add(new Item(r.getInt("id"), r.getString("type"), r.getString("title"),
                    r.getString("description"), r.getString("category"), r.getString("location"),
                    r.getDate("event_date").toLocalDate(), r.getString("status"),
                    r.getInt("reported_by"), claimedBy, r.getString("claim_note")));
        }
        return items;
    }
}
