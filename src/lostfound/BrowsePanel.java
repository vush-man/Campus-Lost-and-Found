package lostfound;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutionException;

public class BrowsePanel extends JPanel {
    private final User user;
    private final ItemDao dao = new ItemDao();
    private final JTextField search = new JTextField(24);
    private final JComboBox<String> filter = new JComboBox<>(new String[]{"ALL", "LOST", "FOUND"});
    private final ItemTableModel model = new ItemTableModel();
    private final JTable table = new JTable(model);
    private final JButton claim = new JButton("Claim selected item");
    private final JButton refresh = new JButton("Refresh");

    public BrowsePanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Search")); controls.add(search);
        controls.add(new JLabel("Type")); controls.add(filter);
        LoginFrame.style(claim); controls.add(claim);
        controls.add(refresh);
        add(controls, BorderLayout.NORTH);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.setDefaultRenderer(Object.class, new StatusRenderer());
        add(new JScrollPane(table), BorderLayout.CENTER);
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { load(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { load(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { load(); }
        });
        filter.addActionListener(e -> load());
        refresh.addActionListener(e -> load());
        claim.addActionListener(e -> claimSelected());
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) showDetails(); }
        });
        load();
    }

    private void load() {
        String text = search.getText();
        String type = (String) filter.getSelectedItem();
        claim.setEnabled(false);
        new SwingWorker<java.util.List<Item>, Void>() {
            @Override protected java.util.List<Item> doInBackground() throws Exception { return dao.search(text, type); }
            @Override protected void done() {
                try { model.setItems(get()); }
                catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                catch (ExecutionException ex) { error(ex.getCause()); }
                claim.setEnabled(!user.isAdmin());
            }
        }.execute();
    }

    private void showDetails() {
        int row = table.getSelectedRow(); if (row < 0) return;
        Item item = model.getItemAt(table.convertRowIndexToModel(row));
        JOptionPane.showMessageDialog(this, item.getDescription(), item.getTitle(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void claimSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an item first."); return; }
        Item item = model.getItemAt(table.convertRowIndexToModel(row));
        if (!"OPEN".equals(item.getStatus())) { JOptionPane.showMessageDialog(this, "Only open items can be claimed."); return; }
        if (item.getReportedBy() == user.getId()) { JOptionPane.showMessageDialog(this, "You cannot claim your own report."); return; }
        String note = JOptionPane.showInputDialog(this, "Add a short note to support your claim:");
        if (note == null) return;
        if (note.isBlank()) { JOptionPane.showMessageDialog(this, "Enter a short claim note."); return; }
        claim.setEnabled(false);
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception { return dao.claim(item.getId(), user.getId(), note); }
            @Override protected void done() {
                claim.setEnabled(true);
                try { if (!get()) JOptionPane.showMessageDialog(BrowsePanel.this, "This item is no longer claimable."); else JOptionPane.showMessageDialog(BrowsePanel.this, "Claim submitted."); load(); }
                catch (Exception ex) { error(ex.getCause() == null ? ex : ex.getCause()); }
            }
        }.execute();
    }

    private void error(Throwable error) { JOptionPane.showMessageDialog(this, "Database error: " + error.getMessage()); }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            if (!selected) {
                String status = String.valueOf(table.getModel().getValueAt(table.convertRowIndexToModel(row), 5));
                component.setBackground("CLAIMED".equals(status) ? new Color(0xFFF4CC)
                        : "RETURNED".equals(status) ? new Color(0xDDF2E1) : Color.WHITE);
            }
            return component;
        }
    }
}
