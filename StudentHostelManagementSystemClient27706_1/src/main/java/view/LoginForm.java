package view;

import controller.LoginController;
import model.Staff;
import model.Student;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import rmi.ServiceLocator;

/**
 * Login form with role selection (Staff / Student) and OTP-aware flow.
 * After credential validation the server may require an OTP; if so
 * an OTP verification panel slides in automatically.
 */
public class LoginForm extends JFrame {
 
    // ── Credential panel ──────────────────────────────────────────────────
    private JTextField     idField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JButton        loginButton;
    private JLabel         statusLabel;
 
    // ── OTP panel ─────────────────────────────────────────────────────────
    private JPanel       otpPanel;
    private JTextField[] otpBoxes;       // 6 single-digit boxes
    private JButton      verifyButton;
    private JButton      resendButton;
    private JLabel       otpStatusLabel;
    private JLabel       otpEmailHint;
 
    // ── State ─────────────────────────────────────────────────────────────
    private String pendingEmail;
    private String pendingRole;
 
    private final LoginController controller = new LoginController();
 
    public LoginForm() {
        initUI();
    }
 
    // ── UI construction ───────────────────────────────────────────────────
 
    private void initUI() {
        setTitle("Hostel Management – Login");
        setSize(440, 540);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
 
        JPanel root = new JPanel(new CardLayout());
        root.setBackground(new Color(240, 244, 250));
        root.add(buildCredentialPanel(), "login");
        root.add(buildOtpPanel(),        "otp");
        setContentPane(root);
    }
 
