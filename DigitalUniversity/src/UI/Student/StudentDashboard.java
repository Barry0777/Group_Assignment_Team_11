/*
 * Student Dashboard - Complete Implementation
 * Author: [Your Name - Student Use Case]
 */
package UI.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import accesscontrol.*;
import business.*;
import model.*;
import utility.*;

/**
 * StudentDashboard - Main dashboard for student operations
 */
public class StudentDashboard extends JPanel {
    
    private JTabbedPane tabbedPane;
    private JPanel courseRegistrationPanel;
    private JPanel transcriptPanel;
    private JPanel graduationAuditPanel;
    private JPanel financialPanel;
    private JPanel courseworkPanel;
    private JPanel profilePanel;
    
    private StudentService studentService;
    private SearchService searchService;
    private UniversityDirectory directory;
    private AuthenticationService authService;
    private Student currentStudent;
    
    // Tables
    private JTable courseTable;
    private DefaultTableModel courseTableModel;
    private JTable transcriptTable;
    private DefaultTableModel transcriptTableModel;
    private JTable paymentHistoryTable;
    private DefaultTableModel paymentHistoryTableModel;
    private JTable assignmentTable;
    private DefaultTableModel assignmentTableModel;
    
    /**
     * Constructor
     */
    public StudentDashboard(UniversityDirectory directory) {
        this.directory = directory;
        this.studentService = new StudentService();
        this.searchService = new SearchService();
        this.authService = AuthenticationService.getInstance();
        this.currentStudent = (Student) authService.getCurrentUser().getPerson();
        
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        
        // Initialize all tabs
        courseRegistrationPanel = createCourseRegistrationPanel();
        transcriptPanel = createTranscriptPanel();
        graduationAuditPanel = createGraduationAuditPanel();
        financialPanel = createFinancialPanel();
        courseworkPanel = createCourseworkPanel();
        profilePanel = createProfilePanel();
        
        // Add tabs
        tabbedPane.addTab("Course Registration", courseRegistrationPanel);
        tabbedPane.addTab("Transcript", transcriptPanel);
        tabbedPane.addTab("Graduation Audit", graduationAuditPanel);
        tabbedPane.addTab("Financial Management", financialPanel);
        tabbedPane.addTab("Coursework", courseworkPanel);
        tabbedPane.addTab("Profile", profilePanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Load initial data
        loadCourseRegistrationData();
        loadGraduationAuditData();
        loadFinancialData();
    }
    
    // ========== COURSE REGISTRATION PANEL ==========
    
    private JPanel createCourseRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top Panel - Search Section
        JPanel searchPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Courses (3 Methods)"));
        
        JLabel lblSearchId = new JLabel("Search by Course ID:");
        JTextField txtSearchId = new JTextField();
        JButton btnSearchId = new JButton("Search by ID");
        
        JLabel lblSearchInstructor = new JLabel("Search by Instructor:");
        JTextField txtSearchInstructor = new JTextField();
        JButton btnSearchInstructor = new JButton("Search by Instructor");
        
        JLabel lblSearchTitle = new JLabel("Search by Course Title:");
        JTextField txtSearchTitle = new JTextField();
        JButton btnSearchTitle = new JButton("Search by Title");
        
        searchPanel.add(lblSearchId);
        searchPanel.add(txtSearchId);
        searchPanel.add(new JLabel());
        searchPanel.add(btnSearchId);
        
        searchPanel.add(lblSearchInstructor);
        searchPanel.add(txtSearchInstructor);
        searchPanel.add(new JLabel());
        searchPanel.add(btnSearchInstructor);
        
        searchPanel.add(lblSearchTitle);
        searchPanel.add(txtSearchTitle);
        searchPanel.add(new JLabel());
        searchPanel.add(btnSearchTitle);
        
        // Center Panel - Course Table
        String[] columns = {"Course ID", "Title", "Instructor", "Credits", "Schedule", "Room", "Available Seats", "Status"};
        courseTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(courseTableModel);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        
        // Bottom Panel - Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnEnroll = new JButton("Enroll in Selected Course");
        JButton btnDrop = new JButton("Drop Selected Course");
        JButton btnRefresh = new JButton("Refresh / Show All");
        JLabel lblCredits = new JLabel("Current Semester Credits: 0 / 8");
        
        btnEnroll.setBackground(new Color(46, 125, 50));
        btnEnroll.setForeground(Color.WHITE);
        btnDrop.setBackground(new Color(211, 47, 47));
        btnDrop.setForeground(Color.WHITE);
        
        actionPanel.add(lblCredits);
        actionPanel.add(btnEnroll);
        actionPanel.add(btnDrop);
        actionPanel.add(btnRefresh);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        // Event Handlers
        btnSearchId.addActionListener(e -> searchCourseById(txtSearchId.getText().trim()));
        btnSearchInstructor.addActionListener(e -> searchCourseByInstructor(txtSearchInstructor.getText().trim()));
        btnSearchTitle.addActionListener(e -> searchCourseByTitle(txtSearchTitle.getText().trim()));
        btnRefresh.addActionListener(e -> loadCourseRegistrationData());
        btnEnroll.addActionListener(e -> enrollInCourse());
        btnDrop.addActionListener(e -> dropCourse());
        
        // Update credits label periodically
        Timer timer = new Timer(1000, e -> {
            Semester currentSemester = getCurrentSemester();
            if (currentSemester != null) {
                int credits = currentStudent.getCurrentSemesterCredits(currentSemester);
                lblCredits.setText("Current Semester Credits: " + credits + " / 8");
            }
        });
        timer.start();
        
        return panel;
    }
    
