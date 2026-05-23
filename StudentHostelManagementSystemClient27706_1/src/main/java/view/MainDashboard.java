package view;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {

    private final String loggedInUser;
    private final String userRole;

    public MainDashboard(String email, String role) {
        this.loggedInUser = email;
        this.userRole = role;
        initUI();
    }

    private void initUI() {
        setTitle("Student Hostel Management System");
        UiUtil.configureFrame(this, 900, 600, 720, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 58, 95));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Student Hostel Management System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JLabel userLabel = new JLabel(
                "Logged in as: " + loggedInUser + "  |  Role: " + userRole + "   ");
        userLabel.setForeground(new Color(180, 210, 240));
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            rmi.ServiceLocator.reset();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(userLabel);
        rightPanel.add(logoutBtn);

        header.add(title, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        boolean isStudent = "Student".equalsIgnoreCase(userRole);

        JPanel grid = new JPanel(new GridLayout(
                isStudent ? 1 : 2,
                3,
                20,
                20
        ));

        grid.setBackground(new Color(240, 244, 250));
        grid.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        if (isStudent) {
            grid.add(makeCard("🛏  Rooms",
                    "View available hostel rooms",
                    new Color(46, 204, 113),
                    () -> new RoomForm().setVisible(true)));

            grid.add(makeCard("📋  Bookings",
                    "Create and view your bookings",
                    new Color(155, 89, 182),
                    () -> new BookingForm().setVisible(true)));

            grid.add(makeCard("💳  Payments",
                    "View or make your payments",
                    new Color(230, 126, 34),
                    () -> new PaymentForm().setVisible(true)));

        } else {
            grid.add(makeCard("👩‍🎓  Students",
                    "Manage student records",
                    new Color(52, 152, 219),
                    () -> new StudentForm().setVisible(true)));

            grid.add(makeCard("🛏  Rooms",
                    "View & manage hostel rooms",
                    new Color(46, 204, 113),
                    () -> new RoomForm().setVisible(true)));

            grid.add(makeCard("📋  Bookings",
                    "Create & track bookings",
                    new Color(155, 89, 182),
                    () -> new BookingForm().setVisible(true)));

            grid.add(makeCard("💳  Payments",
                    "Record & view payments",
                    new Color(230, 126, 34),
                    () -> new PaymentForm().setVisible(true)));

            grid.add(makeCard("👥  Staff",
                    "Manage staff members",
                    new Color(231, 76, 60),
                    () -> new StaffForm().setVisible(true)));

            grid.add(makeCard("📊  Reports",
                    "Export system reports",
                    new Color(22, 160, 133),
                    () -> new ReportsForm().setVisible(true)));
        }

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(220, 225, 235));
        statusBar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        JLabel status = new JLabel("Connected to server  |  Ready");
        status.setForeground(Color.DARK_GRAY);
        status.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusBar.add(status);

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(grid, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel makeCard(String title, String subtitle, Color accent, Runnable action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel stripe = new JPanel();
        stripe.setBackground(accent);
        stripe.setPreferredSize(new Dimension(0, 5));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setForeground(new Color(30, 40, 60));

        JLabel sub = new JLabel("<html><small>" + subtitle + "</small></html>");
        sub.setForeground(Color.GRAY);

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);
        text.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        text.add(lbl);
        text.add(Box.createVerticalStrut(5));
        text.add(sub);

        JButton openBtn = new JButton("Open →");
        openBtn.setFocusPainted(false);
        openBtn.setBackground(accent);
        openBtn.setForeground(Color.WHITE);
        openBtn.setBorderPainted(false);
        openBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openBtn.addActionListener(e -> action.run());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottom.setOpaque(false);
        bottom.add(openBtn);

        card.add(stripe, BorderLayout.NORTH);
        card.add(text, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(248, 250, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE);
            }

            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });

        return card;
    }
}