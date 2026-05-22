package view;

import controller.BookingController;
import controller.PaymentController;
import controller.StudentController;
import model.Booking;
import model.Payment;
import model.Student;
import report.ReportExporter;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Reports module – view and export Booking, Payment and Student reports.
 */
public class ReportsForm extends JFrame {

    private final ReportExporter exporter      = new ReportExporter();
    private final BookingController bookingCtrl = new BookingController();
    private final PaymentController paymentCtrl = new PaymentController();
    private final StudentController studentCtrl = new StudentController();

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel summaryLabel;
    private String currentReport = "bookings";

    public ReportsForm() {
        initUI();
        loadBookingReport();
    }

    private void initUI() {
        setTitle("Reports");
        UiUtil.configureFrame(this, 900, 560, 720, 440);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ── Header ─────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setBackground(new Color(20, 120, 100));
        JLabel heading = new JLabel("  📊  Reports & Exports");
        heading.setForeground(Color.WHITE); heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        toolbar.add(heading);

        // ── Report selector ────────────────────────────────────────────
        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        selector.setBackground(new Color(245, 247, 250));
        selector.setBorder(new MatteBorder(0, 0, 1, 0, new Color(210, 220, 235)));

        JButton bookBtn = reportBtn("📋  Booking Report",  new Color(100, 60, 160));
        JButton payBtn  = reportBtn("💳  Payment Report",  new Color(180, 80, 20));
        JButton stuBtn  = reportBtn("👩‍🎓  Student Report",  new Color(30, 100, 180));

        bookBtn.addActionListener(e -> { currentReport = "bookings"; loadBookingReport(); });
        payBtn.addActionListener(e  -> { currentReport = "payments"; loadPaymentReport(); });
        stuBtn.addActionListener(e  -> { currentReport = "students"; loadStudentReport(); });

        JButton exportCsvBtn = new JButton("⬇ Export CSV");
        styleExportBtn(exportCsvBtn, new Color(20, 120, 100));
        exportCsvBtn.addActionListener(e -> doExport("CSV"));

        JButton exportPdfBtn = new JButton("⬇ Export PDF");
        styleExportBtn(exportPdfBtn, new Color(160, 30, 30));
        exportPdfBtn.addActionListener(e -> doExport("PDF"));

        selector.add(bookBtn); selector.add(payBtn); selector.add(stuBtn);
        selector.add(Box.createHorizontalStrut(20));
        selector.add(exportCsvBtn); selector.add(exportPdfBtn);

        // ── Table ──────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(new String[]{}, 0) { public boolean isCellEditable(int r, int c){return false;} };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.getTableHeader().setBackground(new Color(20, 120, 100));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setGridColor(new Color(220, 225, 235));
        table.setSelectionBackground(new Color(200, 240, 225));

        // ── Summary bar ───────────────────────────────────────────────
        JPanel summaryBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        summaryBar.setBackground(new Color(235, 245, 240));
        summaryBar.setBorder(new MatteBorder(1, 0, 0, 0, new Color(200, 225, 215)));
        summaryLabel = new JLabel("Loading…");
        summaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        summaryLabel.setForeground(new Color(30, 80, 60));
        summaryBar.add(summaryLabel);

        setLayout(new BorderLayout());
        add(toolbar,   BorderLayout.NORTH);
        add(selector,  BorderLayout.BEFORE_FIRST_LINE); // added below NORTH
        JPanel top = new JPanel(new BorderLayout());
        top.add(toolbar,  BorderLayout.NORTH);
        top.add(selector, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(summaryBar, BorderLayout.SOUTH);
    }

    // ── Report loaders ────────────────────────────────────────────────────
    private void loadBookingReport() {
        summaryLabel.setText("Loading bookings…");
        new SwingWorker<List<Booking>,Void>(){
            protected List<Booking> doInBackground() throws Exception{return bookingCtrl.getAllBookings();}
            protected void done(){
                try{
                    List<Booking> list = get();
                    String[] cols = {"Booking Code","Student","Room","Check-In","Check-Out","Status","Total Amount"};
                    tableModel.setColumnIdentifiers(cols); tableModel.setRowCount(0);
                    double total=0;
                    for(Booking b:list){
                        String sName = b.getStudent()!=null?b.getStudent().getFirstName()+" "+b.getStudent().getLastName():"N/A";
                        String rNo   = b.getRoom()!=null?b.getRoom().getRoomNumber():"N/A";
                        tableModel.addRow(new Object[]{b.getBookingCode(),sName,rNo,b.getCheckInDate(),b.getCheckOutDate(),b.getStatus(),String.format("%.2f",b.getTotalAmount())});
                        total += b.getTotalAmount();
                    }
                    summaryLabel.setText(String.format("Total bookings: %d  |  Grand total: RWF %.2f", list.size(), total));
                }catch(Exception ex){summaryLabel.setText("Error: "+ex.getMessage());}
            }
        }.execute();
    }

    private void loadPaymentReport() {
        summaryLabel.setText("Loading payments…");
        new SwingWorker<List<Payment>,Void>(){
            protected List<Payment> doInBackground() throws Exception{return paymentCtrl.getAllPayments();}
            protected void done(){
                try{
                    List<Payment> list=get();
                    String[] cols={"Reference","Booking","Amount","Date","Method","Status"};
                    tableModel.setColumnIdentifiers(cols); tableModel.setRowCount(0);
                    double total=0;
                    for(Payment p:list){
                        String code=p.getBooking()!=null?p.getBooking().gettBookingCode():"N/A";
                        tableModel.addRow(new Object[]{p.getPaymentReference(),code,String.format("%.2f",p.getAmount()),p.getPaymentDate(),p.getPaymentMethod(),p.getStatus()});
                        if("PAID".equalsIgnoreCase((String) p.getStatus())) total+=p.getAmount();
                    }
                    summaryLabel.setText(String.format("Total payments: %d  |  Total received: RWF %.2f", list.size(), total));
                }catch(Exception ex){summaryLabel.setText("Error: "+ex.getMessage());}
            }
        }.execute();
    }

    private void loadStudentReport() {
        summaryLabel.setText("Loading students…");
        new SwingWorker<List<Student>,Void>(){
            protected List<Student> doInBackground() throws Exception{return studentCtrl.getAllStudents();}
            protected void done(){
                try{
                    List<Student> list=get();
                    String[] cols={"Student ID","First Name","Last Name","Email","Phone","Gender","National ID","Active"};
                    tableModel.setColumnIdentifiers(cols); tableModel.setRowCount(0);
                    long active=0;
                    for(Student s:list){
                        boolean a=s.isIsActive(); if(a)active++;
                        tableModel.addRow(new Object[]{s.getStudentId(),s.getFirstName(),s.getLastName(),s.getEmail(),s.getPhoneNumber(),s.getGender(),s.getNationalId(),a?"Yes":"No"});
                    }
                    summaryLabel.setText(String.format("Total students: %d  |  Active: %d", list.size(), active));
                }catch(Exception ex){summaryLabel.setText("Error: "+ex.getMessage());}
            }
        }.execute();
    }

    // ── Export ────────────────────────────────────────────────────────────
    private void doExport(String format) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save " + format + " Report");
        String ext = "CSV".equals(format) ? ".csv" : ".pdf";
        fc.setSelectedFile(new java.io.File(currentReport + "_report" + ext));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        String path = fc.getSelectedFile().getAbsolutePath();
        try {
            if ("CSV".equals(format)) {
                exportCsv(path);
                JOptionPane.showMessageDialog(this, "Exported to: " + path, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "PDF export requires JasperReports/iText.\nCSV export is available.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportCsv(String path) throws Exception {
        StringBuilder sb = new StringBuilder();
        // Headers
        for (int c = 0; c < tableModel.getColumnCount(); c++) {
            if (c > 0) sb.append(",");
            sb.append('"').append(tableModel.getColumnName(c)).append('"');
        }
        sb.append('\n');
        // Rows
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                if (c > 0) sb.append(",");
                Object val = tableModel.getValueAt(r, c);
                sb.append('"').append(val == null ? "" : val.toString().replace("\"", "\"\"")).append('"');
            }
            sb.append('\n');
        }
        try (java.io.FileWriter fw = new java.io.FileWriter(path)) { fw.write(sb.toString()); }
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private JButton reportBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        return b;
    }

    private void styleExportBtn(JButton b, Color bg) {
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
    }
}