    private void loadCourseRegistrationData() {
        courseTableModel.setRowCount(0);
        Semester currentSemester = getCurrentSemester();
        
        if (currentSemester == null) {
            JOptionPane.showMessageDialog(this, "No active semester found.");
            return;
        }
        
        ArrayList<CourseOffering> offerings = directory.getCourseOfferingsBySemester(currentSemester);
        
        for (CourseOffering co : offerings) {
            String status = isEnrolled(co) ? "Enrolled" : 
                           (co.isEnrollmentOpen() && co.hasAvailableSeats() ? "Available" : "Closed");
            
            courseTableModel.addRow(new Object[]{
                co.getCourse().getCourseId(),
                co.getCourse().getTitle(),
                co.getInstructor().getFullName(),
                co.getCourse().getCreditHours(),
                co.getSchedule(),
                co.getRoomLocation(),
                co.getAvailableSeats(),
                status
            });
        }
    }
    
    private void searchCourseById(String courseId) {
        if (ValidationUtility.isNullOrEmpty(courseId)) {
            JOptionPane.showMessageDialog(this, "Please enter a course ID to search.");
            return;
        }
        
        courseTableModel.setRowCount(0);
        Semester currentSemester = getCurrentSemester();
        ArrayList<CourseOffering> results = studentService.searchByCourseId(courseId, currentSemester);
        
        for (CourseOffering co : results) {
            String status = isEnrolled(co) ? "Enrolled" : 
                           (co.isEnrollmentOpen() && co.hasAvailableSeats() ? "Available" : "Closed");
            
            courseTableModel.addRow(new Object[]{
                co.getCourse().getCourseId(),
                co.getCourse().getTitle(),
                co.getInstructor().getFullName(),
                co.getCourse().getCreditHours(),
                co.getSchedule(),
                co.getRoomLocation(),
                co.getAvailableSeats(),
                status
            });
        }
        
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses found with ID: " + courseId);
        }
    }
    
    private void searchCourseByInstructor(String instructor) {
        if (ValidationUtility.isNullOrEmpty(instructor)) {
            JOptionPane.showMessageDialog(this, "Please enter an instructor name to search.");
            return;
        }
        
        courseTableModel.setRowCount(0);
        Semester currentSemester = getCurrentSemester();
        ArrayList<CourseOffering> results = studentService.searchByInstructor(instructor, currentSemester);
        
        for (CourseOffering co : results) {
            String status = isEnrolled(co) ? "Enrolled" : 
                           (co.isEnrollmentOpen() && co.hasAvailableSeats() ? "Available" : "Closed");
            
            courseTableModel.addRow(new Object[]{
                co.getCourse().getCourseId(),
                co.getCourse().getTitle(),
                co.getInstructor().getFullName(),
                co.getCourse().getCreditHours(),
                co.getSchedule(),
                co.getRoomLocation(),
                co.getAvailableSeats(),
                status
            });
        }
        
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses found with instructor: " + instructor);
        }
    }
    
    private void searchCourseByTitle(String title) {
        if (ValidationUtility.isNullOrEmpty(title)) {
            JOptionPane.showMessageDialog(this, "Please enter a course title to search.");
            return;
        }
        
        courseTableModel.setRowCount(0);
        Semester currentSemester = getCurrentSemester();
        ArrayList<CourseOffering> results = studentService.searchByTitle(title, currentSemester);
        
        for (CourseOffering co : results) {
            String status = isEnrolled(co) ? "Enrolled" : 
                           (co.isEnrollmentOpen() && co.hasAvailableSeats() ? "Available" : "Closed");
            
            courseTableModel.addRow(new Object[]{
                co.getCourse().getCourseId(),
                co.getCourse().getTitle(),
                co.getInstructor().getFullName(),
                co.getCourse().getCreditHours(),
                co.getSchedule(),
                co.getRoomLocation(),
                co.getAvailableSeats(),
                status
            });
        }
        
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses found with title: " + title);
        }
    }
    
    private void enrollInCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to enroll in.");
            return;
        }
        
        String courseId = (String) courseTableModel.getValueAt(selectedRow, 0);
        String status = (String) courseTableModel.getValueAt(selectedRow, 7);
        
        if (status.equals("Enrolled")) {
            JOptionPane.showMessageDialog(this, "You are already enrolled in this course.");
            return;
        }
        
        if (!status.equals("Available")) {
            JOptionPane.showMessageDialog(this, "This course is not available for enrollment.");
            return;
        }
        
        try {
            CourseOffering offering = findCourseOfferingById(courseId);
            if (offering == null) {
                JOptionPane.showMessageDialog(this, "Course offering not found.");
                return;
            }
            
            Enrollment enrollment = studentService.enrollInCourse(currentStudent, offering);
            JOptionPane.showMessageDialog(this, 
                "Successfully enrolled in " + courseId + "!\n" +
                "Tuition charged: $" + String.format("%.2f", enrollment.getTuitionAmount()));
            
            loadCourseRegistrationData();
            loadFinancialData();
            
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), 
                "Enrollment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void dropCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to drop.");
            return;
        }
        
        String courseId = (String) courseTableModel.getValueAt(selectedRow, 0);
        String status = (String) courseTableModel.getValueAt(selectedRow, 7);
        
        if (!status.equals("Enrolled")) {
            JOptionPane.showMessageDialog(this, "You are not enrolled in this course.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to drop " + courseId + "?", 
            "Confirm Drop", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            CourseOffering offering = findCourseOfferingById(courseId);
            Enrollment enrollment = findEnrollment(offering);
            
            if (enrollment == null) {
                JOptionPane.showMessageDialog(this, "Enrollment not found.");
                return;
            }
            
            boolean refunded = enrollment.isPaid();
            studentService.dropCourse(currentStudent, enrollment);
            
            String message = "Successfully dropped " + courseId + ".";
            if (refunded) {
                message += "\nRefund of $" + String.format("%.2f", enrollment.getTuitionAmount()) + " applied.";
            }
            
            JOptionPane.showMessageDialog(this, message);
            loadCourseRegistrationData();
            loadFinancialData();
            
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), 
                "Drop Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ========== TRANSCRIPT PANEL ==========
    
    private JPanel createTranscriptPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top Panel - Semester Filter
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel lblSemester = new JLabel("Filter by Semester:");
        JComboBox<String> cmbSemester = new JComboBox<>();
        cmbSemester.addItem("All Semesters");
        for (Semester s : directory.getSemesters()) {
            cmbSemester.addItem(s.getFullName());
        }
        JButton btnViewTranscript = new JButton("View Transcript");
        
        topPanel.add(lblSemester);
        topPanel.add(cmbSemester);
        topPanel.add(btnViewTranscript);
        
        // Center Panel - Transcript Table
        String[] columns = {"Term", "Course ID", "Course Name", "Credits", "Grade", "Grade Points", "Quality Points"};
        transcriptTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transcriptTable = new JTable(transcriptTableModel);
        JScrollPane scrollPane = new JScrollPane(transcriptTable);
        
        // Bottom Panel - GPA Information
        JPanel gpaPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        gpaPanel.setBorder(BorderFactory.createTitledBorder("GPA Information"));
        
        JLabel lblTermGPA = new JLabel("Term GPA:");
        JLabel lblTermGPAValue = new JLabel("N/A");
        JLabel lblOverallGPA = new JLabel("Overall GPA:");
        JLabel lblOverallGPAValue = new JLabel(String.format("%.2f", currentStudent.getOverallGPA()));
        JLabel lblStanding = new JLabel("Academic Standing:");
        JLabel lblStandingValue = new JLabel(currentStudent.getAcademicStanding());
        
        // Color code standing
        if (currentStudent.getAcademicStanding().equals("Good Standing")) {
            lblStandingValue.setForeground(new Color(46, 125, 50));
        } else if (currentStudent.getAcademicStanding().equals("Academic Warning")) {
            lblStandingValue.setForeground(new Color(255, 152, 0));
        } else {
            lblStandingValue.setForeground(new Color(211, 47, 47));
        }
        
        gpaPanel.add(lblTermGPA);
        gpaPanel.add(lblTermGPAValue);
        gpaPanel.add(lblOverallGPA);
        gpaPanel.add(lblOverallGPAValue);
        gpaPanel.add(lblStanding);
        gpaPanel.add(lblStandingValue);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(gpaPanel, BorderLayout.SOUTH);
        
        // Event Handler
        btnViewTranscript.addActionListener(e -> {
            // Check if tuition is paid
            if (!studentService.canViewTranscript(currentStudent)) {
                JOptionPane.showMessageDialog(this, 
                    "You cannot view your transcript until all tuition is paid.\n" +
                    "Current Balance: $" + String.format("%.2f", currentStudent.getAccountBalance()) + "\n" +
                    "Please go to Financial Management to pay tuition.",
                    "Transcript Access Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String selectedSemester = (String) cmbSemester.getSelectedItem();
            loadTranscriptData(selectedSemester, lblTermGPAValue, lblOverallGPAValue, lblStandingValue);
        });
        
        return panel;
    }
    
    private void loadTranscriptData(String semesterName, JLabel termGPALabel, JLabel overallGPALabel, JLabel standingLabel) {
        transcriptTableModel.setRowCount(0);
        
        ArrayList<Enrollment> enrollments;
        double termGPA = 0.0;
        
        if (semesterName.equals("All Semesters")) {
            enrollments = studentService.getCompleteTranscript(currentStudent);
        } else {
            Semester semester = findSemesterByName(semesterName);
            if (semester != null) {
                enrollments = studentService.getTranscriptBySemester(currentStudent, semester);
                termGPA = studentService.calculateTermGPA(currentStudent, semester);
            } else {
                return;
            }
        }
        
        for (Enrollment e : enrollments) {
            CourseOffering co = e.getCourseOffering();
            String grade = e.getGrade() != null ? e.getGrade() : "In Progress";
            
            transcriptTableModel.addRow(new Object[]{
                co.getSemester().getFullName(),
                co.getCourse().getCourseId(),
                co.getCourse().getTitle(),
                co.getCourse().getCreditHours(),
                grade,
                String.format("%.2f", e.getGradePoints()),
                String.format("%.2f", e.getQualityPoints())
            });
        }
        
        // Update GPA labels
        if (!semesterName.equals("All Semesters")) {
            termGPALabel.setText(String.format("%.2f", termGPA));
        } else {
            termGPALabel.setText("N/A (All Semesters)");
        }
        
        overallGPALabel.setText(String.format("%.2f", currentStudent.getOverallGPA()));
        standingLabel.setText(currentStudent.getAcademicStanding());
        
        // Update standing color
        if (currentStudent.getAcademicStanding().equals("Good Standing")) {
            standingLabel.setForeground(new Color(46, 125, 50));
        } else if (currentStudent.getAcademicStanding().equals("Academic Warning")) {
            standingLabel.setForeground(new Color(255, 152, 0));
        } else {
            standingLabel.setForeground(new Color(211, 47, 47));
        }
    }
    
    // ========== GRADUATION AUDIT PANEL ==========
    
    private JPanel createGraduationAuditPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel contentPanel = new JPanel(new GridLayout(8, 2, 15, 15));
        contentPanel.setBorder(BorderFactory.createTitledBorder("MSIS Graduation Requirements"));
        
        JLabel lblTitle = new JLabel("Graduation Audit", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel lblCreditsCompleted = new JLabel("Credits Completed:");
        JLabel lblCreditsValue = new JLabel("0 / 32");
        lblCreditsValue.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel lblCreditsRemaining = new JLabel("Credits Remaining:");
        JLabel lblCreditsRemainingValue = new JLabel("32");
        lblCreditsRemainingValue.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel lblCoreRequired = new JLabel("Core Course (INFO 5100):");
        JLabel lblCoreStatus = new JLabel("✗ Not Completed");
        lblCoreStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblCoreStatus.setForeground(Color.RED);
        
        JLabel lblElectives = new JLabel("Elective Courses:");
        JLabel lblElectivesValue = new JLabel("0 credits");
        
        JLabel lblGPA = new JLabel("Overall GPA:");
        JLabel lblGPAValue = new JLabel("0.00");
        
        JLabel lblEligibility = new JLabel("Graduation Eligibility:");
        JLabel lblEligibilityValue = new JLabel("✗ Not Eligible Yet");
        lblEligibilityValue.setFont(new Font("Arial", Font.BOLD, 18));
        lblEligibilityValue.setForeground(Color.RED);
        
        JButton btnRefresh = new JButton("Refresh Status");
        
        contentPanel.add(lblCreditsCompleted);
        contentPanel.add(lblCreditsValue);
        contentPanel.add(lblCreditsRemaining);
        contentPanel.add(lblCreditsRemainingValue);
        contentPanel.add(lblCoreRequired);
        contentPanel.add(lblCoreStatus);
        contentPanel.add(lblElectives);
        contentPanel.add(lblElectivesValue);
        contentPanel.add(lblGPA);
        contentPanel.add(lblGPAValue);
        contentPanel.add(lblEligibility);
        contentPanel.add(lblEligibilityValue);
        contentPanel.add(new JLabel());
        contentPanel.add(btnRefresh);
        
        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Store labels for updates
        JLabel[] labels = {lblCreditsValue, lblCreditsRemainingValue, lblCoreStatus, 
                          lblElectivesValue, lblGPAValue, lblEligibilityValue};
        
        btnRefresh.addActionListener(e -> updateGraduationAudit(labels));
        
        return panel;
    }
    
    private void loadGraduationAuditData() {
        // Will be updated when tab is opened or refresh is clicked
    }
    
    private void updateGraduationAudit(JLabel[] labels) {
        studentService.updateCreditsCompleted(currentStudent);
        HashMap<String, Object> status = studentService.getGraduationStatus(currentStudent);
        
        int totalCredits = (int) status.get("totalCredits");
        int requiredCredits = (int) status.get("requiredCredits");
        int remaining = (int) status.get("creditsRemaining");
        boolean hasCore = (boolean) status.get("hasCoreCourse");
        boolean eligible = (boolean) status.get("isEligible");
        double gpa = (double) status.get("overallGPA");
        
        int electiveCredits = hasCore ? totalCredits - 4 : totalCredits;
        
        labels[0].setText(totalCredits + " / " + requiredCredits);
        labels[1].setText(String.valueOf(remaining));
        
        if (hasCore) {
            labels[2].setText("✓ Completed");
            labels[2].setForeground(new Color(46, 125, 50));
        } else {
            labels[2].setText("✗ Not Completed");
            labels[2].setForeground(Color.RED);
        }
        
        labels[3].setText(electiveCredits + " credits");
        labels[4].setText(String.format("%.2f", gpa));
        
        if (eligible) {
            labels[5].setText("✓ ELIGIBLE TO GRADUATE");
            labels[5].setForeground(new Color(46, 125, 50));
        } else {
            labels[5].setText("✗ Not Eligible Yet");
            labels[5].setForeground(Color.RED);
        }
    }
    
    // ========== FINANCIAL MANAGEMENT PANEL ==========
    
    private JPanel createFinancialPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top Panel - Balance and Payment
        JPanel topPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Tuition Payment"));
        
        JLabel lblBalance = new JLabel("Current Balance:");
        JLabel lblBalanceValue = new JLabel("$0.00");
        lblBalanceValue.setFont(new Font("Arial", Font.BOLD, 16));
        lblBalanceValue.setForeground(Color.RED);
        
        JLabel lblPaymentAmount = new JLabel("Payment Amount:");
        JTextField txtPaymentAmount = new JTextField();
        
        JButton btnPayFull = new JButton("Pay Full Balance");
        JButton btnPayCustom = new JButton("Pay Custom Amount");
        
        btnPayFull.setBackground(new Color(46, 125, 50));
        btnPayFull.setForeground(Color.WHITE);
        btnPayCustom.setBackground(new Color(33, 150, 243));
        btnPayCustom.setForeground(Color.WHITE);
        
        topPanel.add(lblBalance);
        topPanel.add(lblBalanceValue);
        topPanel.add(lblPaymentAmount);
        topPanel.add(txtPaymentAmount);
        topPanel.add(new JLabel());
        topPanel.add(btnPayFull);
        topPanel.add(new JLabel());
        topPanel.add(btnPayCustom);
        
        // Center Panel - Payment History
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Payment History"));
        
        String[] columns = {"Payment ID", "Date", "Amount", "Balance After", "Description"};
        paymentHistoryTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paymentHistoryTable = new JTable(paymentHistoryTableModel);
        JScrollPane scrollPane = new JScrollPane(paymentHistoryTable);
        
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Event Handlers
        btnPayFull.addActionListener(e -> payFullBalance(lblBalanceValue, txtPaymentAmount));
        btnPayCustom.addActionListener(e -> payCustomAmount(txtPaymentAmount.getText(), lblBalanceValue, txtPaymentAmount));
        
        return panel;
    }
    
    private void loadFinancialData() {
        // Update balance display
        Component[] components = financialPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel topPanel = (JPanel) comp;
                Component[] topComponents = topPanel.getComponents();
                for (Component c : topComponents) {
                    if (c instanceof JLabel) {
                        JLabel label = (JLabel) c;
                        if (label.getText().startsWith("$")) {
                            double balance = currentStudent.getAccountBalance();
                            label.setText("$" + String.format("%.2f", balance));
                            if (balance > 0) {
                                label.setForeground(Color.RED);
                            } else {
                                label.setForeground(new Color(46, 125, 50));
                            }
                            break;
                        }
                    }
                }
                break;
            }
        }
        
        // Load payment history
        paymentHistoryTableModel.setRowCount(0);
        ArrayList<TuitionPayment> payments = studentService.getPaymentHistory(currentStudent);
        
        for (TuitionPayment payment : payments) {
            paymentHistoryTableModel.addRow(new Object[]{
                payment.getPaymentId(),
                payment.getPaymentDate().toString(),
                "$" + String.format("%.2f", payment.getAmount()),
                "$" + String.format("%.2f", currentStudent.getAccountBalance()),
                payment.getDescription()
            });
        }
    }
    
    private void payFullBalance(JLabel balanceLabel, JTextField amountField) {
        double balance = currentStudent.getAccountBalance();
        
        if (balance <= 0) {
            JOptionPane.showMessageDialog(this, 
                "No balance to pay. Your account is current.",
                "No Payment Needed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Pay full balance of $" + String.format("%.2f", balance) + "?",
            "Confirm Payment", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            TuitionPayment payment = studentService.payTuition(currentStudent, balance);
            JOptionPane.showMessageDialog(this, 
                "Payment successful!\n" +
                "Amount Paid: $" + String.format("%.2f", balance) + "\n" +
                "New Balance: $" + String.format("%.2f", currentStudent.getAccountBalance()));
            
            balanceLabel.setText("$" + String.format("%.2f", currentStudent.getAccountBalance()));
            balanceLabel.setForeground(new Color(46, 125, 50));
            amountField.setText("");
            loadFinancialData();
            
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), 
                "Payment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void payCustomAmount(String amountStr, JLabel balanceLabel, JTextField amountField) {
        if (ValidationUtility.isNullOrEmpty(amountStr)) {
            JOptionPane.showMessageDialog(this, "Please enter a payment amount.");
            return;
        }
        
        if (!ValidationUtility.isPositiveDouble(amountStr)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive amount.");
            return;
        }
        
        double amount = Double.parseDouble(amountStr);
        double balance = currentStudent.getAccountBalance();
        
        if (balance <= 0) {
            JOptionPane.showMessageDialog(this, 
                "No balance to pay. Your account is current.",
                "No Payment Needed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (amount > balance) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Payment amount ($" + String.format("%.2f", amount) + ") exceeds balance ($" + 
                String.format("%.2f", balance) + ").\n" +
                "Do you want to pay the full balance instead?",
                "Amount Exceeds Balance", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                amount = balance;
            } else {
                return;
            }
        }
        
        try {
            TuitionPayment payment = studentService.payTuition(currentStudent, amount);
            JOptionPane.showMessageDialog(this, 
                "Payment successful!\n" +
                "Amount Paid: $" + String.format("%.2f", amount) + "\n" +
                "New Balance: $" + String.format("%.2f", currentStudent.getAccountBalance()));
            
            balanceLabel.setText("$" + String.format("%.2f", currentStudent.getAccountBalance()));
            if (currentStudent.getAccountBalance() <= 0) {
                balanceLabel.setForeground(new Color(46, 125, 50));
            }
            amountField.setText("");
            loadFinancialData();
            
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), 
                "Payment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ========== COURSEWORK PANEL ==========
    
    private JPanel createCourseworkPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top Panel - Course Selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblCourse = new JLabel("Select Course:");
        JComboBox<String> cmbCourse = new JComboBox<>();
        JButton btnLoadAssignments = new JButton("Load Assignments");
        
        topPanel.add(lblCourse);
        topPanel.add(cmbCourse);
        topPanel.add(btnLoadAssignments);
        
        // Populate courses
        for (Enrollment e : currentStudent.getEnrollments()) {
            if (e.isActive()) {
                cmbCourse.addItem(e.getCourseOffering().getCourse().getCourseId() + " - " + 
                                 e.getCourseOffering().getCourse().getTitle());
            }
        }
        
        // Center Panel - Assignments Table
        String[] columns = {"Assignment", "Due Date", "Max Points", "Your Score", "Percentage", "Status"};
        assignmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignmentTable = new JTable(assignmentTableModel);
        JScrollPane scrollPane = new JScrollPane(assignmentTable);
        
        // Bottom Panel - Progress Summary
        JPanel bottomPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Course Progress"));
        
        JLabel lblCompleted = new JLabel("Assignments Completed:");
        JLabel lblCompletedValue = new JLabel("0 / 0");
        JLabel lblCurrentGrade = new JLabel("Current Grade:");
        JLabel lblCurrentGradeValue = new JLabel("N/A");
        JLabel lblPercentage = new JLabel("Overall Percentage:");
        JLabel lblPercentageValue = new JLabel("0.00%");
        
        bottomPanel.add(lblCompleted);
        bottomPanel.add(lblCompletedValue);
        bottomPanel.add(lblCurrentGrade);
        bottomPanel.add(lblCurrentGradeValue);
        bottomPanel.add(lblPercentage);
        bottomPanel.add(lblPercentageValue);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Event Handler
        btnLoadAssignments.addActionListener(e -> {
            String selected = (String) cmbCourse.getSelectedItem();
            if (selected != null) {
                String courseId = selected.split(" - ")[0];
                loadAssignmentsForCourse(courseId, lblCompletedValue, lblCurrentGradeValue, lblPercentageValue);
            }
        });
        
        return panel;
    }
    
    private void loadAssignmentsForCourse(String courseId, JLabel completedLabel, 
                                         JLabel gradeLabel, JLabel percentageLabel) {
        assignmentTableModel.setRowCount(0);
        
        CourseOffering offering = findCourseOfferingById(courseId);
        if (offering == null) {
            return;
        }
        
        ArrayList<Assignment> assignments = studentService.getCourseAssignments(currentStudent, offering);
        int completed = 0;
        
        for (Assignment assignment : assignments) {
            Double score = studentService.getAssignmentScore(currentStudent, assignment);
            String scoreStr = score != null ? String.format("%.2f", score) : "Not Graded";
            String percentage = score != null ? String.format("%.2f%%", (score / assignment.getMaxPoints()) * 100) : "N/A";
            String status = score != null && score > 0 ? "Completed" : "Pending";
            
            if (score != null && score > 0) {
                completed++;
            }
            
            assignmentTableModel.addRow(new Object[]{
                assignment.getTitle(),
                assignment.getDueDate() != null ? assignment.getDueDate().toString() : "No due date",
                assignment.getMaxPoints(),
                scoreStr,
                percentage,
                status
            });
        }
        
        // Update progress
        completedLabel.setText(completed + " / " + assignments.size());
        
        // Get current grade
        Enrollment enrollment = findEnrollment(offering);
        if (enrollment != null && enrollment.getGrade() != null) {
            gradeLabel.setText(enrollment.getGrade());
        } else {
            gradeLabel.setText("In Progress");
        }
        
        // Calculate percentage
        double totalPercentage = GradeCalculator.calculateCoursePercentage(currentStudent, offering);
        percentageLabel.setText(String.format("%.2f%%", totalPercentage));
    }
    
    // ========== PROFILE PANEL ==========
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel lblTitle = new JLabel("My Profile", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        
        // Profile fields
        JLabel lblId = new JLabel("University ID:");
        JLabel lblIdValue = new JLabel(currentStudent.getUniversityId());
        gbc.gridx = 0;
        panel.add(lblId, gbc);
        gbc.gridx = 1;
        panel.add(lblIdValue, gbc);
        
        gbc.gridy = 2;
        JLabel lblName = new JLabel("Name:");
        JLabel lblNameValue = new JLabel(currentStudent.getFullName());
        gbc.gridx = 0;
        panel.add(lblName, gbc);
        gbc.gridx = 1;
        panel.add(lblNameValue, gbc);
        
        gbc.gridy = 3;
        JLabel lblEmail = new JLabel("Email:");
        JTextField txtEmail = new JTextField(currentStudent.getEmail(), 20);
        gbc.gridx = 0;
        panel.add(lblEmail, gbc);
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);
        
        gbc.gridy = 4;
        JLabel lblPhone = new JLabel("Phone:");
        JTextField txtPhone = new JTextField(currentStudent.getPhoneNumber(), 20);
        gbc.gridx = 0;
        panel.add(lblPhone, gbc);
        gbc.gridx = 1;
        panel.add(txtPhone, gbc);
        
        gbc.gridy = 5;
        JLabel lblAddress = new JLabel("Address:");
        JTextField txtAddress = new JTextField(currentStudent.getAddress(), 20);
        gbc.gridx = 0;
        panel.add(lblAddress, gbc);
        gbc.gridx = 1;
        panel.add(txtAddress, gbc);
        
        gbc.gridy = 6;
        JLabel lblProgram = new JLabel("Program:");
        JLabel lblProgramValue = new JLabel(currentStudent.getProgram());
        gbc.gridx = 0;
        panel.add(lblProgram, gbc);
        gbc.gridx = 1;
        panel.add(lblProgramValue, gbc);
        
        gbc.gridy = 7;
        JButton btnSave = new JButton("Save Changes");
        btnSave.setBackground(new Color(33, 150, 243));
        btnSave.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(btnSave, gbc);
        
        // Event Handler
        btnSave.addActionListener(e -> {
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String address = txtAddress.getText().trim();
            
            // Validation
            if (!ValidationUtility.isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
                return;
            }
            
            currentStudent.setEmail(email);
            currentStudent.setPhoneNumber(phone);
            currentStudent.setAddress(address);
            
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        });
        
        return panel;
    }
    
    // ========== HELPER METHODS ==========
    
    private Semester getCurrentSemester() {
        ArrayList<Semester> semesters = directory.getSemesters();
        if (!semesters.isEmpty()) {
            return semesters.get(0); // Return first semester (Fall 2025)
        }
        return null;
    }
    
    private CourseOffering findCourseOfferingById(String courseId) {
        Semester currentSemester = getCurrentSemester();
        for (CourseOffering co : directory.getCourseOfferingsBySemester(currentSemester)) {
            if (co.getCourse().getCourseId().equals(courseId)) {
                return co;
            }
        }
        return null;
    }
    
    private boolean isEnrolled(CourseOffering offering) {
        for (Enrollment e : currentStudent.getEnrollments()) {
            if (e.getCourseOffering().equals(offering) && e.isActive()) {
                return true;
            }
        }
        return false;
    }
    
    private Enrollment findEnrollment(CourseOffering offering) {
        for (Enrollment e : currentStudent.getEnrollments()) {
            if (e.getCourseOffering().equals(offering)) {
                return e;
            }
        }
        return null;
    }
    
    private Semester findSemesterByName(String name) {
        for (Semester s : directory.getSemesters()) {
            if (s.getFullName().equals(name)) {
                return s;
            }
        }
        return null;
    }
}