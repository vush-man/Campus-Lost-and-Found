package lostfound;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AdminPanel extends JPanel {
    private final ItemDao dao = new ItemDao();
    private final DefaultListModel<Item> model = new DefaultListModel<>();
    private final JList<Item> claims = new JList<>(model);
    private final JButton returned = new JButton("Mark Returned");
    private final JButton reject = new JButton("Reject claim");

    public AdminPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        claims.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        claims.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Item item) setText("#" + item.getId() + "  " + item.getTitle() + " — " + item.getClaimNote());
                return this;
            }
        });
        add(new JScrollPane(claims), BorderLayout.CENTER);
        LoginFrame.style(returned); LoginFrame.style(reject);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refresh = new JButton("Refresh");
        actions.add(refresh); actions.add(reject); actions.add(returned);
        add(actions, BorderLayout.SOUTH);
        refresh.addActionListener(e -> load());
        returned.addActionListener(e -> update(true));
        reject.addActionListener(e -> update(false));
        load();
    }

    private void load() {
        setButtons(false);
        new SwingWorker<List<Item>, Void>() {
            @Override protected List<Item> doInBackground() throws Exception { return dao.listClaimed(); }
            @Override protected void done() {
                try { model.clear(); for (Item item : get()) model.addElement(item); }
                catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                catch (ExecutionException ex) { message("Database error: " + ex.getCause().getMessage()); }
                setButtons(true);
            }
        }.execute();
    }

    private void update(boolean markReturned) {
        Item item = claims.getSelectedValue();
        if (item == null) { message("Select a claim first."); return; }
        setButtons(false);
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception {
                return markReturned ? dao.markReturned(item.getId()) : dao.rejectClaim(item.getId());
            }
            @Override protected void done() {
                try { if (!get()) message("This claim has already changed."); load(); }
                catch (Exception ex) { message("Database error: " + (ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage())); setButtons(true); }
            }
        }.execute();
    }

    private void setButtons(boolean enabled) { returned.setEnabled(enabled); reject.setEnabled(enabled); }
    private void message(String text) { JOptionPane.showMessageDialog(this, text); }
}