    private JPanel buildCredentialPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(new Color(240, 244, 250));
 
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(210, 220, 235), 1, true),
            new EmptyBorder(35, 40, 30, 40)
        ));
        card.setPreferredSize(new Dimension(360, 460));
 
        JLabel icon = new JLabel("🏠", SwingConstants.CENTER);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel title = new JLabel("Hostel Management", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(30, 58, 95));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel sub = new JLabel("Sign in to continue", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(Color.GRAY);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        roleCombo = new JComboBox<>(new String[]{"Staff", "Student"});
        styleCombo(roleCombo);
 
        idField = new JTextField();
        idField.setToolTipText("Email (Staff) or Student ID (Student)");
        styleField(idField);
 
        passwordField = new JPasswordField();
        styleField(passwordField);
 
        loginButton = new JButton("Sign In");
        styleButton(loginButton, new Color(30, 58, 95));
        loginButton.addActionListener(e -> doLogin());
 
        // "Register here" link for new students
        JButton registerBtn = new JButton("New student? Register here");
        registerBtn.setContentAreaFilled(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setForeground(new Color(30, 100, 200));
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> new StudentForm().setVisible(true));
 
        KeyAdapter enter = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        };
        idField.addKeyListener(enter);
        passwordField.addKeyListener(enter);
 
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(200, 50, 50));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        card.add(icon);
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(22));
        card.add(labelFor("Role"));
        card.add(Box.createVerticalStrut(4));
        card.add(roleCombo);
        card.add(Box.createVerticalStrut(12));
        card.add(labelFor("Email / Student ID"));
        card.add(Box.createVerticalStrut(4));
        card.add(idField);
        card.add(Box.createVerticalStrut(12));
        card.add(labelFor("Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(20));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(6));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(statusLabel);
 
        outer.add(card);
        return outer;
    }
 
    private JPanel buildOtpPanel() {
        otpPanel = new JPanel(new GridBagLayout());
        otpPanel.setBackground(new Color(240, 244, 250));
 
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(210, 220, 235), 1, true),
            new EmptyBorder(35, 40, 30, 40)
        ));
        card.setPreferredSize(new Dimension(380, 420));
 
        JLabel icon = new JLabel("📧", SwingConstants.CENTER);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel title = new JLabel("Verify OTP", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(30, 58, 95));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        otpEmailHint = new JLabel("Enter the 6-digit code sent to your email", SwingConstants.CENTER);
        otpEmailHint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        otpEmailHint.setForeground(Color.GRAY);
        otpEmailHint.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        // 6 digit boxes with auto-advance
        JPanel boxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        boxPanel.setOpaque(false);
        boxPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        otpBoxes = new JTextField[6];
        for (int i = 0; i < 6; i++) {
            otpBoxes[i] = new JTextField(1);
            otpBoxes[i].setFont(new Font("SansSerif", Font.BOLD, 22));
            otpBoxes[i].setHorizontalAlignment(SwingConstants.CENTER);
            otpBoxes[i].setPreferredSize(new Dimension(42, 50));
            otpBoxes[i].setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 205, 225), 1, true),
                new EmptyBorder(2, 2, 2, 2)
            ));
            final int idx = i;
            otpBoxes[i].addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    String txt = otpBoxes[idx].getText();
                    if (!txt.isEmpty() && idx < 5) {
                        otpBoxes[idx].setText(txt.substring(0, 1));
                        otpBoxes[idx + 1].requestFocus();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && txt.isEmpty() && idx > 0) {
                        otpBoxes[idx - 1].requestFocus();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) doVerifyOtp();
                }
            });
            boxPanel.add(otpBoxes[i]);
        }
 
        verifyButton = new JButton("Verify OTP");
        styleButton(verifyButton, new Color(46, 160, 67));
        verifyButton.addActionListener(e -> doVerifyOtp());
 
        resendButton = new JButton("Resend OTP");
        resendButton.setFocusPainted(false);
        resendButton.setContentAreaFilled(false);
        resendButton.setBorderPainted(false);
        resendButton.setForeground(new Color(30, 100, 200));
        resendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resendButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resendButton.addActionListener(e -> doResendOtp());
 
        JButton backBtn = new JButton("← Back to Login");
        backBtn.setFocusPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setForeground(Color.GRAY);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> showPanel("login"));
 
        otpStatusLabel = new JLabel(" ", SwingConstants.CENTER);
        otpStatusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        otpStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        card.add(icon);
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(otpEmailHint);
        card.add(Box.createVerticalStrut(24));
        card.add(boxPanel);
        card.add(Box.createVerticalStrut(20));
        card.add(verifyButton);
        card.add(Box.createVerticalStrut(10));
        card.add(resendButton);
        card.add(Box.createVerticalStrut(4));
        card.add(backBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(otpStatusLabel);
 
        otpPanel.add(card);
        return otpPanel;
    }
 
    // ── Login logic ───────────────────────────────────────────────────────
 
    private void doLogin() {
        String id   = idField.getText().trim();
        String pwd  = new String(passwordField.getPassword());
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
                    // Credentials valid — extract email and trigger OTP
                    String email = "Staff".equals(role)
                        ? ((Staff)   result).getEmail()
                        : ((Student) result).getEmail();
                    pendingEmail = email;
                    pendingRole  = role;
                    doSendOtp(email, role.toUpperCase());
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    showStatus(statusLabel, "Error: " + msg, false);
                }
            }
        }.execute();
    }
 
    /**
     * Calls HostelService.sendOtp() via RMI.
     * On success, switches the card layout to the OTP panel.
     */
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
                    get();  // surface any exception
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
 
    /**
     * Reads the 6 digit boxes and calls HostelService.verifyOtp() via RMI.
     */
    private void doVerifyOtp() {
        StringBuilder code = new StringBuilder();
        for (JTextField b : otpBoxes) code.append(b.getText().trim());
        if (code.length() < 6) {
            showStatus(otpStatusLabel, "Please enter the full 6-digit code.", false);
            return;
        }
 
        verifyButton.setEnabled(false);
        verifyButton.setText("Verifying…");
 
        final String otpCode = code.toString();
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
 
    // ── Helpers ───────────────────────────────────────────────────────────
 
    private void showPanel(String name) {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), name);
    }
 
    private void clearOtpBoxes() {
        for (JTextField b : otpBoxes) b.setText("");
        if (otpBoxes.length > 0) otpBoxes[0].requestFocus();
    }
 
    private void showStatus(JLabel lbl, String msg, boolean ok) {
        lbl.setForeground(ok ? new Color(30, 140, 60) : new Color(200, 50, 50));
        lbl.setText(msg);
    }
 
    private JLabel labelFor(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(new Color(60, 70, 90));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
 
    private void styleField(JTextField f) {
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(190, 205, 225), 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
 
    private void styleCombo(JComboBox<String> c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        c.setFont(new Font("SansSerif", Font.PLAIN, 14));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
 
    private void styleButton(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}