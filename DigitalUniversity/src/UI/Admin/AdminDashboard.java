/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package UI.Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import accesscontrol.*;
import business.*;
import model.*;
/**
 *
 * @author USER
 */
public class AdminDashboard extends javax.swing.JPanel {
    private JTabbedPane tabbedPane;
    private AdminService adminService;
    private ReportService reportService;
    private UniversityDirectory directory;
    private AuthenticationService authService;
    private DefaultTableModel userTableModel;
    private JTable userTable;
    
    /**
     * Creates new form AdminDashboard
     */
    public AdminDashboard(UniversityDirectory directory) {
        this.directory = directory;
        this.adminService = new AdminService();
        this.reportService = new ReportService();
        this.authService = AuthenticationService.getInstance();

        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("User Account Management", createUserAccountPanel());
        tabbedPane.addTab("Registrar Records Management", createRegistrarPanel());
        tabbedPane.addTab("Student & Faculty Records", createStudentFacultyPanel());
        tabbedPane.addTab("Analytics Dashboard", createAnalyticsPanel());
        tabbedPane.addTab("Profile Management", createProfilePanel());

        add(tabbedPane, BorderLayout.CENTER);
        
        
    }
    
    // ============================================================
    // user account MANAGEMENT PANEL
    // ============================================================
    private JPanel createUserAccountPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    JLabel title = new JLabel("Manage User Accounts", SwingConstants.CENTER);

    // 使用 class-level model
    String[] columns = {"Username", "Role", "University ID"};
    userTableModel = new DefaultTableModel(columns, 0);
    userTable = new JTable(userTableModel);

    // 預設帳號
    userTableModel.addRow(new Object[]{"admin", "ADMIN", "U0001"});
    userTableModel.addRow(new Object[]{"faculty01", "FACULTY", "U0002"});
    userTableModel.addRow(new Object[]{"student01", "STUDENT", "U0003"});
    userTableModel.addRow(new Object[]{"registrar01", "REGISTRAR", "U0004"});

    JButton btnAdd = new JButton("Add Account");
    JButton btnEdit = new JButton("Edit Account");
    JButton btnDelete = new JButton("Delete Account");

    // 新增帳號按鈕
    btnAdd.addActionListener(e -> {
        String username = JOptionPane.showInputDialog(this, "Enter username:");
        if (username == null || username.trim().isEmpty()) return;

        String password = JOptionPane.showInputDialog(this, "Enter password:");
        if (password == null || password.trim().isEmpty()) return;

        String[] roles = {"Admin", "Faculty", "Student", "Registrar"};
        String role = (String) JOptionPane.showInputDialog(
                this, "Select Role:", "Choose Role",
                JOptionPane.PLAIN_MESSAGE, null, roles, roles[0]);
        if (role == null) return;

        // 產生唯一 University ID
        String uniId = generateUniversityId();

        // 生成臨時 Person（防止 NullPointer / IllegalArgumentException）
        Person tempPerson = new Person(
                uniId,
                username,          // firstName
                "",                // lastName
                username + "@example.com"  // email
        );

        // 呼叫後端 service
        adminService.createUserAccount(username, password, role.toUpperCase(), tempPerson);

        // 更新 JTable
        userTableModel.addRow(new Object[]{username, role.toUpperCase(), uniId});
        userTableModel.fireTableDataChanged();
        JOptionPane.showMessageDialog(this, "✅ Account created successfully!");
    });

    //  編輯帳號
    btnEdit.addActionListener(e -> {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to edit!");
            return;
        }
        String newUsername = JOptionPane.showInputDialog(this, "Enter new username:");
        if (newUsername != null && !newUsername.trim().isEmpty()) {
            userTableModel.setValueAt(newUsername, row, 0);
            JOptionPane.showMessageDialog(this, "Username updated!");
        }
    });

    // ️ 刪除帳號
    btnDelete.addActionListener(e -> {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to delete!");
            return;
        }
        userTableModel.removeRow(row);
        JOptionPane.showMessageDialog(this, "User deleted!");
    });

    JPanel buttons = new JPanel();
    buttons.add(btnAdd);
    buttons.add(btnEdit);
    buttons.add(btnDelete);

    panel.add(title, BorderLayout.NORTH);
    panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
    panel.add(buttons, BorderLayout.SOUTH);
    return panel;
}


