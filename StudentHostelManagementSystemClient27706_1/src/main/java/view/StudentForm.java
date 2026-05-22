package view;

import controller.StudentController;
import model.Student;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Full CRUD form for managing students.
 */
public class StudentForm extends JFrame {

    private final StudentController controller = new StudentController();

    // Table
    private JTable table;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField studentIdField, firstNameField, lastNameField,
                       emailField, phoneField, nationalIdField, passwordField;
    private JComboBox<String> genderCombo;
    private JLabel statusLabel;

    // Track selected row's DB id
    private Long selectedDbId = null;

    public StudentForm() {
        initUI();
        loadStudents();
    }

    private void initUI() {
        setTitle("Student Management");
        setSize(1000, 640);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ── Top toolbar ───────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setBackground(new Color(30, 58, 95));

        JLabel heading = new JLabel("  👩‍🎓  Student Management");
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        toolbar.add(heading);

        // ── Table ─────────────────────────────────────────────────────
        String[] cols = {"DB ID", "Student ID", "First Name", "Last Name", "Email", "Phone", "Gender", "National ID", "Active"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(30, 58, 95));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setGridColor(new Color(220, 225, 235));
        table.setSelectionBackground(new Color(210, 228, 255));
        // Hide DB ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromTable();
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // ── Refresh / search toolbar ───────────────────────────────────
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchBar.setBackground(new Color(245, 247, 250));
        JTextField searchField = new JTextField(20);
        searchField.setToolTipText("Search by name or student ID");
        JButton searchBtn = new JButton("🔍 Search");
        JButton refreshBtn = new JButton("↺ Refresh");
        styleSmallButton(searchBtn, new Color(30, 58, 95));
        styleSmallButton(refreshBtn, new Color(100, 110, 130));
        searchBtn.addActionListener(e -> filterTable(searchField.getText()));
        refreshBtn.addActionListener(e -> { searchField.setText(""); loadStudents(); });
        searchBar.add(new JLabel("Search:"));
        searchBar.add(searchField);
        searchBar.add(searchBtn);
        searchBar.add(refreshBtn);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(searchBar, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        // ── Right form panel ──────────────────────────────────────────
        JPanel formPanel = buildFormPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, formPanel);
        split.setDividerLocation(620);
        split.setResizeWeight(0.65);
        split.setDividerSize(4);

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
            new MatteBorder(0, 1, 0, 0, new Color(210, 220, 235)),
            new EmptyBorder(16, 18, 16, 18)
        ));
        panel.setPreferredSize(new Dimension(360, 0));

        JLabel title = new JLabel("Student Details");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(new Color(30, 58, 95));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        studentIdField  = formField();
        firstNameField  = formField();
        lastNameField   = formField();
        emailField      = formField();
        phoneField      = formField();
        nationalIdField = formField();
        passwordField   = formField();
        genderCombo     = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        genderCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Buttons
        JButton addBtn    = new JButton("➕ Add Student");
        JButton updateBtn = new JButton("✏️ Update");
        JButton deleteBtn = new JButton("🗑 Delete");
        JButton clearBtn  = new JButton("✕ Clear");

        styleSmallButton(addBtn,    new Color(46, 160, 67));
        styleSmallButton(updateBtn, new Color(30, 58, 95));
        styleSmallButton(deleteBtn, new Color(200, 50, 50));
        styleSmallButton(clearBtn,  new Color(120, 130, 145));

        addBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        updateBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        deleteBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        clearBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        deleteBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        addBtn.addActionListener(e    -> doAdd());
        updateBtn.addActionListener(e -> doUpdate());
        deleteBtn.addActionListener(e -> doDelete());
        clearBtn.addActionListener(e  -> clearForm());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(14));
        panel.add(lbl("Student ID *")); panel.add(studentIdField);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lbl("First Name *")); panel.add(firstNameField);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lbl("Last Name *"));  panel.add(lastNameField);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lbl("Email *"));      panel.add(emailField);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lbl("Phone"));        panel.add(phoneField);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lbl("National ID"));  panel.add(nationalIdField);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lbl("Gender"));       panel.add(genderCombo);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lbl("Password *"));   panel.add(passwordField);
        panel.add(Box.createVerticalStrut(16));
        panel.add(addBtn);
        panel.add(Box.createVerticalStrut(5));
        panel.add(updateBtn);
        panel.add(Box.createVerticalStrut(5));
        panel.add(deleteBtn);
        panel.add(Box.createVerticalStrut(5));
        panel.add(clearBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(statusLabel);

        return panel;
    }

    // ── Data operations ──────────────────────────────────────────────────
    private void loadStudents() {
        SwingWorker<List<Student>, Void> w = new SwingWorker<>() {
            protected List<Student> doInBackground() throws Exception {
                return controller.getAllStudents();
            }
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    for (Student s : get()) {
                        tableModel.addRow(new Object[]{
                            s.getId(), s.getStudentId(), s.getFirstName(), s.getLastName(),
                            s.getEmail(), s.getPhoneNumber(), s.getGender(),
                            s.getNationalId(), s.isIsActive() ? "Yes" : "No"
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentForm.this, "Load error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    private void doAdd() {
        if (!validateForm()) return;
        Student s = buildStudentFromForm(null);
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            protected Boolean doInBackground() throws Exception { return controller.addStudent(s); }
            protected void done() {
                try {
                    if (get()) { showStatus("Student added successfully.", true); clearForm(); loadStudents(); }
                    else         showStatus("Failed to add student.", false);
                } catch (Exception ex) { showStatus("Error: " + ex.getMessage(), false); }
            }
        };
        w.execute();
    }

    private void doUpdate() {
        if (selectedDbId == null) { showStatus("Select a student to update.", false); return; }
        if (!validateForm()) return;
        Student s = buildStudentFromForm(selectedDbId);
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            protected Boolean doInBackground() throws Exception { return controller.updateStudent(s); }
            protected void done() {
                try {
                    if (get()) { showStatus("Student updated.", true); loadStudents(); }
                    else         showStatus("Update failed.", false);
                } catch (Exception ex) { showStatus("Error: " + ex.getMessage(), false); }
            }
        };
        w.execute();
    }

    private void doDelete() {
        if (selectedDbId == null) { showStatus("Select a student to delete.", false); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            protected Boolean doInBackground() throws Exception { return controller.deleteStudent(selectedDbId); }
            protected void done() {
                try {
                    if (get()) { showStatus("Student deleted.", true); clearForm(); loadStudents(); }
                    else         showStatus("Delete failed.", false);
                } catch (Exception ex) { showStatus("Error: " + ex.getMessage(), false); }
            }
        };
        w.execute();
    }

    // ── UI helpers ────────────────────────────────────────────────────────
    private void populateFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedDbId = (Long) tableModel.getValueAt(row, 0);
        studentIdField.setText(str(tableModel.getValueAt(row, 1)));
        firstNameField.setText(str(tableModel.getValueAt(row, 2)));
        lastNameField.setText(str(tableModel.getValueAt(row, 3)));
        emailField.setText(str(tableModel.getValueAt(row, 4)));
        phoneField.setText(str(tableModel.getValueAt(row, 5)));
        genderCombo.setSelectedItem(tableModel.getValueAt(row, 6));
        nationalIdField.setText(str(tableModel.getValueAt(row, 7)));
        passwordField.setText("");
    }

    private void filterTable(String query) {
        String q = query.toLowerCase().trim();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        if (q.isEmpty()) { sorter.setRowFilter(null); return; }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + q));
    }

    private boolean validateForm() {
        if (studentIdField.getText().trim().isEmpty() || firstNameField.getText().trim().isEmpty()
                || lastNameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
            showStatus("Please fill in all required (*) fields.", false);
            return false;
        }
        return true;
    }

    private Student buildStudentFromForm(Long id) {
       return new Student(id,
    studentIdField.getText().trim(),
    firstNameField.getText().trim(),
    lastNameField.getText().trim(),
    emailField.getText().trim(),
    phoneField.getText().trim(),
    (String) genderCombo.getSelectedItem(),
    nationalIdField.getText().trim(),
    passwordField.getText().isEmpty() ? null : passwordField.getText(),
    true     
);
    }

    private void clearForm() {
        selectedDbId = null;
        studentIdField.setText(""); firstNameField.setText(""); lastNameField.setText("");
        emailField.setText(""); phoneField.setText(""); nationalIdField.setText("");
        passwordField.setText(""); genderCombo.setSelectedIndex(0);
        table.clearSelection();
        statusLabel.setText(" ");
    }

    private void showStatus(String msg, boolean ok) {
        statusLabel.setForeground(ok ? new Color(30, 140, 60) : new Color(200, 50, 50));
        statusLabel.setText(msg);
    }

    private JTextField formField() {
        JTextField f = new JTextField();
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(190, 205, 225), 1, true),
            new EmptyBorder(3, 7, 3, 7)
        ));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l.setForeground(new Color(80, 90, 110));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleSmallButton(JButton b, Color bg) {
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }
}
