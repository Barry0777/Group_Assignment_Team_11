/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package UI.Admin;

import javax.swing.*;
import java.awt.*;
import accesscontrol.*;
import model.*;
import utility.*;
import business.*;

/**
 *
 * @author USER
 */
public class AdminDashboard extends javax.swing.JPanel {
    private JTabbedPane tabbedPane;
private JPanel userAccountPanel;
private JPanel personRegistrationPanel;
private JPanel studentPanel;
private JPanel facultyPanel;
private JPanel registrarPanel;
private JPanel analyticsPanel;
private JPanel profilePanel;


private AdminService adminService;
private ReportService reportService;
private SearchService searchService;
private UniversityDirectory directory;
private AuthenticationService authService;

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
        userAccountPanel = createUserAccountPanel();
        personRegistrationPanel = new JPanel();  // placeholder
        studentPanel = new JPanel(); 
        facultyPanel = createFacultyPanel();
        registrarPanel = createRegistrarPanel();
        analyticsPanel = createAnalyticsPanel();
        profilePanel = createProfilePanel();


        tabbedPane.addTab("User Accounts", userAccountPanel);
        tabbedPane.addTab("Person Registration", personRegistrationPanel);
        tabbedPane.addTab("Student Records", studentPanel);
        tabbedPane.addTab("Faculty Records", facultyPanel);
        tabbedPane.addTab("Registrar Records", registrarPanel);
        tabbedPane.addTab("Analytics Dashboard", analyticsPanel);
        tabbedPane.addTab("Profile Management", profilePanel);


        add(tabbedPane, BorderLayout.CENTER);
    }
    private JPanel createUserAccountPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Manage User Accounts", SwingConstants.CENTER);
        JTable table = new JTable();
        JButton btnAdd = new JButton("Add Account");
        JButton btnEdit = new JButton("Edit Account");
        JButton btnDelete = new JButton("Delete Account");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
}


/** FACULTY RECORDS TAB */
private JPanel createFacultyPanel() {
JPanel panel = new JPanel(new BorderLayout());
JLabel label = new JLabel("Manage Faculty Records", SwingConstants.CENTER);
JTable table = new JTable();
JButton btnAssignCourse = new JButton("Assign to Course");


panel.add(label, BorderLayout.NORTH);
panel.add(new JScrollPane(table), BorderLayout.CENTER);
panel.add(btnAssignCourse, BorderLayout.SOUTH);
return panel;
}


/** REGISTRAR RECORDS TAB */
private JPanel createRegistrarPanel() {
JPanel panel = new JPanel(new BorderLayout());
JLabel label = new JLabel("Manage Registrar Records", SwingConstants.CENTER);
JTable table = new JTable();
JButton btnEdit = new JButton("Edit Registrar");
JButton btnDelete = new JButton("Delete Registrar");


JPanel buttonPanel = new JPanel();
buttonPanel.add(btnEdit);
buttonPanel.add(btnDelete);


panel.add(label, BorderLayout.NORTH);
panel.add(new JScrollPane(table), BorderLayout.CENTER);
panel.add(buttonPanel, BorderLayout.SOUTH);
return panel;
}


/** ANALYTICS TAB */
private JPanel createAnalyticsPanel() {
JPanel panel = new JPanel(new BorderLayout());
JLabel label = new JLabel("University Analytics Dashboard", SwingConstants.CENTER);
JTextArea reportArea = new JTextArea();
reportArea.setEditable(false);
JButton btnGenerate = new JButton("Generate Report");


panel.add(label, BorderLayout.NORTH);
panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
panel.add(btnGenerate, BorderLayout.SOUTH);
return panel;
}


/** PROFILE MANAGEMENT TAB */
private JPanel createProfilePanel() {
JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
JLabel lblName = new JLabel("Name:");
JTextField txtName = new JTextField();
JLabel lblEmail = new JLabel("Email:");
JTextField txtEmail = new JTextField();
JLabel lblPassword = new JLabel("Password:");
JPasswordField txtPassword = new JPasswordField();
JButton btnSave = new JButton("Save Changes");


panel.add(lblName); panel.add(txtName);
panel.add(lblEmail); panel.add(txtEmail);
panel.add(lblPassword); panel.add(txtPassword);
panel.add(new JLabel()); panel.add(btnSave);
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
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
