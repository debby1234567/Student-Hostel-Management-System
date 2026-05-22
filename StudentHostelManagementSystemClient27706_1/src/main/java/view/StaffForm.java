package view;

import controller.StaffController;
import model.Staff;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class StaffForm extends JFrame {

    private final StaffController controller = new StaffController();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField staffCodeField, firstNameField, lastNameField, emailField, phoneField, passwordField, hireDateField;
    private JComboBox<String> roleCombo;
    private JLabel statusLabel;
    private Long selectedDbId = null;

    public StaffForm() {
        initUI();
        loadStaff();
    }

    private void initUI() {
        setTitle("Staff Management");
        setSize(1000, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,6));
        toolbar.setBackground(new Color(160,30,30));
        JLabel heading = new JLabel("  👥  Staff Management");
        heading.setForeground(Color.WHITE); heading.setFont(new Font("SansSerif",Font.BOLD,16));
        toolbar.add(heading);

        String[] cols = {"ID","Code","First Name","Last Name","Email","Phone","Role","Hire Date","Active"};
        tableModel = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(tableModel);
        styleTable(table, new Color(160,30,30));
        hideCol(table, 0);
        table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())populateForm();});

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
        JButton sb = btn("🔍 Search",new Color(160,30,30));
        JButton rb = btn("↺ Refresh",new Color(100,110,130));
        sb.addActionListener(e->filter(sf.getText()));
        rb.addActionListener(e->{sf.setText("");loadStaff();});
        bar.add(new JLabel("Search:")); bar.add(sf); bar.add(sb); bar.add(rb);
        return bar;
    }

    private JPanel buildFormPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(new MatteBorder(0,1,0,0,new Color(210,220,235)),new EmptyBorder(16,18,16,18)));

        JLabel title = new JLabel("Staff Details");
        title.setFont(new Font("SansSerif",Font.BOLD,15)); title.setForeground(new Color(160,30,30)); title.setAlignmentX(LEFT_ALIGNMENT);

        staffCodeField = field(); firstNameField = field(); lastNameField = field();
        emailField = field(); phoneField = field(); passwordField = field(); hireDateField = field();
        hireDateField.setToolTipText("YYYY-MM-DD");
        roleCombo = new JComboBox<>(new String[]{"ADMIN","MANAGER","RECEPTIONIST","MAINTENANCE","SECURITY","OTHER"});
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE,30)); roleCombo.setAlignmentX(LEFT_ALIGNMENT);

        JButton addBtn = btn("➕ Add Staff",    new Color(46,160,67));
        JButton updBtn = btn("✏️ Update",        new Color(160,30,30));
        JButton delBtn = btn("🗑 Delete",        new Color(200,50,50));
        JButton clrBtn = btn("⬜ Clear",         new Color(120,130,145));
        addBtn.addActionListener(e->doAdd());
        updBtn.addActionListener(e->doUpdate());
        delBtn.addActionListener(e->doDelete());
        clrBtn.addActionListener(e->clear());

        statusLabel = new JLabel(" "); statusLabel.setFont(new Font("SansSerif",Font.PLAIN,11)); statusLabel.setAlignmentX(LEFT_ALIGNMENT);

        p.add(title); p.add(vgap(14));
        p.add(lbl("Staff Code *"));  p.add(staffCodeField); p.add(vgap(5));
        p.add(lbl("First Name *"));  p.add(firstNameField); p.add(vgap(5));
        p.add(lbl("Last Name *"));   p.add(lastNameField);  p.add(vgap(5));
        p.add(lbl("Email *"));       p.add(emailField);     p.add(vgap(5));
        p.add(lbl("Phone"));         p.add(phoneField);     p.add(vgap(5));
        p.add(lbl("Role"));          p.add(roleCombo);      p.add(vgap(5));
        p.add(lbl("Password *"));    p.add(passwordField);  p.add(vgap(5));
        p.add(lbl("Hire Date"));     p.add(hireDateField);  p.add(vgap(14));
        p.add(addBtn); p.add(vgap(4)); p.add(updBtn); p.add(vgap(4));
        p.add(delBtn); p.add(vgap(4)); p.add(clrBtn); p.add(vgap(8));
        p.add(statusLabel);
        return p;
    }

    private void loadStaff() {
        new SwingWorker<List<Staff>,Void>(){
            protected List<Staff> doInBackground() throws Exception{return controller.getAllStaff();}
            protected void done(){
                try{tableModel.setRowCount(0);
                    for(Staff s:get()) tableModel.addRow(new Object[]{s.getId(),s.getStaffCode(),s.getFirstName(),s.getLastName(),s.getEmail(),s.getPhoneNumber(),s.getRole(),s.getHireDate(),s.isIsActive()?"Yes":"No"});
                }catch(Exception ex){JOptionPane.showMessageDialog(StaffForm.this,"Load error: "+ex.getMessage());}
            }
        }.execute();
    }

    private void doAdd() {
        if(staffCodeField.getText().trim().isEmpty()||firstNameField.getText().trim().isEmpty()||emailField.getText().trim().isEmpty()){stat("Fill required fields.",false);return;}
        Staff s = buildStaff(null);
        new SwingWorker<Boolean,Void>(){
            protected Boolean doInBackground() throws Exception{return controller.addStaff(s);}
            protected void done(){try{if(get()){stat("Staff added.",true);clear();loadStaff();}else stat("Failed.",false);}catch(Exception ex){stat("Error: "+ex.getMessage(),false);}}
        }.execute();
    }

    private void doUpdate() {
        if(selectedDbId==null){stat("Select a staff member.",false);return;}
        Staff s = buildStaff(selectedDbId);
        new SwingWorker<Boolean,Void>(){
            protected Boolean doInBackground() throws Exception{return controller.updateStaff(s);}
            protected void done(){try{if(get()){stat("Updated.",true);loadStaff();}else stat("Failed.",false);}catch(Exception ex){stat("Error: "+ex.getMessage(),false);}}
        }.execute();
    }

    private void doDelete() {
        if(selectedDbId==null){stat("Select a staff member.",false);return;}
        if(JOptionPane.showConfirmDialog(this,"Delete this staff member?","Confirm",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        new SwingWorker<Boolean,Void>(){
            protected Boolean doInBackground() throws Exception{return controller.deleteStaff(selectedDbId);}
            protected void done(){try{if(get()){stat("Deleted.",true);clear();loadStaff();}else stat("Failed.",false);}catch(Exception ex){stat("Error: "+ex.getMessage(),false);}}
        }.execute();
    }

    private void populateForm() {
        int row=table.getSelectedRow(); if(row<0)return;
        selectedDbId=(Long)tableModel.getValueAt(row,0);
        staffCodeField.setText(str(tableModel.getValueAt(row,1)));
        firstNameField.setText(str(tableModel.getValueAt(row,2)));
        lastNameField.setText(str(tableModel.getValueAt(row,3)));
        emailField.setText(str(tableModel.getValueAt(row,4)));
        phoneField.setText(str(tableModel.getValueAt(row,5)));
        roleCombo.setSelectedItem(tableModel.getValueAt(row,6));
        hireDateField.setText(str(tableModel.getValueAt(row,7)));
        passwordField.setText("");
    }

    private Staff buildStaff(Long id) {
        LocalDate hd = hireDateField.getText().trim().isEmpty() ? LocalDate.now() : LocalDate.parse(hireDateField.getText().trim());
        return null;
        
    }

    private void clear(){selectedDbId=null;staffCodeField.setText("");firstNameField.setText("");lastNameField.setText("");emailField.setText("");phoneField.setText("");passwordField.setText("");hireDateField.setText("");roleCombo.setSelectedIndex(0);table.clearSelection();statusLabel.setText(" ");}
    private void filter(String q){TableRowSorter<DefaultTableModel> s=new TableRowSorter<>(tableModel);table.setRowSorter(s);s.setRowFilter(q.isEmpty()?null:RowFilter.regexFilter("(?i)"+q));}
    private void stat(String m,boolean ok){statusLabel.setForeground(ok?new Color(30,140,60):new Color(200,50,50));statusLabel.setText(m);}

    private JTextField field(){JTextField f=new JTextField();f.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));f.setFont(new Font("SansSerif",Font.PLAIN,12));f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(190,205,225),1,true),new EmptyBorder(3,7,3,7)));f.setAlignmentX(LEFT_ALIGNMENT);return f;}
    private JButton btn(String t,Color bg){JButton b=new JButton(t);b.setBackground(bg);b.setForeground(Color.WHITE);b.setFocusPainted(false);b.setBorderPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setFont(new Font("SansSerif",Font.BOLD,12));b.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));b.setAlignmentX(LEFT_ALIGNMENT);return b;}
    private JLabel lbl(String t){JLabel l=new JLabel(t);l.setFont(new Font("SansSerif",Font.PLAIN,11));l.setForeground(new Color(80,90,110));l.setAlignmentX(LEFT_ALIGNMENT);return l;}
    private Component vgap(int h){return Box.createVerticalStrut(h);}
    private String str(Object o){return o==null?"":o.toString();}
    private void styleTable(JTable t,Color h){t.setRowHeight(26);t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);t.getTableHeader().setBackground(h);t.getTableHeader().setForeground(Color.WHITE);t.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));t.setFont(new Font("SansSerif",Font.PLAIN,12));t.setGridColor(new Color(220,225,235));t.setSelectionBackground(new Color(210,228,255));}
    private void hideCol(JTable t,int c){t.getColumnModel().getColumn(c).setMinWidth(0);t.getColumnModel().getColumn(c).setMaxWidth(0);t.getColumnModel().getColumn(c).setWidth(0);}
}
