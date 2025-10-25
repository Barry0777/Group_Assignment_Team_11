/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package UI.Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import accesscontrol.*;
import business.*;
import model.*;
import utility.*;
import java.util.List;

/**
 *
 * @author USER
 */
public class AdminDashboard extends javax.swing.JPanel {
     private JTabbedPane tabbedPane;

    private AdminService adminService;
    private ReportService reportService;
    private SearchService searchService;
    private UniversityDirectory directory;
    private AuthenticationService authService;
    
    private Department findDepartmentByName(String deptName) {
    for (Department d : directory.getDepartments()) {
        if (d.getName().equalsIgnoreCase(deptName)) {
            return d;
        }
    }
    return null;
}

    /**
     * Creates new form AdminDashboard
     */
    public AdminDashboard(UniversityDirectory directory) {
        this.directory = directory;
        this.adminService = new AdminService();
        this.reportService = new ReportService();
        this.searchService = new SearchService();
        this.authService = AuthenticationService.getInstance();

        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();

        // Initialize tabs
        tabbedPane.addTab("User Accounts", createUserAccountPanel());
        tabbedPane.addTab("Person Registration", createPersonRegistrationPanel());
        tabbedPane.addTab("Student Records", createStudentPanel());
        tabbedPane.addTab("Faculty Records", createFacultyPanel());
        tabbedPane.addTab("Registrar Records", createRegistrarPanel());
        tabbedPane.addTab("Analytics Dashboard", createAnalyticsPanel());
        tabbedPane.addTab("Profile Management", createProfilePanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
    
     /** 1️⃣ USER ACCOUNT MANAGEMENT **/
    private JPanel createUserAccountPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Manage User Accounts", SwingConstants.CENTER);
        String[] columns = {"Username", "Role", "Active"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        JButton btnAdd = new JButton("Add Account");
        JButton btnEdit = new JButton("Edit Account");
        JButton btnDelete = new JButton("Delete Account");

        btnAdd.addActionListener(e -> {
    try {
        String username = JOptionPane.showInputDialog(this, "Enter username:");
        String password = JOptionPane.showInputDialog(this, "Enter password:");
        String roleInput = JOptionPane.showInputDialog(this, "Enter role (Admin / Faculty / Student / Registrar):");
        if (username == null || password == null || roleInput == null) return;

        String role = roleInput.trim().toUpperCase(); // 後端是用大寫：ADMIN/FACULTY/STUDENT/REGISTRAR

        // 讓管理員輸入要綁定的人（用 University ID 或 Email）
        String key = JOptionPane.showInputDialog(this, "Enter person's University ID or Email for this account:");
        if (key == null || key.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "A person (by ID or Email) is required to bind the account.");
            return;
        }

        // 依角色找出真正的 Person
        Person person = resolvePersonForRole(role, key.trim());
        if (person == null) {
            JOptionPane.showMessageDialog(this, "No matching " + role + " found for: " + key);
            return;
        }

        adminService.createUserAccount(username.trim(), password.trim(), role, person);
        JOptionPane.showMessageDialog(this, "Account created successfully!");
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
});



        btnDelete.addActionListener(e -> {
            String username = JOptionPane.showInputDialog("Enter username to delete:");
            try {
                adminService.deleteUserAccount(username);
                JOptionPane.showMessageDialog(this, "Account deleted.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    /** 2️⃣ PERSON REGISTRATION **/
    private JPanel createPersonRegistrationPanel() {
    JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
    
    JLabel lblType = new JLabel("Person Type:");
    JComboBox<String> cmbType = new JComboBox<>(new String[]{"Student", "Faculty", "Registrar", "Admin"});
    
    JLabel lblFirst = new JLabel("First Name:");
    JTextField txtFirst = new JTextField();
    
    JLabel lblLast = new JLabel("Last Name:");
    JTextField txtLast = new JTextField();
    
    JLabel lblEmail = new JLabel("Email:");
    JTextField txtEmail = new JTextField();
    
    JLabel lblPhone = new JLabel("Phone Number:");
    JTextField txtPhone = new JTextField();
    
    JLabel lblAddress = new JLabel("Address:");
    JTextField txtAddress = new JTextField();
    
    JLabel lblDeptOrProg = new JLabel("Department / Program:");
    JTextField txtDeptOrProg = new JTextField();
    
    JButton btnRegister = new JButton("Register");

    btnRegister.addActionListener(e -> {
        try {
            String type = (String) cmbType.getSelectedItem();
            String first = txtFirst.getText().trim();
            String last = txtLast.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String addr = txtAddress.getText().trim();
            String deptOrProg = txtDeptOrProg.getText().trim();

            if (first.isEmpty() || last.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "First name, last name, and email are required.");
                return;
            }

            switch (type) {
                case "Student":
                    adminService.registerStudent(first, last, email, phone, addr, deptOrProg);
                    break;
                case "Faculty":
                    Department dept = findDepartmentByName(deptOrProg);
                    if (dept == null) {
                        JOptionPane.showMessageDialog(this, "Department not found: " + deptOrProg);
                        return;
                    }
                    adminService.registerFaculty(first, last, email, phone, dept, "Office A", "Mon-Fri 9am-5pm");
                    break;
                case "Registrar":
                    adminService.registerRegistrar(first, last, email, phone, "Registrar Office", "Mon-Fri 9am-5pm");
                    break;
                case "Admin":
                    adminService.registerAdmin(first, last, email, phone, "System-Level");
                    break;
            }

            JOptionPane.showMessageDialog(this, type + " registered successfully!");

            // 清空表單
            txtFirst.setText("");
            txtLast.setText("");
            txtEmail.setText("");
            txtPhone.setText("");
            txtAddress.setText("");
            txtDeptOrProg.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    });

    panel.add(lblType); panel.add(cmbType);
    panel.add(lblFirst); panel.add(txtFirst);
    panel.add(lblLast); panel.add(txtLast);
    panel.add(lblEmail); panel.add(txtEmail);
    panel.add(lblPhone); panel.add(txtPhone);
    panel.add(lblAddress); panel.add(txtAddress);
    panel.add(lblDeptOrProg); panel.add(txtDeptOrProg);
    panel.add(new JLabel()); panel.add(btnRegister);

    return panel;
}


    /** 3️⃣ STUDENT RECORDS **/
   private JPanel createStudentPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    JLabel label = new JLabel("Manage Student Records", SwingConstants.CENTER);
    String[] columns = {"Student ID", "Name", "Program"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    JTable table = new JTable(model);

    JTextField txtSearch = new JTextField(15);
    JButton btnSearch = new JButton("Search");
    JButton btnDelete = new JButton("Delete Student");

    // 🔍 Search
    btnSearch.addActionListener(e -> {
        String keyword = txtSearch.getText().trim();
        model.setRowCount(0);
        try {
            java.util.List<Student> students = searchStudentByName(keyword);
            for (Student s : students) {
                model.addRow(new Object[]{
                    s.getUniversityId(),
                    s.getFirstName() + " " + s.getLastName(),
                    s.getProgram()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    });

    // 🗑️ Delete
    btnDelete.addActionListener(e -> {
        String id = JOptionPane.showInputDialog(this, "Enter Student ID to delete:");
        try {
            Student student = findStudentById(id);
            if (student == null) {
                JOptionPane.showMessageDialog(this, "Student not found!");
                return;
            }
            adminService.deleteStudent(student);
            JOptionPane.showMessageDialog(this, "Student deleted.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    });

    JPanel topPanel = new JPanel();
    topPanel.add(new JLabel("Search:"));
    topPanel.add(txtSearch);
    topPanel.add(btnSearch);
    topPanel.add(btnDelete);

    panel.add(label, BorderLayout.NORTH);
    panel.add(topPanel, BorderLayout.SOUTH);
    panel.add(new JScrollPane(table), BorderLayout.CENTER);

    return panel;
}



    /** 4️⃣ FACULTY RECORDS **/
    /** 4️⃣ FACULTY RECORDS **/
private JPanel createFacultyPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    JLabel label = new JLabel("Manage Faculty Records", SwingConstants.CENTER);
    JTable table = new JTable();
    JButton btnAssign = new JButton("Assign Faculty to Course");

    btnAssign.addActionListener(e -> {
        try {
            String facultyId = JOptionPane.showInputDialog(this, "Enter Faculty ID:");
            String courseId  = JOptionPane.showInputDialog(this, "Enter Course ID:");
            if (facultyId == null || courseId == null || facultyId.trim().isEmpty() || courseId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Both Faculty ID and Course ID are required.");
                return;
            }

            // 依 ID 找出實體
            Faculty faculty = findFacultyByIdOrEmail(facultyId.trim());
            if (faculty == null) {
                // 若用 ID 找不到，試著把輸入當 email
                faculty = findFacultyByIdOrEmail(facultyId.trim());
            }

            CourseOffering offering = findCourseOfferingByCourseId(courseId.trim());

            if (faculty == null) {
                JOptionPane.showMessageDialog(this, "Faculty not found: " + facultyId);
                return;
            }
            if (offering == null) {
                JOptionPane.showMessageDialog(this, "Course offering not found for courseId: " + courseId);
                return;
            }

            adminService.assignFacultyToCourse(faculty, offering);
            JOptionPane.showMessageDialog(this, "Faculty assigned to course successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    panel.add(label, BorderLayout.NORTH);
    panel.add(new JScrollPane(table), BorderLayout.CENTER);
    panel.add(btnAssign, BorderLayout.SOUTH);
    return panel;
}


    /** 5️⃣ REGISTRAR RECORDS **/
private JPanel createRegistrarPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    JLabel label = new JLabel("Manage Registrar Records", SwingConstants.CENTER);
    JTable table = new JTable();
    JButton btnEdit = new JButton("Edit Registrar Info");
    JButton btnDelete = new JButton("Delete Registrar");

    btnDelete.addActionListener(e -> {
        String idOrEmail = JOptionPane.showInputDialog(this, "Enter Registrar ID or Email:");
        try {
            if (idOrEmail == null || idOrEmail.trim().isEmpty()) return;

            Registrar registrar = findRegistrarByIdOrEmail(idOrEmail.trim());
            if (registrar == null) {
                JOptionPane.showMessageDialog(this, "Registrar not found: " + idOrEmail);
                return;
            }

            adminService.deleteRegistrar(registrar);
            JOptionPane.showMessageDialog(this, "Registrar deleted.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(btnEdit);
    buttonPanel.add(btnDelete);

    panel.add(label, BorderLayout.NORTH);
    panel.add(new JScrollPane(table), BorderLayout.CENTER);
    panel.add(buttonPanel, BorderLayout.SOUTH);
    return panel;
}

    /** 6️⃣ ANALYTICS DASHBOARD **/
    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("University Analytics Dashboard", SwingConstants.CENTER);
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        JButton btnGenerate = new JButton("Generate Report");

        btnGenerate.addActionListener(e -> {
            String report = reportService.generateAdminDashboard();
            reportArea.setText(report);
        });

        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        panel.add(btnGenerate, BorderLayout.SOUTH);
        return panel;
    }

    /** 7️⃣ PROFILE MANAGEMENT **/
private JPanel createProfilePanel() {
    JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
    JLabel lblName = new JLabel("Name:");
    JTextField txtName = new JTextField();
    JLabel lblEmail = new JLabel("Email:");
    JTextField txtEmail = new JTextField();
    JLabel lblPassword = new JLabel("Password:");
    JPasswordField txtPassword = new JPasswordField();
    JButton btnSave = new JButton("Save Changes");

    // 預載目前使用者資料
    User current = authService.getCurrentUser();
    if (current != null && current.getPerson() != null) {
        Person p = current.getPerson();
        String full = (p.getFirstName() == null ? "" : p.getFirstName()) +
                      (p.getLastName()  == null ? "" : (" " + p.getLastName()));
        txtName.setText(full.trim());
        txtEmail.setText(p.getEmail() == null ? "" : p.getEmail());
    }

    btnSave.addActionListener(e -> {
        try {
            User u = authService.getCurrentUser();
            if (u == null) {
                JOptionPane.showMessageDialog(this, "No user logged in.");
                return;
            }
            Person p = u.getPerson();
            if (p != null) {
                // 名字拆成 first/last（簡易拆分）
                String name = txtName.getText().trim();
                if (!name.isEmpty()) {
                    String[] parts = name.split("\\s+", 2);
                    p.setFirstName(parts[0]);
                    p.setLastName(parts.length > 1 ? parts[1] : "");
                }
                if (!txtEmail.getText().trim().isEmpty()) {
                    p.setEmail(txtEmail.getText().trim());
                }
            }
            String newPw = new String(txtPassword.getPassword()).trim();
            if (!newPw.isEmpty()) {
                u.setPassword(newPw);
            }
            JOptionPane.showMessageDialog(this, "Profile updated!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    panel.add(lblName); panel.add(txtName);
    panel.add(lblEmail); panel.add(txtEmail);
    panel.add(lblPassword); panel.add(txtPassword);
    panel.add(new JLabel()); panel.add(btnSave);
    return panel;
}

    /** 
 * Helper: Search students by name (local search)
 */
private java.util.List<Student> searchStudentByName(String keyword) {
    java.util.List<Student> result = new java.util.ArrayList<>();
    if (keyword == null || keyword.trim().isEmpty()) {
        return result;
    }

    for (Student s : directory.getStudents()) {
        String fullName = s.getFirstName() + " " + s.getLastName();
        if (fullName.toLowerCase().contains(keyword.toLowerCase())) {
            result.add(s);
        }
    }
    return result;
}

// 依角色用 ID 或 Email 尋找對應 Person
private Person resolvePersonForRole(String role, String key) {
    switch (role) {
        case "STUDENT":
            return findStudentByIdOrEmail(key);
        case "FACULTY":
            return findFacultyByIdOrEmail(key);
        case "REGISTRAR":
            return findRegistrarByIdOrEmail(key);
        case "ADMIN":
            return findAdminByIdOrEmail(key);
        default:
            return null;
    }
}

private Student findStudentByIdOrEmail(String key) {
    for (Student s : directory.getStudents()) {
        if (equalsIgnoreCaseSafe(s.getUniversityId(), key) || equalsIgnoreCaseSafe(s.getEmail(), key)) {
            return s;
        }
    }
    return null;
}

private Faculty findFacultyByIdOrEmail(String key) {
    for (Faculty f : directory.getFaculty()) {
        if (equalsIgnoreCaseSafe(f.getUniversityId(), key) || equalsIgnoreCaseSafe(f.getEmail(), key)) {
            return f;
        }
    }
    return null;
}

private Registrar findRegistrarByIdOrEmail(String key) {
    for (Registrar r : directory.getRegistrars()) {
        if (equalsIgnoreCaseSafe(r.getUniversityId(), key) || equalsIgnoreCaseSafe(r.getEmail(), key)) {
            return r;
        }
    }
    return null;
}

private Admin findAdminByIdOrEmail(String key) {
    for (Admin a : directory.getAdmins()) {
        if (equalsIgnoreCaseSafe(a.getUniversityId(), key) || equalsIgnoreCaseSafe(a.getEmail(), key)) {
            return a;
        }
    }
    return null;
}

private boolean equalsIgnoreCaseSafe(String a, String b) {
    return a != null && b != null && a.equalsIgnoreCase(b);
}

/** 
 * Helper: Find student by university ID
 */
private Student findStudentById(String id) {
    if (id == null || id.trim().isEmpty()) {
        return null;
    }

    for (Student s : directory.getStudents()) {
        if (s.getUniversityId().equalsIgnoreCase(id)) {
            return s;
        }
    }
    return null;
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
