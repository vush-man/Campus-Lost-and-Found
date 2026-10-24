package lostfound;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class LoginFrame extends JFrame {
    private static final Color NAVY = new Color(0x1E3A5F);
    private final UserDao users = new UserDao();
    private final JTextField email = new JTextField(22);
    private final JPasswordField password = new JPasswordField(22);

    public LoginFrame() {
        super("Campus Lost and Found - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 310);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(22, 28, 22, 28));
        JLabel heading = new JLabel("Campus Lost and Found", SwingConstants.CENTER);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 21f));
        heading.setForeground(NAVY);
        root.add(heading, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 12));
        form.add(new JLabel("Email")); form.add(email);
        form.add(new JLabel("Password")); form.add(password);
        root.add(form, BorderLayout.CENTER);
        JButton login = new JButton("Log in");
        JButton register = new JButton("Create student account");
        style(login); style(register);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actions.add(login); actions.add(register);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);
        login.addActionListener(e -> login());
        register.addActionListener(e -> register());
        getRootPane().setDefaultButton(login);
    }

    private void login() {
        String mail = email.getText().trim();
        String pass = new String(password.getPassword());
        if (mail.isBlank() || pass.isBlank()) { message("Enter your email and password."); return; }
        setBusy(true);
        new SwingWorker<User, Void>() {
            @Override protected User doInBackground() throws Exception { return users.login(mail, pass); }
            @Override protected void done() {
                setBusy(false);
                try {
                    User user = get();
                    if (user == null) { message("Incorrect email or password."); return; }
                    dispose(); new MainFrame(user).setVisible(true);
                } catch (InterruptedException ex) { Thread.currentThread().interrupt(); message("Login was interrupted."); }
                catch (ExecutionException ex) { showError(ex.getCause()); }
            }
        }.execute();
    }

    private void register() {
        JTextField name = new JTextField(20);
        JTextField mail = new JTextField(20);
        JPasswordField pass = new JPasswordField(20);
        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.add(new JLabel("Name")); panel.add(name);
        panel.add(new JLabel("Email")); panel.add(mail);
        panel.add(new JLabel("Password")); panel.add(pass);
        if (JOptionPane.showConfirmDialog(this, panel, "Create student account", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        String n = name.getText().trim(), m = mail.getText().trim(), p = new String(pass.getPassword());
        if (n.isBlank() || m.isBlank() || p.isBlank()) { message("Complete all fields."); return; }
        setBusy(true);
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception { users.register(n, m, p); return null; }
            @Override protected void done() {
                setBusy(false);
                try { get(); email.setText(m); password.setText(""); message("Account created. You can now log in."); }
                catch (InterruptedException ex) { Thread.currentThread().interrupt(); showError(ex); }
                catch (ExecutionException ex) { showError(ex.getCause()); }
            }
        }.execute();
    }

    private void setBusy(boolean busy) { email.setEnabled(!busy); password.setEnabled(!busy); }
    private void message(String text) { JOptionPane.showMessageDialog(this, text); }
    private void showError(Throwable error) { message("Database error: " + error.getMessage()); }
    static void style(AbstractButton button) { button.setBackground(NAVY); button.setForeground(Color.WHITE); }
}
