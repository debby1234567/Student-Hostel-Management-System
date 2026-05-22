package view;

import controller.RoomController;
import model.Room;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class RoomForm extends JFrame {

    private final RoomController controller = new RoomController();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField roomNumberField, capacityField, priceField;
    private JComboBox<String> typeCombo, statusCombo;
    private JLabel statusLabel;
    private Long selectedDbId = null;

    public RoomForm() {
        initUI();
        loadRooms();
    }

    private void initUI() {
        setTitle("Room Management");
        UiUtil.configureFrame(this, 950, 580, 760, 460);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setBackground(new Color(46, 120, 80));
        JLabel heading = new JLabel("  🛏  Room Management");
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        toolbar.add(heading);

        String[] cols = {"ID", "Room No.", "Type", "Capacity", "Price/Month", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table, new Color(46, 120, 80));
        hideIdColumn(table);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) populateForm(); });

        JPanel searchBar = buildSearchBar();

        JPanel left = new JPanel(new BorderLayout());
        left.add(searchBar, BorderLayout.NORTH);
        left.add(new JScrollPane(table), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, buildFormPanel());
        split.setDividerLocation(600);
        split.setDividerSize(4);

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        bar.setBackground(new Color(245, 247, 250));
        JTextField sf = new JTextField(20);
        JButton sb = new JButton("🔍 Search"); styleSmall(sb, new Color(46, 120, 80));
        JButton rb = new JButton("↺ Refresh"); styleSmall(rb, new Color(100, 110, 130));
        sb.addActionListener(e -> filterTable(sf.getText()));
        rb.addActionListener(e -> { sf.setText(""); loadRooms(); });
        bar.add(new JLabel("Search:")); bar.add(sf); bar.add(sb); bar.add(rb);
        return bar;
    }

    private JPanel buildFormPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(new MatteBorder(0,1,0,0,new Color(210,220,235)), new EmptyBorder(16,18,16,18)));

        JLabel title = new JLabel("Room Details");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(new Color(46, 120, 80));
        title.setAlignmentX(LEFT_ALIGNMENT);

        roomNumberField = field(); capacityField = field(); priceField = field();
        typeCombo   = combo(new String[]{"Single", "Double", "Triple", "Suite"});
        statusCombo = combo(new String[]{"AVAILABLE", "OCCUPIED", "MAINTENANCE"});

        JButton addBtn = btn("➕ Add Room",    new Color(46, 160, 67));
        JButton updBtn = btn("✏️ Update",      new Color(46, 120, 80));
        JButton delBtn = btn("🗑 Delete",      new Color(200, 50, 50));
        JButton clrBtn = btn("✕ Clear",        new Color(120, 130, 145));

        addBtn.addActionListener(e -> doAdd());
        updBtn.addActionListener(e -> doUpdate());
        delBtn.addActionListener(e -> doDelete());
        clrBtn.addActionListener(e -> clear());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);

        p.add(title); p.add(vgap(14));
        p.add(lbl("Room Number *")); p.add(roomNumberField); p.add(vgap(6));
        p.add(lbl("Type"));          p.add(typeCombo);       p.add(vgap(6));
        p.add(lbl("Capacity *"));    p.add(capacityField);   p.add(vgap(6));
        p.add(lbl("Price/Month *")); p.add(priceField);      p.add(vgap(6));
        p.add(lbl("Status"));        p.add(statusCombo);     p.add(vgap(16));
        p.add(addBtn); p.add(vgap(5)); p.add(updBtn); p.add(vgap(5));
        p.add(delBtn); p.add(vgap(5)); p.add(clrBtn); p.add(vgap(10));
        p.add(statusLabel);
        return p;
    }

    private void loadRooms() {
        new SwingWorker<List<Room>, Void>() {
            protected List<Room> doInBackground() throws Exception { return controller.getAllRooms(); }
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    for (Room r : get()) {
                        tableModel.addRow(new Object[]{
                            r.getId(), r.getRoomNumber(), r.getRoomType(),
                            r.getCapacity(), String.format("%.2f", r.getPricePerMonth()), r.getStatus()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RoomForm.this, "Load error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void doAdd() {
        if (roomNumberField.getText().trim().isEmpty() || capacityField.getText().trim().isEmpty() || priceField.getText().trim().isEmpty()) {
            stat("Fill all required fields.", false); return;
        }
        try {
            Room r = buildRoom(null);
            new SwingWorker<Boolean,Void>() {
                protected Boolean doInBackground() throws Exception { return controller.addRoom(r); }
                protected void done() { try { if(get()){stat("Room added.",true);clear();loadRooms();}else stat("Add failed.",false); } catch(Exception ex){stat("Error: "+ex.getMessage(),false);} }
            }.execute();
        } catch (NumberFormatException ex) { stat("Capacity and price must be numbers.", false); }
    }

    private void doUpdate() {
        if (selectedDbId == null) { stat("Select a room first.", false); return; }
        try {
            Room r = buildRoom(selectedDbId);
            new SwingWorker<Boolean,Void>() {
                protected Boolean doInBackground() throws Exception { return controller.updateRoom(r); }
                protected void done() { try { if(get()){stat("Room updated.",true);loadRooms();}else stat("Update failed.",false); } catch(Exception ex){stat("Error: "+ex.getMessage(),false);} }
            }.execute();
        } catch (NumberFormatException ex) { stat("Capacity and price must be numbers.", false); }
    }

    private void doDelete() {
        if (selectedDbId == null) { stat("Select a room first.", false); return; }
        if (JOptionPane.showConfirmDialog(this,"Delete this room?","Confirm",JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        new SwingWorker<Boolean,Void>() {
            protected Boolean doInBackground() throws Exception { return controller.deleteRoom(selectedDbId); }
            protected void done() { try { if(get()){stat("Room deleted.",true);clear();loadRooms();}else stat("Delete failed.",false); } catch(Exception ex){stat("Error: "+ex.getMessage(),false);} }
        }.execute();
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedDbId = (Long) tableModel.getValueAt(row, 0);
        roomNumberField.setText(str(tableModel.getValueAt(row, 1)));
        typeCombo.setSelectedItem(tableModel.getValueAt(row, 2));
        capacityField.setText(str(tableModel.getValueAt(row, 3)));
        priceField.setText(str(tableModel.getValueAt(row, 4)));
        statusCombo.setSelectedItem(tableModel.getValueAt(row, 5));
    }

    private Room buildRoom(Long id) {
        return new Room(id,
            roomNumberField.getText().trim(),
            (String) typeCombo.getSelectedItem(),
            Integer.parseInt(capacityField.getText().trim()),
            Double.parseDouble(priceField.getText().trim()),
            (String) statusCombo.getSelectedItem(),
            null, false, false, null
        );
    }

    private void clear() {
        selectedDbId = null;
        roomNumberField.setText(""); capacityField.setText(""); priceField.setText("");
        typeCombo.setSelectedIndex(0); statusCombo.setSelectedIndex(0);
        table.clearSelection(); statusLabel.setText(" ");
    }

    private void filterTable(String q) {
        TableRowSorter<DefaultTableModel> s = new TableRowSorter<>(tableModel);
        table.setRowSorter(s);
        s.setRowFilter(q.isEmpty() ? null : RowFilter.regexFilter("(?i)" + q));
    }

    private void stat(String m, boolean ok) { statusLabel.setForeground(ok?new Color(30,140,60):new Color(200,50,50)); statusLabel.setText(m); }

    // ── Generic helpers ───────────────────────────────────────────────────
    private JTextField field() {
        JTextField f = new JTextField();
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(190,205,225),1,true), new EmptyBorder(3,7,3,7)));
        f.setAlignmentX(LEFT_ALIGNMENT); return f;
    }
    private JComboBox<String> combo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        c.setAlignmentX(LEFT_ALIGNMENT); return c;
    }
    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("SansSerif",Font.BOLD,12));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        b.setAlignmentX(LEFT_ALIGNMENT); return b;
    }
    private JLabel lbl(String t) { JLabel l=new JLabel(t); l.setFont(new Font("SansSerif",Font.PLAIN,11)); l.setForeground(new Color(80,90,110)); l.setAlignmentX(LEFT_ALIGNMENT); return l; }
    private Component vgap(int h) { return Box.createVerticalStrut(h); }
    private String str(Object o) { return o==null?"":o.toString(); }
    private void styleSmall(JButton b, Color bg) { b.setBackground(bg); b.setForeground(Color.WHITE); b.setFocusPainted(false); b.setBorderPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setFont(new Font("SansSerif",Font.BOLD,12)); }
    private void styleTable(JTable t, Color header) {
        t.setRowHeight(26); t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.getTableHeader().setBackground(header); t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));
        t.setFont(new Font("SansSerif",Font.PLAIN,12)); t.setGridColor(new Color(220,225,235));
        t.setSelectionBackground(new Color(210,228,255));
    }
    private void hideIdColumn(JTable t) {
        t.getColumnModel().getColumn(0).setMinWidth(0); t.getColumnModel().getColumn(0).setMaxWidth(0); t.getColumnModel().getColumn(0).setWidth(0);
    }
}
