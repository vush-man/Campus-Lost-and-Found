package lostfound;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ItemTableModel extends AbstractTableModel {
    private final String[] columns = {"Type", "Title", "Category", "Location", "Date", "Status"};
    private List<Item> items = new ArrayList<>();

    public void setItems(List<Item> items) { this.items = new ArrayList<>(items); fireTableDataChanged(); }
    public Item getItemAt(int row) { return items.get(row); }
    @Override public int getRowCount() { return items.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }
    @Override public Object getValueAt(int row, int column) {
        Item item = items.get(row);
        return switch (column) {
            case 0 -> item.getType(); case 1 -> item.getTitle(); case 2 -> item.getCategory();
            case 3 -> item.getLocation(); case 4 -> item.getEventDate(); case 5 -> item.getStatus();
            default -> "";
        };
    }
}
