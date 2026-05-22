package view;

import controller.BookingController;
import controller.PaymentController;
import model.Booking;
import model.Payment;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PaymentForm extends JFrame {

    private final PaymentController controller = new PaymentController();
    private final BookingController bookingCtrl = new BookingController();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField refField, amountField, dateField;
    private JComboBox<BookingItem> bookingCombo;
    private JComboBox<String> methodCombo, statusCombo;
    private JLabel statusLabel;
    private Long selectedDbId = null;

    public PaymentForm() {
        initUI();
        loadCombos();
        loadPayments();
    }

    private void initUI() {
        setTitle("Payment Management");
        UiUtil.configureFrame(this, 1000, 580, 800, 460);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setBackground(new Color(180, 80, 20));
        JLabel heading = new JLabel("  💳  Payment Management");
        heading.setForeground(Color.WHITE); heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        toolbar.add(heading);

        String[] cols = {"ID","Reference","Booking Code","Amount","Date","Method","Status"};
        tableModel = new DefaultTableModel(cols, 0){public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(tableModel);
        styleTable(table, new Color(180,80,20));
        hideCol(table, 0);
        table.getSelectionModel().addListSelectionListener(e -> {if(!e.getValueIsAdjusting()) populateForm();});

        JPanel searchBar = buildSearchBar();
        JPanel left = new JPanel(new BorderLayout());
        left.add(searchBar, BorderLayout.NORTH);
        left.add(new JScrollPane(table), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, buildFormPanel());
        split.setDividerLocation(620); split.setDividerSize(4);

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        bar.setBackground(new Color(245,247,250));
        JTextField sf = new JTextField(20);
        JButton sb = btn("🔍 Search",new Color(180,80,20));
        JButton rb = btn("↺ Refresh",new Color(100,110,130));
        sb.addActionListener(e->filter(sf.getText()));
        rb.addActionListener(e->{sf.setText("");loadPayments();});
        bar.add(new JLabel("Search:")); bar.add(sf); bar.add(sb); bar.add(rb);
        return bar;
    }

    private JPanel buildFormPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(new MatteBorder(0,1,0,0,new Color(210,220,235)),new EmptyBorder(16,18,16,18)));

        JLabel title = new JLabel("Payment Details");
        title.setFont(new Font("SansSerif",Font.BOLD,15));
        title.setForeground(new Color(180,80,20)); title.setAlignmentX(LEFT_ALIGNMENT);

        refField = field(); amountField = field(); dateField = field();
        dateField.setToolTipText("YYYY-MM-DD");
        bookingCombo = new JComboBox<>(); bookingCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE,30)); bookingCombo.setAlignmentX(LEFT_ALIGNMENT);
        methodCombo = new JComboBox<>(new String[]{"CASH","MOBILE_MONEY","BANK_TRANSFER","CARD"}); methodCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE,30)); methodCombo.setAlignmentX(LEFT_ALIGNMENT);
        statusCombo = new JComboBox<>(new String[]{"PAID","PENDING","FAILED"}); statusCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE,30)); statusCombo.setAlignmentX(LEFT_ALIGNMENT);

        JButton addBtn = btn("➕ Record Payment",new Color(46,160,67));
        JButton updBtn = btn("✏️ Update",         new Color(180,80,20));
        JButton clrBtn = btn("⬜ Clear",           new Color(120,130,145));
        addBtn.addActionListener(e->doRecord());
        updBtn.addActionListener(e->doUpdate());
        clrBtn.addActionListener(e->clear());

        statusLabel = new JLabel(" "); statusLabel.setFont(new Font("SansSerif",Font.PLAIN,11)); statusLabel.setAlignmentX(LEFT_ALIGNMENT);

        p.add(title); p.add(vgap(14));
        p.add(lbl("Payment Ref *")); p.add(refField);      p.add(vgap(5));
        p.add(lbl("Booking *"));     p.add(bookingCombo);  p.add(vgap(5));
        p.add(lbl("Amount *"));      p.add(amountField);   p.add(vgap(5));
        p.add(lbl("Date (YYYY-MM-DD) *")); p.add(dateField); p.add(vgap(5));
        p.add(lbl("Method"));        p.add(methodCombo);   p.add(vgap(5));
        p.add(lbl("Status"));        p.add(statusCombo);   p.add(vgap(14));
        p.add(addBtn); p.add(vgap(4)); p.add(updBtn); p.add(vgap(4)); p.add(clrBtn); p.add(vgap(8));
        p.add(statusLabel);
        return p;
    }

    private void loadCombos() {
        new SwingWorker<List<Booking>,Void>() {
            protected List<Booking> doInBackground() throws Exception { return bookingCtrl.getAllBookings(); }
            protected void done() {
                try { bookingCombo.removeAllItems(); for(Booking b:get()) bookingCombo.addItem(new BookingItem(b)); }
                catch(Exception ignored){}
            }
        }.execute();
    }

    private void loadPayments() {
        new SwingWorker<List<Payment>,Void>() {
            protected List<Payment> doInBackground() throws Exception { return controller.getAllPayments(); }
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    for(Payment p:get()) {
                        String code = p.getBooking()!=null?p.getBooking().getBookingCode():"N/A";
                        tableModel.addRow(new Object[]{p.getId(),p.getPaymentReference(),code,
                            String.format("%.2f",p.getAmount()),p.getPaymentDate(),p.getPaymentMethod(),p.getStatus()});
                    }
                } catch(Exception ex){JOptionPane.showMessageDialog(PaymentForm.this,"Load error: "+ex.getMessage());}
            }
        }.execute();
    }

    private void doRecord() {
        if(refField.getText().trim().isEmpty()||amountField.getText().trim().isEmpty()||dateField.getText().trim().isEmpty()){stat("Fill required fields.",false);return;}
        try {
            Payment pay = buildPayment(null);
            new SwingWorker<Boolean,Void>(){
                protected Boolean doInBackground() throws Exception{return controller.recordPayment(pay);}
                protected void done(){try{if(get()){stat("Payment recorded.",true);clear();loadPayments();}else stat("Failed.",false);}catch(Exception ex){stat("Error: "+ex.getMessage(),false);}}
            }.execute();
        } catch(Exception ex){stat("Error: "+ex.getMessage(),false);}
    }

    private void doUpdate() {
        if(selectedDbId==null){stat("Select a payment.",false);return;}
        try {
            Payment pay = buildPayment(selectedDbId);
            new SwingWorker<Boolean,Void>(){
                protected Boolean doInBackground() throws Exception{return controller.updatePayment(pay);}
                protected void done(){try{if(get()){stat("Updated.",true);loadPayments();}else stat("Failed.",false);}catch(Exception ex){stat("Error: "+ex.getMessage(),false);}}
            }.execute();
        } catch(Exception ex){stat("Error: "+ex.getMessage(),false);}
    }

    private void populateForm() {
        int row=table.getSelectedRow(); if(row<0)return;
        selectedDbId=(Long)tableModel.getValueAt(row,0);
        refField.setText(str(tableModel.getValueAt(row,1)));
        amountField.setText(str(tableModel.getValueAt(row,3)));
        dateField.setText(str(tableModel.getValueAt(row,4)));
        methodCombo.setSelectedItem(tableModel.getValueAt(row,5));
        statusCombo.setSelectedItem(tableModel.getValueAt(row,6));
    }

    private Payment buildPayment(Long id) {
        BookingItem bi = (BookingItem) bookingCombo.getSelectedItem();
        return new Payment(id, refField.getText().trim(),
            bi!=null?bi.booking:null,
            Double.parseDouble(amountField.getText().trim()),
            LocalDate.parse(dateField.getText().trim()),
            (String)methodCombo.getSelectedItem(),
            (String)statusCombo.getSelectedItem());
    }

    private void clear(){selectedDbId=null;refField.setText("");amountField.setText("");dateField.setText("");methodCombo.setSelectedIndex(0);statusCombo.setSelectedIndex(0);table.clearSelection();statusLabel.setText(" ");}
    private void filter(String q){TableRowSorter<DefaultTableModel> s=new TableRowSorter<>(tableModel);table.setRowSorter(s);s.setRowFilter(q.isEmpty()?null:RowFilter.regexFilter("(?i)"+q));}
    private void stat(String m,boolean ok){statusLabel.setForeground(ok?new Color(30,140,60):new Color(200,50,50));statusLabel.setText(m);}

    private static class BookingItem{final Booking booking;BookingItem(Booking b){this.booking=b;}public String toString(){return booking.getBookingCode()+(booking.getStudent()!=null?" – "+booking.getStudent().getFirstName():"");}}

    private JTextField field(){JTextField f=new JTextField();f.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));f.setFont(new Font("SansSerif",Font.PLAIN,12));f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(190,205,225),1,true),new EmptyBorder(3,7,3,7)));f.setAlignmentX(LEFT_ALIGNMENT);return f;}
    private JButton btn(String t,Color bg){JButton b=new JButton(t);b.setBackground(bg);b.setForeground(Color.WHITE);b.setFocusPainted(false);b.setBorderPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setFont(new Font("SansSerif",Font.BOLD,12));b.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));b.setAlignmentX(LEFT_ALIGNMENT);return b;}
    private JLabel lbl(String t){JLabel l=new JLabel(t);l.setFont(new Font("SansSerif",Font.PLAIN,11));l.setForeground(new Color(80,90,110));l.setAlignmentX(LEFT_ALIGNMENT);return l;}
    private Component vgap(int h){return Box.createVerticalStrut(h);}
    private String str(Object o){return o==null?"":o.toString();}
    private void styleTable(JTable t,Color h){t.setRowHeight(26);t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);t.getTableHeader().setBackground(h);t.getTableHeader().setForeground(Color.WHITE);t.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));t.setFont(new Font("SansSerif",Font.PLAIN,12));t.setGridColor(new Color(220,225,235));t.setSelectionBackground(new Color(210,228,255));}
    private void hideCol(JTable t,int c){t.getColumnModel().getColumn(c).setMinWidth(0);t.getColumnModel().getColumn(c).setMaxWidth(0);t.getColumnModel().getColumn(c).setWidth(0);}
}
