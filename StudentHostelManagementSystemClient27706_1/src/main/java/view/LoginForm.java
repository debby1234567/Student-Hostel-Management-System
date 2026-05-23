package view;

import controller.LoginController;
import model.Staff;
import model.Student;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import rmi.ServiceLocator;

public class LoginForm extends JFrame {

    private JTextField idField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JButton loginButton;
    private JLabel statusLabel;

    private JPanel otpPanel;
    private JTextField[] otpBoxes;
    private JButton verifyButton;
    private JButton resendButton;
    private JLabel otpStatusLabel;
    private JLabel otpEmailHint;

    private String pendingEmail;
    private String pendingRole;

    private final LoginController controller = new LoginController();

    private static final Color PRIMARY = new Color(25, 45, 85);
    private static final Color SECONDARY = new Color(50, 95, 160);
    private static final Color BG = new Color(245, 248, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(35, 45, 60);
    private static final Color MUTED = new Color(110, 120, 135);
    private static final Color BORDER = new Color(220, 228, 238);
    private static final Color SUCCESS = new Color(35, 150, 80);
    private static final Color DANGER = new Color(200, 50, 50);

    public LoginForm() {
        initUI();
    }

    private void initUI() {
        setTitle("Hostel Management - Login");
        UiUtil.configureFrame(this, 950, 600, 750, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new CardLayout());
        root.setBackground(BG);
        root.add(buildCredentialPanel(), "login");
        root.add(buildOtpPanel(), "otp");
        setContentPane(root);
    }

    private JPanel buildCredentialPanel() {
        JPanel main = new JPanel(new GridLayout(1, 2));
        main.setBackground(BG);

        main.add(buildBrandPanel("Hostel Management",
                "Secure login for staff and students",
                "01  Choose your role",
                "02  Enter your credentials",
                "03  Verify OTP",
                "04  Access your dashboard"));

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(BG);
        right.setBorder(new EmptyBorder(40, 55, 40, 55));

        JPanel card = createCard(430, 460);

        JLabel title = titleLabel("Welcome back");
        JLabel sub = subtitleLabel("Sign in to continue to your dashboard");

        roleCombo = new JComboBox<>(new String[]{"Staff", "Student"});
        styleCombo(roleCombo);

        idField = new JTextField();
        idField.setToolTipText("Email (Staff) or Student ID (Student)");
        styleField(idField);

        passwordField = new JPasswordField();
        styleField(passwordField);

        loginButton = new JButton("Sign In");
        styleButton(loginButton, PRIMARY);
        loginButton.addActionListener(e -> doLogin());

        JButton registerBtn = linkButton("New student? Register here");
        registerBtn.addActionListener(e -> new StudentRegistrationDialog(this).setVisible(true));

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        KeyAdapter enter = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        };
        idField.addKeyListener(enter);
        passwordField.addKeyListener(enter);

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));

        card.add(labelFor("Role"));
        card.add(Box.createVerticalStrut(6));
        card.add(roleCombo);
        card.add(Box.createVerticalStrut(16));

        card.add(labelFor("Email / Student ID"));
        card.add(Box.createVerticalStrut(6));
        card.add(idField);
        card.add(Box.createVerticalStrut(16));

        card.add(labelFor("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(24));

        card.add(loginButton);
        card.add(Box.createVerticalStrut(12));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);

        right.add(card);
        main.add(right);

        return main;
    }

    private JPanel buildOtpPanel() {
        JPanel main = new JPanel(new GridLayout(1, 2));
        main.setBackground(BG);

        main.add(buildBrandPanel("OTP Verification",
                "Protecting your hostel account",
                "01  OTP sent to email",
                "02  Enter 6-digit code",
                "03  Verify securely",
                "04  Continue to dashboard"));

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(BG);
        right.setBorder(new EmptyBorder(40, 55, 40, 55));

        otpPanel = right;

        JPanel card = createCard(470, 430);

        JLabel title = titleLabel("Verify OTP");
        otpEmailHint = subtitleLabel("Enter the 6-digit code sent to your email");

        JPanel boxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        boxPanel.setOpaque(false);
        boxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        otpBoxes = new JTextField[1];
        otpBoxes[0] = new JTextField();

        otpBoxes[0].setFont(new Font("Segoe UI", Font.BOLD, 26));
        otpBoxes[0].setHorizontalAlignment(SwingConstants.CENTER);
        otpBoxes[0].setPreferredSize(new Dimension(260, 55));
        otpBoxes[0].setMaximumSize(new Dimension(260, 55));
        otpBoxes[0].setToolTipText("Enter 6-digit OTP");
        otpBoxes[0].setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        otpBoxes[0].addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char ch = e.getKeyChar();

                if (!Character.isDigit(ch)) {
                    e.consume();
                    return;
                }

                if (otpBoxes[0].getText().length() >= 6) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doVerifyOtp();
                }
            }
        });

        boxPanel.add(otpBoxes[0]);

        verifyButton = new JButton("Verify OTP");
        styleButton(verifyButton, SUCCESS);
        verifyButton.addActionListener(e -> doVerifyOtp());

        resendButton = linkButton("Resend OTP");
        resendButton.addActionListener(e -> doResendOtp());

        JButton backBtn = linkButton("← Back to Login");
        backBtn.setForeground(MUTED);
        backBtn.addActionListener(e -> showPanel("login"));

        otpStatusLabel = new JLabel(" ", SwingConstants.CENTER);
        otpStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        otpStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(otpEmailHint);
        card.add(Box.createVerticalStrut(32));
        card.add(boxPanel);
        card.add(Box.createVerticalStrut(28));
        card.add(verifyButton);
        card.add(Box.createVerticalStrut(12));
        card.add(resendButton);
        card.add(Box.createVerticalStrut(6));
        card.add(backBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(otpStatusLabel);

        right.add(card);
        main.add(right);

        return main;
    }

    private JPanel buildBrandPanel(String heading, String tagline,
                                   String s1, String s2, String s3, String s4) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PRIMARY);
        panel.setBorder(new EmptyBorder(60, 65, 60, 65));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("HM", SwingConstants.CENTER);
        logo.setFont(new Font("Serif", Font.BOLD, 30));
        logo.setForeground(WHITE);
        logo.setBorder(new LineBorder(new Color(180, 200, 230), 1));
        logo.setMaximumSize(new Dimension(75, 65));
        logo.setPreferredSize(new Dimension(75, 65));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel app = new JLabel("Hostel");
        app.setFont(new Font("Serif", Font.BOLD, 46));
        app.setForeground(WHITE);
        app.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel app2 = new JLabel("Management");
        app2.setFont(new Font("Serif", Font.BOLD, 38));
        app2.setForeground(WHITE);
        app2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel(tagline);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        desc.setForeground(new Color(210, 225, 245));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel line = new JPanel();
        line.setBackground(new Color(95, 145, 210));
        line.setMaximumSize(new Dimension(70, 4));
        line.setPreferredSize(new Dimension(70, 4));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel steps = new JLabel("<html>"
                + s1 + "<br><br>"
                + s2 + "<br><br>"
                + s3 + "<br><br>"
                + s4
                + "</html>");
        steps.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        steps.setForeground(new Color(210, 225, 245));
        steps.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(logo);
        content.add(Box.createVerticalStrut(28));
        content.add(app);
        content.add(app2);
        content.add(Box.createVerticalStrut(12));
        content.add(desc);
        content.add(Box.createVerticalStrut(30));
        content.add(line);
        content.add(Box.createVerticalStrut(35));
        content.add(steps);

        panel.add(content);
        return panel;
    }

    private JPanel createCard(int width, int height) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setPreferredSize(new Dimension(width, height));
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(36, 42, 34, 42)
        ));
        return card;
    }

    private JLabel titleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Serif", Font.BOLD, 32));
        lbl.setForeground(TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel subtitleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void doLogin() {
        String id = idField.getText().trim();
        String pwd = new String(passwordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (id.isEmpty() || pwd.isEmpty()) {
            showStatus(statusLabel, "Please fill in all fields.", false);
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Signing in…");
        statusLabel.setText(" ");

        new SwingWorker<Object, Void>() {
            protected Object doInBackground() throws Exception {
                return "Staff".equals(role)
                        ? controller.loginStaff(id, pwd)
                        : controller.loginStudent(id, pwd);
            }

            protected void done() {
                loginButton.setEnabled(true);
                loginButton.setText("Sign In");
                try {
                    Object result = get();
                    if (result == null) {
                        showStatus(statusLabel, "Invalid credentials. Please try again.", false);
                        return;
                    }

                    String email = "Staff".equals(role)
                            ? ((Staff) result).getEmail()
                            : ((Student) result).getEmail();

                    pendingEmail = email;
                    pendingRole = role;
                    doSendOtp(email, role.toUpperCase());

                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    showStatus(statusLabel, "Error: " + msg, false);
                }
            }
        }.execute();
    }

    private void doSendOtp(String email, String userType) {
        loginButton.setEnabled(false);
        loginButton.setText("Sending OTP…");

        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                ServiceLocator.getHostelService().sendOtp(email, userType);
                return null;
            }

            protected void done() {
                loginButton.setEnabled(true);
                loginButton.setText("Sign In");
                try {
                    get();
                    otpEmailHint.setText("Code sent to " + pendingEmail);
                    clearOtpBoxes();
                    showPanel("otp");
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    showStatus(statusLabel, "Could not send OTP: " + msg, false);
                }
            }
        }.execute();
    }

    private void doVerifyOtp() {
        String otpCode = otpBoxes[0].getText().trim();

        if (otpCode.length() != 6) {
            showStatus(otpStatusLabel, "OTP must be exactly 6 digits.", false);
            return;
        }

        if (!otpCode.matches("\\d{6}")) {
            showStatus(otpStatusLabel, "OTP must contain digits only.", false);
            return;
        }

        verifyButton.setEnabled(false);
        verifyButton.setText("Verifying…");

        new SwingWorker<Boolean, Void>() {
            protected Boolean doInBackground() throws Exception {
                return ServiceLocator.getHostelService().verifyOtp(pendingEmail, otpCode);
            }

            protected void done() {
                verifyButton.setEnabled(true);
                verifyButton.setText("Verify OTP");
                try {
                    if (get()) {
                        dispose();
                        SwingUtilities.invokeLater(() ->
                                new MainDashboard(pendingEmail, pendingRole).setVisible(true));
                    } else {
                        showStatus(otpStatusLabel, "Invalid or expired OTP. Try again.", false);
                        clearOtpBoxes();
                    }
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    showStatus(otpStatusLabel, "Error: " + msg, false);
                }
            }
        }.execute();
    }

    private void doResendOtp() {
        resendButton.setEnabled(false);
        doSendOtp(pendingEmail, pendingRole.toUpperCase());
        showStatus(otpStatusLabel, "A new OTP has been sent to your email.", true);
        clearOtpBoxes();

        Timer t = new Timer(4000, e -> resendButton.setEnabled(true));
        t.setRepeats(false);
        t.start();
    }

    private void showPanel(String name) {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), name);
    }

    private void clearOtpBoxes() {
        otpBoxes[0].setText("");
        otpBoxes[0].requestFocus();
    }

    private void showStatus(JLabel lbl, String msg, boolean ok) {
        lbl.setForeground(ok ? SUCCESS : DANGER);
        lbl.setText(msg);
    }

    private JLabel labelFor(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleField(JTextField f) {
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        f.setPreferredSize(new Dimension(360, 42));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(TEXT);
        f.setBackground(new Color(252, 253, 255));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(7, 12, 7, 12)
        ));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void styleCombo(JComboBox<String> c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        c.setPreferredSize(new Dimension(360, 42));
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.setBackground(WHITE);
        c.setForeground(TEXT);
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void styleButton(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setPreferredSize(new Dimension(360, 44));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JButton linkButton(String text) {
        JButton b = new JButton(text);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setForeground(SECONDARY);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        return b;
    }
}