private String generateUniversityId() {
    return "U" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
}

    // ============================================================
    // 2️⃣ REGISTRAR RECORDS MANAGEMENT PANEL
    // ============================================================
    private JPanel createRegistrarPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Registrar Records Management", SwingConstants.CENTER);

        String[] columns = {"University ID", "Name", "Email", "Phone"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        model.addRow(new Object[]{"U2001", "Amy", "amy.student@northeastern.edu", "123-456-7890"});
        model.addRow(new Object[]{"U2002", "Ben", "ben.learner@northeastern.edu", "987-654-3210"});
        model.addRow(new Object[]{"U3001", "Chen", "emily.chen@northeastern.edu", "555-111-2222"});
        model.addRow(new Object[]{"U3002", "Kim", "daniel.kim@northeastern.edu", "444-555-6666"});
        

        JButton btnView = new JButton("View All");
        JButton btnEdit = new JButton("Edit Registrar Info");
        JButton btnDelete = new JButton("Delete Registrar");

        btnView.addActionListener(e -> {
           int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a record to view details!");
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("University ID: ").append(model.getValueAt(row, 0)).append("\n");
        details.append("Name: ").append(model.getValueAt(row, 1)).append("\n");
        details.append("Email: ").append(model.getValueAt(row, 2)).append("\n");
        details.append("Phone: ").append(model.getValueAt(row, 3)).append("\n");

        JOptionPane.showMessageDialog(this, details.toString(), "Record Details", JOptionPane.INFORMATION_MESSAGE);
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            String newEmail = JOptionPane.showInputDialog(this, "Enter new email:");
            if (newEmail != null) model.setValueAt(newEmail, row, 2);
            JOptionPane.showMessageDialog(this, "Registrar info updated!");
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            model.removeRow(row);
            JOptionPane.showMessageDialog(this, "Registrar deleted!");
        });

        JPanel buttons = new JPanel();
        buttons.add(btnView);
        buttons.add(btnEdit);
        buttons.add(btnDelete);

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    // ============================================================
    // 3️⃣ STUDENT & FACULTY RECORDS MANAGEMENT PANEL
    // ============================================================
    private JPanel createStudentFacultyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Student & Faculty Records Management", SwingConstants.CENTER);

        String[] columns = {"Type", "University ID", "Name", "Department", "Email", "Phone"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        model.addRow(new Object[]{"Student", "U2001", "Amy Student", "Information Systems", "amy.student@northeastern.edu", "123-456-7890"});
        model.addRow(new Object[]{"Student", "U2002", "Ben Learner", "Computer Science", "ben.learner@northeastern.edu", "987-654-3210"});
        model.addRow(new Object[]{"Faculty", "U3001", "Dr. Emily Chen", "Data Science", "emily.chen@northeastern.edu", "555-111-2222"});
        model.addRow(new Object[]{"Faculty", "U3002", "Prof. Daniel Kim", "Software Engineering", "daniel.kim@northeastern.edu", "444-555-6666"});


        JButton btnView = new JButton("View All");
        JButton btnEdit = new JButton("Edit Info");
        JButton btnDelete = new JButton("Delete Record");
        JButton btnSearch = new JButton("Search");

        btnView.addActionListener(e -> {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a record to view details!");
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("Type: ").append(model.getValueAt(row, 0)).append("\n");
        details.append("University ID: ").append(model.getValueAt(row, 1)).append("\n");
        details.append("Name: ").append(model.getValueAt(row, 2)).append("\n");
        details.append("Department: ").append(model.getValueAt(row, 3)).append("\n");
        details.append("Email: ").append(model.getValueAt(row, 4)).append("\n");
        details.append("Phone: ").append(model.getValueAt(row, 5)).append("\n");

        JOptionPane.showMessageDialog(this, details.toString(), "Record Details", JOptionPane.INFORMATION_MESSAGE);
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            String newEmail = JOptionPane.showInputDialog(this, "Enter new email:");
            if (newEmail != null) model.setValueAt(newEmail, row, 4);
            JOptionPane.showMessageDialog(this, "Record updated!");
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            model.removeRow(row);
            JOptionPane.showMessageDialog(this, "Record deleted!");
        });

        btnSearch.addActionListener(e -> {
        String keyword = JOptionPane.showInputDialog(this, "Enter name, ID, or department:");
        if (keyword == null || keyword.trim().isEmpty()) return;

        keyword = keyword.toLowerCase();
        DefaultTableModel searchModel = new DefaultTableModel(columns, 0);

        // 搜尋 JTable 內目前的資料
        for (int i = 0; i < model.getRowCount(); i++) {
            String name = model.getValueAt(i, 2).toString().toLowerCase();
            String id = model.getValueAt(i, 1).toString().toLowerCase();
            String dept = model.getValueAt(i, 3).toString().toLowerCase();

            if (name.contains(keyword) || id.contains(keyword) || dept.contains(keyword)) {
                searchModel.addRow(new Object[]{
                    model.getValueAt(i, 0),
                    model.getValueAt(i, 1),
                    model.getValueAt(i, 2),
                    model.getValueAt(i, 3),
                    model.getValueAt(i, 4),
                    model.getValueAt(i, 5)
                });
            }
        }

        if (searchModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No records found matching: " + keyword);
            // 恢復原始資料
            table.setModel(model);
        } else {
            table.setModel(searchModel);
        }
        });

        JPanel buttons = new JPanel();
        buttons.add(btnView);
        buttons.add(btnEdit);
        buttons.add(btnDelete);
        buttons.add(btnSearch);

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    // ============================================================
    // 4️⃣ ANALYTICS DASHBOARD
    // ============================================================
    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("University Analytics Dashboard", SwingConstants.CENTER);

        String[] columns = {"Category", "Value"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        JButton btnGenerate = new JButton("Generate Overview Statistics");

        btnGenerate.addActionListener(e -> {
            model.setRowCount(0);
            model.addRow(new Object[]{"Active Admin Users", directory.getAdmins().size()});
            model.addRow(new Object[]{"Active Faculty Users", directory.getFaculties().size()});
            model.addRow(new Object[]{"Active Students", directory.getStudents().size()});
            model.addRow(new Object[]{"Active Registrars", directory.getRegistrars().size()});
            model.addRow(new Object[]{"Total Courses", directory.getCourses().size()});
            model.addRow(new Object[]{"Total Semesters", directory.getSemesters().size()});
            model.addRow(new Object[]{"Total Enrollments", directory.getEnrollments().size()});

            // 假設 TuitionPayment 已實作
            //double totalPaid = 0.0;
            //for (Student s : directory.getStudents()) {
           //     for (TuitionPayment t : s.getPayments()) {
            //        totalPaid += t.getAmount();
           //     }
           // }
           // model.addRow(new Object[]{"💰 Total Tuition Paid", String.format("$%.2f", totalPaid)});
        });

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnGenerate, BorderLayout.SOUTH);
        return panel;
    }

    // ============================================================
    // 5️⃣ PROFILE MANAGEMENT PANEL
    // ============================================================
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        JLabel lblName = new JLabel("Name:");
        JTextField txtName = new JTextField();
        JLabel lblEmail = new JLabel("Email:");
        JTextField txtEmail = new JTextField();
        JLabel lblPassword = new JLabel("Password:");
        JPasswordField txtPassword = new JPasswordField();
        JButton btnSave = new JButton("Save Changes");

        User current = authService.getCurrentUser();
        if (current != null && current.getPerson() != null) {
            Person p = current.getPerson();
            txtName.setText(p.getFullName());
            txtEmail.setText(p.getEmail());
        }

        btnSave.addActionListener(e -> {
            User user = authService.getCurrentUser();
            if (user != null) {
                Person p = user.getPerson();
                if (p != null) {
                    p.setFirstName(txtName.getText().trim());
                    p.setEmail(txtEmail.getText().trim());
                }
                user.setPassword(new String(txtPassword.getPassword()));
                JOptionPane.showMessageDialog(this, "Profile updated!");
            }
        });

        panel.add(lblName);
        panel.add(txtName);
        panel.add(lblEmail);
        panel.add(txtEmail);
        panel.add(lblPassword);
        panel.add(txtPassword);
        panel.add(new JLabel());
        panel.add(btnSave);

        return panel;
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Category", "Value"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jLabel2.setText("University Analytics Dashboard");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(382, 382, 382)
                .addComponent(jLabel2)
                .addContainerGap(398, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE))
        );

        jButton13.setText("Geneate Overview Statistics");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        setLayout(new java.awt.BorderLayout());

        jButton1.setText("Add Account");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Edit Account");

        jButton3.setText("Delete Account");

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Username", "Role", "University ID"
            }
        ));
        jScrollPane3.setViewportView(jTable3);

        jLabel3.setText("Manage User Accounts");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(382, 382, 382)
                        .addComponent(jLabel3))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 843, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(307, 307, 307)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 313, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        jTabbedPane1.addTab("User Account Management", jPanel1);

        jButton9.setText("View All");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("Edit Registrar Info");

        jButton11.setText("Delete Registrar");

        jLabel4.setText("Registrar Records Management");

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "University ID", "Name", "Email", "Phone"
            }
        ));
        jScrollPane4.setViewportView(jTable4);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(306, 306, 306)
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton11)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 910, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(377, 377, 377)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addGap(35, 35, 35)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jButton10)
                    .addComponent(jButton11))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Registrar Records Management", jPanel2);

        jButton5.setText("View All");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Edit Info");

        jButton7.setText("Delete Record");

        jButton8.setText("Search");

        jLabel5.setText("Student & Faculty Records Management");

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Type", "University ID", "Name", "Department", "Email", "Phone"
            }
        ));
        jScrollPane5.setViewportView(jTable5);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane5))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(362, 362, 362)
                        .addComponent(jLabel5)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(292, 292, 292)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addContainerGap(272, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addGap(35, 35, 35)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton6)
                    .addComponent(jButton7)
                    .addComponent(jButton8))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Student & Faculty Records", jPanel3);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(420);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Category", "Value"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setText("University Analytics Dashboard");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(382, 382, 382)
                .addComponent(jLabel1)
                .addContainerGap(362, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE))
        );

        jSplitPane1.setTopComponent(jPanel6);

        jButton12.setText("Geneate Overview Statistics");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jSplitPane1.setRightComponent(jButton12);

        jPanel4.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Analytics Dashboard", jPanel4);

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jButton4.setText("Save Changes");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel6.setText("Name:");

        jLabel7.setText("Email:");

        jLabel8.setText("Password:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 383, Short.MAX_VALUE)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                    .addContainerGap(445, Short.MAX_VALUE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(35, 35, 35)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(26, 26, 26)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(372, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Profile Management", jPanel5);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed
  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables
}
