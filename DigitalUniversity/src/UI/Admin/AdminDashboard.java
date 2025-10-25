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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 952, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 489, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
