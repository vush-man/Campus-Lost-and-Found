package lostfound;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ReportPanel extends JPanel {
    private final User user;
    private final ItemDao dao = new ItemDao();
    private final JComboBox<String> type = new JComboBox<>(new String[]{"LOST", "FOUND"});
    private final JTextField title = new JTextField(28);
    private final JTextArea description = new JTextArea(5, 28);
    private final JComboBox<String> category = new JComboBox<>(new String[]{
            "Electronics", "Clothing", "Books and stationery", "Keys", "Wallets and bags",
            "ID cards", "Jewelry", "Accessories", "Other"
    });
    private final JComboBox<String> location = new JComboBox<>(new String[]{
            "Library", "Cafeteria", "Classroom", "Laboratory", "Hostel",
            "Parking lot", "Sports ground", "Administrative office", "Other"
    });
    private final JTextField date = new JTextField(LocalDate.now().toString(), 28);
    private final JButton submit = new JButton("Submit report");

    public ReportPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(18, 18, 18, 18));
        category.setEditable(true);
        location.setEditable(true);
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6); c.anchor = GridBagConstraints.WEST;
        c.gridx = 0; c.gridy = 0; addRow(form, c, "Item type", type);
        c.gridy++; addRow(form, c, "Title", title);
        c.gridy++; addRow(form, c, "Description", new JScrollPane(description));
        c.gridy++; addRow(form, c, "Category", category);
        c.gridy++; addRow(form, c, "Location", location);
        c.gridy++; addRow(form, c, "Date (YYYY-MM-DD)", date);
        LoginFrame.style(submit);
        c.gridy++; c.gridx = 1; c.anchor = GridBagConstraints.EAST; form.add(submit, c);
        add(form, BorderLayout.NORTH);
        submit.addActionListener(e -> submitReport());
    }

    private void addRow(JPanel panel, GridBagConstraints c, String label, Component field) {
        c.gridx = 0; c.weightx = 0; panel.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL; panel.add(field, c);
        c.fill = GridBagConstraints.NONE;
    }

    private void submitReport() {
        String itemTitle = title.getText().trim();
        String itemDescription = description.getText().trim();
        String itemCategory = ((JTextField) category.getEditor().getEditorComponent()).getText().trim();
        String itemLocation = ((JTextField) location.getEditor().getEditorComponent()).getText().trim();
        LocalDate eventDate;
        try { eventDate = LocalDate.parse(date.getText().trim()); }
        catch (DateTimeParseException ex) { message("Enter the date in YYYY-MM-DD format."); return; }
        if (itemTitle.isBlank() || itemDescription.isBlank() || itemCategory.isBlank() || itemLocation.isBlank()) {
            message("Complete all report fields."); return;
        }
        submit.setEnabled(false);
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                dao.add((String) type.getSelectedItem(), itemTitle, itemDescription, itemCategory,
                        itemLocation, Date.valueOf(eventDate), user.getId());
                return null;
            }
            @Override protected void done() {
                submit.setEnabled(true);
                try {
                    get(); message("Item report added."); title.setText(""); description.setText("");
                    category.setSelectedIndex(0); location.setSelectedIndex(0); date.setText(LocalDate.now().toString());
                } catch (InterruptedException ex) { Thread.currentThread().interrupt(); message("Save was interrupted."); }
                catch (java.util.concurrent.ExecutionException ex) { message("Database error: " + ex.getCause().getMessage()); }
            }
        }.execute();
    }

    private void message(String text) { JOptionPane.showMessageDialog(this, text); }
}
