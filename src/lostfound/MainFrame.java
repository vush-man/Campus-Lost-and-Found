package lostfound;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame(User user) {
        super("Campus Lost and Found - " + user.getName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Browse", new BrowsePanel(user));
        tabs.addTab("Report", new ReportPanel(user));
        if (user.isAdmin()) tabs.addTab("Claims", new AdminPanel());
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(0xF2F4F7));
        content.setBorder(new EmptyBorder(10, 12, 12, 12));
        JLabel welcome = new JLabel("Signed in as " + user.getName() + " (" + user.getRole() + ")");
        welcome.setBorder(new EmptyBorder(4, 4, 10, 4));
        JButton logout = new JButton("Log out");
        LoginFrame.style(logout);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(4, 4, 10, 4));
        header.add(welcome, BorderLayout.CENTER);
        header.add(logout, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);
        content.add(tabs, BorderLayout.CENTER);
        setContentPane(content);
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }
}
