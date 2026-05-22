package view;

import controller.StudentController;
import model.Student;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Focused registration dialog for new students (from login screen).
 */
public class StudentRegistrationDialog extends JDialog {

    private final StudentController controller = new StudentController();

    private JTextField studentIdField, firstNameField, lastNameField, emailField, phoneField, nationalIdField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> genderCombo;
    private JLabel statusLabel;

    public StudentRegistrationDialog(Window owner) {
        super(owner, "Student Registration", ModalityType.APPLICATION_MODAL);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UiUtil.PANEL_BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiUtil.NAVY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Create your student account");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.add(title, BorderLayout.CENTER);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new CompoundBorder(
            new LineBorder(new Color(210, 220, 235), 1, true),
            new EmptyBorder(20, 24, 20, 24)
        ));

        studentIdField = UiUtil.styledField();
        firstNameField = UiUtil.styledField();
        lastNameField = UiUtil.styledField();
        emailField = UiUtil.styledField();
        phoneField = UiUtil.styledField();
        nationalIdField = UiUtil.styledField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();
        stylePassword(passwordField);
        stylePassword(confirmPasswordField);
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        genderCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        addRow(form, "Student ID *", studentIdField);
        addRow(form, "First name *", firstNameField);
        addRow(form, "Last name *", lastNameField);
        addRow(form, "Email *", emailField);
        addRow(form, "Phone", phoneField);
        addRow(form, "National ID", nationalIdField);
        addRow(form, "Gender", genderCombo);
        addRow(form, "Password *", passwordField);
        addRow(form, "Confirm password *", confirmPasswordField);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(Box.createVerticalStrut(8));
        form.add(statusLabel);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttons.setBackground(Color.WHITE);
        JButton cancelBtn = new JButton("Cancel");
        JButton registerBtn = new JButton("Register");
        cancelBtn.addActionListener(e -> dispose());
        registerBtn.addActionListener(e -> doRegister());
        UiUtil.stylePrimaryButton(registerBtn, new Color(46, 160, 67));
        buttons.add(cancelBtn);
        buttons.add(registerBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        UiUtil.configureDialog(this, 480, 620, 420, 520);
        setLocationRelativeTo(getOwner());
        getRootPane().setDefaultButton(registerBtn);
    }

    private void addRow(JPanel form, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(new Color(80, 90, 110));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (field instanceof JTextField tf) {
            tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        }
        form.add(lbl);
        form.add(Box.createVerticalStrut(4));
        form.add(field);
        form.add(Box.createVerticalStrut(10));
    }

    private void stylePassword(JPasswordField f) {
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190, 205, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    private void doRegister() {
        if (!validateInput()) return;

        Student s = new Student(
            null,
            studentIdField.getText().trim(),
            firstNameField.getText().trim(),
            lastNameField.getText().trim(),
            emailField.getText().trim(),
            UiUtil.blankToNull(phoneField.getText()),
            (String) genderCombo.getSelectedItem(),
            UiUtil.blankToNull(nationalIdField.getText()),
            new String(passwordField.getPassword()),
            true
        );

        setEnabled(false);
        statusLabel.setForeground(new Color(100, 110, 130));
        statusLabel.setText("Registering...");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return controller.addStudent(s);
            }

            @Override
            protected void done() {
                setEnabled(true);
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(StudentRegistrationDialog.this,
                            "Registration successful. You can now sign in.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        showError("Registration failed. Please try again.");
                    }
                } catch (Exception ex) {
                    showError(ex.getMessage() != null ? ex.getMessage() : "Registration failed.");
                }
            }
        }.execute();
    }

    private boolean validateInput() {
        if (studentIdField.getText().trim().isEmpty()
                || firstNameField.getText().trim().isEmpty()
                || lastNameField.getText().trim().isEmpty()
                || emailField.getText().trim().isEmpty()) {
            showError("Please fill in all required (*) fields.");
            return false;
        }
        String email = emailField.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            return false;
        }
        char[] pw = passwordField.getPassword();
        char[] cpw = confirmPasswordField.getPassword();
        if (pw.length == 0) {
            showError("Password is required.");
            return false;
        }
        if (!String.valueOf(pw).equals(String.valueOf(cpw))) {
            showError("Passwords do not match.");
            return false;
        }
        return true;
    }

    private void showError(String msg) {
        statusLabel.setForeground(UiUtil.ERROR);
        statusLabel.setText(msg);
    }
}
