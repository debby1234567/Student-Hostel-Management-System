package view;

import controller.BookingController;
import controller.RoomController;
import controller.StudentController;
import model.Booking;
import model.Room;
import model.Student;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class BookingForm extends JFrame {

    private final BookingController controller = new BookingController();
    private final StudentController studentCtrl = new StudentController();
    private final RoomController roomCtrl       = new RoomController();

    private JTable table;
    private DefaultTableModel tableModel;

    // Form
    private JTextField bookingCodeField, checkInField, checkOutField, totalAmountField;
    private JComboBox<StudentItem> studentCombo;
    private JComboBox<RoomItem>    roomCombo;
    private JComboBox<String>      statusCombo;
    private JLabel statusLabel;
    private Long selectedDbId = null;

    public BookingForm() {
        initUI();
        loadCombos();
        loadBookings();
    }

    private void initUI() {
        setTitle("Booking Management");
        setSize(1050, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setBackground(new Color(100, 60, 160));
        JLabel heading = new JLabel("  📋  Booking Management");
        heading.setForeground(Color.WHITE); heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        toolbar.add(heading);

        String[] cols = {"ID","Code","Student","Room","Check-In","Check-Out","Status","Total"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c){return false;} };
        table = new JTable(tableModel);
        styleTable(table, new Color(100, 60, 160));
        hideCol(table, 0);
        table.getSelectionModel().addListSelectionListener(e -> { if(!e.getValueIsAdjusting()) populateForm(); });

        JPanel searchBar = buildSearchBar();
        JPanel left = new JPanel(new BorderLayout());
        left.add(searchBar, BorderLayout.NORTH);
        left.add(new JScrollPane(table), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, buildFormPanel());
        split.setDividerLocation(640); split.setDividerSize(4);

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        bar.setBackground(new Color(245,247,250));
        JTextField sf = new JTextField(20);
        JButton sb = btn("🔍 Search", new Color(100,60,160));
        JButton rb = btn("↺ Refresh", new Color(100,110,130));
        sb.addActionListener(e -> filter(sf.getText()));
        rb.addActionListener(e -> { sf.setText(""); loadBookings(); });
        bar.add(new JLabel("Search:")); bar.add(sf); bar.add(sb); bar.add(rb);
        return bar;
    }

    private JPanel buildFormPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(new MatteBorder(0,1,0,0,new Color(210,220,235)), new EmptyBorder(16,18,16,18)));

        JLabel title = new JLabel("Booking Details");
        title.setFont(new Font("SansSerif",Font.BOLD,15)); title.setForeground(new Color(100,60,160)); title.setAlignmentX(LEFT_ALIGNMENT);

        bookingCodeField = field(); checkInField = field(); checkOutField = field(); totalAmountField = field();
        checkInField.setToolTipText("YYYY-MM-DD"); checkOutField.setToolTipText("YYYY-MM-DD");

        studentCombo = new JComboBox<>(); studentCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE,30)); studentCombo.setAlignmentX(LEFT_ALIGNMENT);
        roomCombo    = new JComboBox<>(); roomCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE,30)); roomCombo.setAlignmentX(LEFT_ALIGNMENT);
        statusCombo  = new JComboBox<>(new String[]{"ACTIVE","COMPLETED","CANCELLED"}); statusCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE,30)); statusCombo.setAlignmentX(LEFT_ALIGNMENT);

        JButton addBtn = btn("➕ Create Booking", new Color(46,160,67));
        JButton updBtn = btn("✏️ Update",          new Color(100,60,160));
        JButton canBtn = btn("✕ Cancel Booking",   new Color(200,50,50));
        JButton clrBtn = btn("⬜ Clear",            new Color(120,130,145));

        addBtn.addActionListener(e -> doCreate());
        updBtn.addActionListener(e -> doUpdate());
        canBtn.addActionListener(e -> doCancel());
        clrBtn.addActionListener(e -> clear());

        statusLabel = new JLabel(" "); statusLabel.setFont(new Font("SansSerif",Font.PLAIN,11)); statusLabel.setAlignmentX(LEFT_ALIGNMENT);

        p.add(title); p.add(vgap(14));
        p.add(lbl("Booking Code *")); p.add(bookingCodeField); p.add(vgap(5));
        p.add(lbl("Student *"));      p.add(studentCombo);     p.add(vgap(5));
        p.add(lbl("Room *"));         p.add(roomCombo);         p.add(vgap(5));
        p.add(lbl("Check-In (YYYY-MM-DD) *")); p.add(checkInField); p.add(vgap(5));
        p.add(lbl("Check-Out (YYYY-MM-DD) *")); p.add(checkOutField); p.add(vgap(5));
        p.add(lbl("Status"));          p.add(statusCombo);      p.add(vgap(5));
        p.add(lbl("Total Amount"));    p.add(totalAmountField); p.add(vgap(14));
        p.add(addBtn); p.add(vgap(4)); p.add(updBtn); p.add(vgap(4));
        p.add(canBtn); p.add(vgap(4)); p.add(clrBtn); p.add(vgap(8));
        p.add(statusLabel);
        return p;
    }

    private void loadCombos() {
        new SwingWorker<Void,Void>() {
            List<Student> students;
            List<Room> rooms;
            protected Void doInBackground() throws Exception {
                students = studentCtrl.getAllStudents();
                rooms    = roomCtrl.getAvailableRooms();
                return null;
            }
            protected void done() {
                try { get(); } catch (Exception ignored) {}
                studentCombo.removeAllItems();
                for (Student s : students) studentCombo.addItem(new StudentItem(s));
                roomCombo.removeAllItems();
                for (Room r : rooms) roomCombo.addItem(new RoomItem(r));
            }
        }.execute();
    }

    private void loadBookings() {
        new SwingWorker<List<Booking>,Void>() {
            protected List<Booking> doInBackground() throws Exception { return controller.getAllBookings(); }
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    for (Booking b : get()) {
                        String studentName = b.getStudent() != null ? b.getStudent().getFirstName()+" "+b.getStudent().getLastName() : "N/A";
                        String roomNo      = b.getRoom()    != null ? b.getRoom().getRoomNumber() : "N/A";
                        tableModel.addRow(new Object[]{
                            b.getId(), b.getBookingCode(), studentName, roomNo,
                            b.getCheckInDate(), b.getCheckOutDate(), b.getStatus(),
                            String.format("%.2f", b.getTotalAmount())
                        });
                    }
                } catch (Exception ex) { JOptionPane.showMessageDialog(BookingForm.this,"Load error: "+ex.getMessage()); }
            }
        }.execute();
    }

    private void doCreate() {
        if (!validateForm()) return;
        try {
            Booking b = buildBooking(null);
            new SwingWorker<Boolean,Void>() {
                protected Boolean doInBackground() throws Exception { return controller.createBooking(b); }
                protected void done() { try{if(get()){stat("Booking created.",true);clear();loadBookings();}else stat("Failed.",false);}catch(Exception ex){stat("Error: "+ex.getMessage(),false);} }
            }.execute();
        } catch (Exception ex) { stat("Error: "+ex.getMessage(), false); }
    }

    private void doUpdate() {
        if (selectedDbId == null){stat("Select a booking.",false);return;}
        try {
            Booking b = buildBooking(selectedDbId);
            new SwingWorker<Boolean,Void>() {
                protected Boolean doInBackground() throws Exception { return controller.updateBooking(b); }
                protected void done() { try{if(get()){stat("Updated.",true);loadBookings();}else stat("Failed.",false);}catch(Exception ex){stat("Error: "+ex.getMessage(),false);} }
            }.execute();
        } catch (Exception ex) { stat("Error: "+ex.getMessage(), false); }
    }

    private void doCancel() {
        if (selectedDbId == null){stat("Select a booking.",false);return;}
        if (JOptionPane.showConfirmDialog(this,"Cancel this booking?","Confirm",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        new SwingWorker<Boolean,Void>() {
            protected Boolean doInBackground() throws Exception { return controller.cancelBooking(selectedDbId); }
            protected void done() { try{if(get()){stat("Booking cancelled.",true);clear();loadBookings();}else stat("Failed.",false);}catch(Exception ex){stat("Error: "+ex.getMessage(),false);} }
        }.execute();
    }

    private void populateForm() {
        int row = table.getSelectedRow(); if(row<0) return;
        selectedDbId = (Long) tableModel.getValueAt(row,0);
        bookingCodeField.setText(str(tableModel.getValueAt(row,1)));
        checkInField.setText(str(tableModel.getValueAt(row,4)));
        checkOutField.setText(str(tableModel.getValueAt(row,5)));
        statusCombo.setSelectedItem(tableModel.getValueAt(row,6));
        totalAmountField.setText(str(tableModel.getValueAt(row,7)));
        
        String studentName = str(tableModel.getValueAt(row, 2));
    for (int i = 0; i < studentCombo.getItemCount(); i++) {
        StudentItem item = studentCombo.getItemAt(i);
        String label = item.student.getFirstName()+" "+item.student.getLastName();
        if (label.equals(studentName)) { studentCombo.setSelectedIndex(i); break; }
    }
    String roomNo = str(tableModel.getValueAt(row, 3));
    for (int i = 0; i < roomCombo.getItemCount(); i++) {
        if (roomCombo.getItemAt(i).room.getRoomNumber().equals(roomNo)) {
            roomCombo.setSelectedIndex(i); break;
        }
    }
    }


    private boolean validateForm() {
        if (bookingCodeField.getText().trim().isEmpty() || checkInField.getText().trim().isEmpty() || checkOutField.getText().trim().isEmpty()) {
            stat("Fill all required fields.",false); return false;
        }
        return true;
    }

    private Booking buildBooking(Long id) {
        StudentItem si = (StudentItem) studentCombo.getSelectedItem();
        RoomItem    ri = (RoomItem)    roomCombo.getSelectedItem();
        double amt = totalAmountField.getText().trim().isEmpty() ? 0 : Double.parseDouble(totalAmountField.getText().trim());
        return new Booking(id,
            bookingCodeField.getText().trim(),
            si != null ? si.student : null,
            ri != null ? ri.room    : null,
            LocalDate.parse(checkInField.getText().trim()),
            LocalDate.parse(checkOutField.getText().trim()),
            (String) statusCombo.getSelectedItem(),
            amt,
            LocalDate.now()
        );
    }

    private void clear() {
        selectedDbId=null; bookingCodeField.setText(""); checkInField.setText(""); checkOutField.setText(""); totalAmountField.setText("");
        statusCombo.setSelectedIndex(0); table.clearSelection(); statusLabel.setText(" ");
    }
    private void filter(String q) {
        TableRowSorter<DefaultTableModel> s=new TableRowSorter<>(tableModel); table.setRowSorter(s);
        s.setRowFilter(q.isEmpty()?null:RowFilter.regexFilter("(?i)"+q));
    }
    private void stat(String m, boolean ok){statusLabel.setForeground(ok?new Color(30,140,60):new Color(200,50,50));statusLabel.setText(m);}

    // ── Combo item wrappers ───────────────────────────────────────────────
    private static class StudentItem { final Student student;
        StudentItem(Student s){this.student=s;}
    @Override
        public String toString(){return s().getStudentId()+" – "+s().getFirstName()+" "+s().getLastName();}
        Student s(){return student;}
    }
    private static class RoomItem { final Room room;
        RoomItem(Room r){this.room=r;}
    @Override
        public String toString(){return r().getRoomNumber()+" ("+r().getRoomType()+") – "+r().getStatus();}
        Room r(){return room;}
    }

    // ── Generic helpers ───────────────────────────────────────────────────
    private JTextField field(){JTextField f=new JTextField();f.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));f.setFont(new Font("SansSerif",Font.PLAIN,12));f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(190,205,225),1,true),new EmptyBorder(3,7,3,7)));f.setAlignmentX(LEFT_ALIGNMENT);return f;}
    private JButton btn(String t,Color bg){JButton b=new JButton(t);b.setBackground(bg);b.setForeground(Color.WHITE);b.setFocusPainted(false);b.setBorderPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setFont(new Font("SansSerif",Font.BOLD,12));b.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));b.setAlignmentX(LEFT_ALIGNMENT);return b;}
    private JLabel lbl(String t){JLabel l=new JLabel(t);l.setFont(new Font("SansSerif",Font.PLAIN,11));l.setForeground(new Color(80,90,110));l.setAlignmentX(LEFT_ALIGNMENT);return l;}
    private Component vgap(int h){return Box.createVerticalStrut(h);}
    private String str(Object o){return o==null?"":o.toString();}
    private void styleTable(JTable t,Color h){t.setRowHeight(26);t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);t.getTableHeader().setBackground(h);t.getTableHeader().setForeground(Color.WHITE);t.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));t.setFont(new Font("SansSerif",Font.PLAIN,12));t.setGridColor(new Color(220,225,235));t.setSelectionBackground(new Color(210,228,255));}
    private void hideCol(JTable t,int c){t.getColumnModel().getColumn(c).setMinWidth(0);t.getColumnModel().getColumn(c).setMaxWidth(0);t.getColumnModel().getColumn(c).setWidth(0);}
}
