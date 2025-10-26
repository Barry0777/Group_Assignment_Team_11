package UI.Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import accesscontrol.*;
import model.*;
import business.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * StudentDashboard - Complete student interface with all required functionality
 * Author: Student Use Case Implementation
 */
public class StudentDashboard extends javax.swing.JPanel {
    
    // Services
    private UniversityDirectory directory;
    private StudentService studentService;
    private SearchService searchService;
    private GradeCalculator gradeCalculator;
    private AuthenticationService authService;
    
    // Current student
    private Student currentStudent;
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JPanel courseRegistrationPanel;
    private JPanel graduationAuditPanel;
    private JPanel transcriptPanel;
    private JPanel financialPanel;
    private JPanel courseworkPanel;
    private JPanel profilePanel;
    
    // Course Registration Components
    private JTable courseOfferingsTable;
    private DefaultTableModel courseTableModel;
    private JComboBox<String> searchMethodComboBox;
    private JTextField searchTextField;
    private JButton searchButton;
    private JButton enrollButton;
    private JButton dropButton;
    private JButton refreshButton;
    private JComboBox<Semester> semesterComboBox;
    
    // Graduation Audit Components
    private JLabel totalCreditsLabel;
    private JLabel requiredCreditsLabel;
    private JLabel creditsRemainingLabel;
    private JLabel coreCourseStatusLabel;
    private JLabel overallGPALabel;
    private JLabel graduationStatusLabel;
    private JProgressBar creditProgressBar;
    
    // Transcript Components
    private JTable transcriptTable;
    private DefaultTableModel transcriptTableModel;
    private JComboBox<String> transcriptSemesterComboBox;
    private JLabel termGPALabel;
    private JLabel overallGPATranscriptLabel;
    private JLabel academicStandingLabel;
    
    // Financial Components
    private JLabel currentBalanceLabel;
    private JButton payTuitionButton;
    private JTable paymentHistoryTable;
    private DefaultTableModel paymentTableModel;
    private JTextField paymentAmountField;
    
    // Coursework Components
    private JComboBox<CourseOffering> courseworkCourseComboBox;
    private JTable assignmentsTable;
    private DefaultTableModel assignmentsTableModel;
    private JButton submitAssignmentButton;
    
    // Profile Components
    private JTextField profileFirstNameField;
    private JTextField profileLastNameField;
    private JTextField profileEmailField;
    private JTextField profilePhoneField;
    private JTextField profileAddressField;
    private JButton saveProfileButton;
    
    /**
     * Constructor
     */
    public StudentDashboard(UniversityDirectory directory) {
        this.directory = directory;
        this.studentService = new StudentService();
        this.searchService = new SearchService();
        this.gradeCalculator = new GradeCalculator();
        this.authService = AuthenticationService.getInstance();
        
        // Get current logged-in student
        User currentUser = authService.getCurrentUser();
        if (currentUser != null && currentUser.getPerson() instanceof Student) {
            this.currentStudent = (Student) currentUser.getPerson();
        }
        
        initializeUI();
        loadInitialData();
    }
    
    /**
     * Initialize the UI
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create all panels
        courseRegistrationPanel = createCourseRegistrationPanel();
        graduationAuditPanel = createGraduationAuditPanel();
        transcriptPanel = createTranscriptPanel();
        financialPanel = createFinancialPanel();
        courseworkPanel = createCourseworkPanel();
        profilePanel = createProfilePanel();
        
        // Add tabs
        tabbedPane.addTab("Course Registration", courseRegistrationPanel);
        tabbedPane.addTab("Graduation Audit", graduationAuditPanel);
        tabbedPane.addTab("Transcript Review", transcriptPanel);
        tabbedPane.addTab("Financial Management", financialPanel);
        tabbedPane.addTab("Coursework", courseworkPanel);
        tabbedPane.addTab("Profile", profilePanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    // ========== COURSE REGISTRATION PANEL ==========
    
    private JPanel createCourseRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Course Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Method:"));
        
        searchMethodComboBox = new JComboBox<>(new String[]{
            "Course ID", "Instructor Name", "Course Title"
        });
        searchPanel.add(searchMethodComboBox);
        
        searchPanel.add(new JLabel("Search:"));
        searchTextField = new JTextField(20);
        searchPanel.add(searchTextField);
        
        searchButton = new JButton("Search");
        searchPanel.add(searchButton);
        
        refreshButton = new JButton("Show All");
        searchPanel.add(refreshButton);
        
        searchPanel.add(new JLabel("Semester:"));
        semesterComboBox = new JComboBox<>();
        searchPanel.add(semesterComboBox);
        
        // Table
        String[] columns = {"Course ID", "Title", "Instructor", "Credits", "Schedule", 
                           "Room", "Enrolled/Capacity", "Status"};
        courseTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseOfferingsTable = new JTable(courseTableModel);
        courseOfferingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(courseOfferingsTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        enrollButton = new JButton("Enroll in Selected Course");
        dropButton = new JButton("Drop Selected Course");
        
        enrollButton.setBackground(new Color(46, 125, 50));
        enrollButton.setForeground(Color.WHITE);
        dropButton.setBackground(new Color(211, 47, 47));
        dropButton.setForeground(Color.WHITE);
        
        buttonPanel.add(enrollButton);
        buttonPanel.add(dropButton);
        
        // Add action listeners
        searchButton.addActionListener(e -> performSearch());
        refreshButton.addActionListener(e -> loadCourseOfferings());
        enrollButton.addActionListener(e -> enrollInCourse());
        dropButton.addActionListener(e -> dropCourse());
        semesterComboBox.addActionListener(e -> loadCourseOfferings());
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void performSearch() {
        if (semesterComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a semester first.");
            return;
        }
        
        Semester selectedSemester = (Semester) semesterComboBox.getSelectedItem();
        String searchText = searchTextField.getText().trim();
        String searchMethod = (String) searchMethodComboBox.getSelectedItem();
        
        ArrayList<CourseOffering> results = new ArrayList<>();
        
        if (searchText.isEmpty()) {
            loadCourseOfferings();
            return;
        }
        
        switch (searchMethod) {
            case "Course ID":
                results = studentService.searchByCourseId(searchText, selectedSemester);
                break;
            case "Instructor Name":
                results = studentService.searchByInstructor(searchText, selectedSemester);
                break;
            case "Course Title":
                results = studentService.searchByTitle(searchText, selectedSemester);
                break;
        }
        
        displayCourseOfferings(results);
    }
    
    private void loadCourseOfferings() {
        if (semesterComboBox.getSelectedItem() == null) return;
        
        Semester selectedSemester = (Semester) semesterComboBox.getSelectedItem();
        ArrayList<CourseOffering> offerings = directory.getCourseOfferingsBySemester(selectedSemester);
        displayCourseOfferings(offerings);
    }
    
    private void displayCourseOfferings(ArrayList<CourseOffering> offerings) {
        courseTableModel.setRowCount(0);
        
        for (CourseOffering offering : offerings) {
            String status = offering.isEnrollmentOpen() ? "Open" : "Closed";
            if (!offering.hasAvailableSeats()) {
                status = "Full";
            }
            
            // Check if student is already enrolled
            for (Enrollment e : currentStudent.getEnrollments()) {
                if (e.getCourseOffering().equals(offering) && e.isActive()) {
                    status = "Enrolled";
                    break;
                }
            }
            
            courseTableModel.addRow(new Object[]{
                offering.getCourse().getCourseId(),
                offering.getCourse().getTitle(),
                offering.getInstructor().getFullName(),
                offering.getCourse().getCreditHours(),
                offering.getSchedule(),
                offering.getRoomLocation(),
                offering.getCurrentEnrollment() + "/" + offering.getMaxCapacity(),
                status
            });
        }
    }
    
    private void enrollInCourse() {
        int selectedRow = courseOfferingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to enroll.");
            return;
        }
        
        String courseId = (String) courseTableModel.getValueAt(selectedRow, 0);
        Semester selectedSemester = (Semester) semesterComboBox.getSelectedItem();
        
        // Find the course offering
        CourseOffering selectedOffering = null;
        for (CourseOffering co : directory.getCourseOfferingsBySemester(selectedSemester)) {
            if (co.getCourse().getCourseId().equals(courseId)) {
                selectedOffering = co;
                break;
            }
        }
        
        if (selectedOffering == null) return;
        
        try {
            Enrollment enrollment = studentService.enrollInCourse(currentStudent, selectedOffering);
            JOptionPane.showMessageDialog(this, 
                "Successfully enrolled in " + selectedOffering.getCourse().getTitle() + 
                "\nTuition added: $" + enrollment.getTuitionAmount());
            loadCourseOfferings();
            updateGraduationAudit();
            updateFinancialInfo();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Enrollment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void dropCourse() {
        int selectedRow = courseOfferingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to drop.");
            return;
        }
        
        String courseId = (String) courseTableModel.getValueAt(selectedRow, 0);
        Semester selectedSemester = (Semester) semesterComboBox.getSelectedItem();
        
        // Find the enrollment
        Enrollment enrollmentToDrop = null;
        for (Enrollment e : currentStudent.getEnrollments()) {
            if (e.getCourseOffering().getCourse().getCourseId().equals(courseId) &&
                e.getCourseOffering().getSemester().equals(selectedSemester) &&
                e.isActive()) {
                enrollmentToDrop = e;
                break;
            }
        }
        
        if (enrollmentToDrop == null) {
            JOptionPane.showMessageDialog(this, "You are not enrolled in this course.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to drop " + enrollmentToDrop.getCourseOffering().getCourse().getTitle() + "?",
            "Confirm Drop", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = studentService.dropCourse(currentStudent, enrollmentToDrop);
            if (success) {
                JOptionPane.showMessageDialog(this, "Course dropped successfully.");
                loadCourseOfferings();
                updateGraduationAudit();
                updateFinancialInfo();
            }
        }
    }
    
    // ========== GRADUATION AUDIT PANEL ==========
    
    private JPanel createGraduationAuditPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Graduation Audit - MSIS Program", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(8, 1, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Degree Progress"));
        
        totalCreditsLabel = new JLabel("Total Credits Completed: 0");
        totalCreditsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        requiredCreditsLabel = new JLabel("Required Credits: 32");
        requiredCreditsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        creditsRemainingLabel = new JLabel("Credits Remaining: 32");
        creditsRemainingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        creditProgressBar = new JProgressBar(0, 32);
        creditProgressBar.setStringPainted(true);
        
        coreCourseStatusLabel = new JLabel("Core Course (INFO 5100): Not Completed");
        coreCourseStatusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        overallGPALabel = new JLabel("Overall GPA: 0.00");
        overallGPALabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        graduationStatusLabel = new JLabel("Graduation Status: Not Eligible");
        graduationStatusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton refreshButton = new JButton("Refresh Status");
        refreshButton.addActionListener(e -> updateGraduationAudit());
        
        infoPanel.add(totalCreditsLabel);
        infoPanel.add(requiredCreditsLabel);
        infoPanel.add(creditsRemainingLabel);
        infoPanel.add(creditProgressBar);
        infoPanel.add(coreCourseStatusLabel);
        infoPanel.add(overallGPALabel);
        infoPanel.add(graduationStatusLabel);
        infoPanel.add(refreshButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateGraduationAudit() {
        // Update credits completed
        studentService.updateCreditsCompleted(currentStudent);
        
        HashMap<String, Object> status = studentService.getGraduationStatus(currentStudent);
        
        int totalCredits = (int) status.get("totalCredits");
        int requiredCredits = (int) status.get("requiredCredits");
        int creditsRemaining = (int) status.get("creditsRemaining");
        boolean hasCoreCourse = (boolean) status.get("hasCoreCourse");
        boolean isEligible = (boolean) status.get("isEligible");
        double gpa = (double) status.get("overallGPA");
        
        totalCreditsLabel.setText("Total Credits Completed: " + totalCredits);
        creditsRemainingLabel.setText("Credits Remaining: " + creditsRemaining);
        creditProgressBar.setValue(totalCredits);
        creditProgressBar.setString(totalCredits + " / " + requiredCredits);
        
        if (hasCoreCourse) {
            coreCourseStatusLabel.setText("Core Course (INFO 5100): ✓ Completed");
            coreCourseStatusLabel.setForeground(new Color(46, 125, 50));
        } else {
            coreCourseStatusLabel.setText("Core Course (INFO 5100): ✗ Not Completed");
            coreCourseStatusLabel.setForeground(new Color(211, 47, 47));
        }
        
        overallGPALabel.setText(String.format("Overall GPA: %.2f", gpa));
        
        if (isEligible) {
            graduationStatusLabel.setText("🎓 Graduation Status: READY TO GRADUATE!");
            graduationStatusLabel.setForeground(new Color(46, 125, 50));
        } else {
            graduationStatusLabel.setText("Graduation Status: Not Eligible Yet");
            graduationStatusLabel.setForeground(new Color(211, 47, 47));
        }
    }
    
    // ========== TRANSCRIPT PANEL ==========
    
    private JPanel createTranscriptPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title and filter
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Academic Transcript", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Semester:"));
        transcriptSemesterComboBox = new JComboBox<>();
        transcriptSemesterComboBox.addItem("All Semesters");
        transcriptSemesterComboBox.addActionListener(e -> loadTranscript());
        filterPanel.add(transcriptSemesterComboBox);
        
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        // Table
        String[] columns = {"Term", "Academic Standing", "Course ID", "Course Name", 
                           "Grade", "Credits", "Grade Points", "Quality Points"};
        transcriptTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transcriptTable = new JTable(transcriptTableModel);
        JScrollPane tableScrollPane = new JScrollPane(transcriptTable);
        
        // GPA Info Panel
        JPanel gpaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        gpaPanel.setBorder(BorderFactory.createTitledBorder("GPA Information"));
        
        termGPALabel = new JLabel("Term GPA: N/A");
        termGPALabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        overallGPATranscriptLabel = new JLabel("Overall GPA: 0.00");
        overallGPATranscriptLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        academicStandingLabel = new JLabel("Academic Standing: Good Standing");
        academicStandingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        gpaPanel.add(termGPALabel);
        gpaPanel.add(overallGPATranscriptLabel);
        gpaPanel.add(academicStandingLabel);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(gpaPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadTranscript() {
        // Check if tuition is paid
        if (!studentService.canViewTranscript(currentStudent)) {
            transcriptTableModel.setRowCount(0);
            JOptionPane.showMessageDialog(this, 
                "You must pay your tuition balance before viewing your transcript.\n" +
                "Current balance: $" + String.format("%.2f", currentStudent.getAccountBalance()),
                "Transcript Locked", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        transcriptTableModel.setRowCount(0);
        
        String selectedItem = (String) transcriptSemesterComboBox.getSelectedItem();
        ArrayList<Enrollment> enrollments;
        
        if ("All Semesters".equals(selectedItem)) {
            enrollments = studentService.getCompleteTranscript(currentStudent);
        } else {
            // Find semester
            Semester selectedSemester = null;
            for (Semester sem : directory.getSemesters()) {
                if (sem.getFullName().equals(selectedItem)) {
                    selectedSemester = sem;
                    break;
                }
            }
            if (selectedSemester != null) {
                enrollments = studentService.getTranscriptBySemester(currentStudent, selectedSemester);
            } else {
                enrollments = new ArrayList<>();
            }
        }
        
        // Display enrollments
        Semester currentSemester = null;
        double termGPA = 0.0;
        
        for (Enrollment e : enrollments) {
            if (e.getGrade() == null) continue; // Skip courses without grades
            
            Semester sem = e.getCourseOffering().getSemester();
            
            // Calculate term GPA for this semester
            if (currentSemester == null || !currentSemester.equals(sem)) {
                currentSemester = sem;
                termGPA = GradeCalculator.calculateTermGPA(currentStudent, sem);
            }
            
            // Determine academic standing
            double overallGPA = currentStudent.getOverallGPA();
            String standing = GradeCalculator.determineAcademicStanding(termGPA, overallGPA);
            
            transcriptTableModel.addRow(new Object[]{
                sem.getFullName(),
                standing,
                e.getCourseOffering().getCourse().getCourseId(),
                e.getCourseOffering().getCourse().getTitle(),
                e.getGrade(),
                e.getCourseOffering().getCourse().getCreditHours(),
                String.format("%.2f", e.getGradePoints()),
                String.format("%.2f", e.getQualityPoints())
            });
        }
        
        // Update GPA labels
        if (!selectedItem.equals("All Semesters") && currentSemester != null) {
            termGPALabel.setText(String.format("Term GPA: %.2f", termGPA));
        } else {
            termGPALabel.setText("Term GPA: Select a semester");
        }
        
        overallGPATranscriptLabel.setText(String.format("Overall GPA: %.2f", currentStudent.getOverallGPA()));
        academicStandingLabel.setText("Academic Standing: " + currentStudent.getAcademicStanding());
        
        // Color code academic standing
        if (currentStudent.getAcademicStanding().equals("Good Standing")) {
            academicStandingLabel.setForeground(new Color(46, 125, 50));
        } else if (currentStudent.getAcademicStanding().equals("Academic Warning")) {
            academicStandingLabel.setForeground(new Color(255, 152, 0));
        } else {
            academicStandingLabel.setForeground(new Color(211, 47, 47));
        }
    }
    
    // ========== FINANCIAL PANEL ==========
    
    private JPanel createFinancialPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Financial Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Balance panel
        JPanel balancePanel = new JPanel(new GridLayout(3, 1, 5, 5));
        balancePanel.setBorder(BorderFactory.createTitledBorder("Current Balance"));
        
        currentBalanceLabel = new JLabel("Current Balance: $0.00");
        currentBalanceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paymentPanel.add(new JLabel("Payment Amount: $"));
        paymentAmountField = new JTextField(10);
        paymentPanel.add(paymentAmountField);
        
        payTuitionButton = new JButton("Pay Tuition");
        payTuitionButton.setBackground(new Color(46, 125, 50));
        payTuitionButton.setForeground(Color.WHITE);
        paymentPanel.add(payTuitionButton);
        
        balancePanel.add(currentBalanceLabel);
        balancePanel.add(paymentPanel);
        
        // Payment history table
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Payment History"));
        
        String[] columns = {"Payment ID", "Date", "Amount", "Semester", "Description"};
        paymentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paymentHistoryTable = new JTable(paymentTableModel);
        JScrollPane historyScrollPane = new JScrollPane(paymentHistoryTable);
        
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);
        
        // Action listener
        payTuitionButton.addActionListener(e -> processTuitionPayment());
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(balancePanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(historyPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateFinancialInfo() {
        double balance = currentStudent.getAccountBalance();
        currentBalanceLabel.setText(String.format("Current Balance: $%.2f", balance));
        
        if (balance > 0) {
            currentBalanceLabel.setForeground(new Color(211, 47, 47));
        } else {
            currentBalanceLabel.setForeground(new Color(46, 125, 50));
        }
        
        // Load payment history
        paymentTableModel.setRowCount(0);
        ArrayList<TuitionPayment> payments = studentService.getPaymentHistory(currentStudent);
        
        for (TuitionPayment payment : payments) {
            paymentTableModel.addRow(new Object[]{
                payment.getPaymentId(),
                payment.getPaymentDate().toString(),
                String.format("$%.2f", payment.getAmount()),
                payment.getSemester(),
                payment.getDescription()
            });
        }
    }
    
    private void processTuitionPayment() {
        // Check if there's a balance
        if (currentStudent.getAccountBalance() <= 0) {
            JOptionPane.showMessageDialog(this, 
                "No balance to pay. Your account is current.",
                "No Balance", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String amountText = paymentAmountField.getText().trim();
        
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a payment amount.",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountText);
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Payment amount must be greater than 0.",
                    "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (amount > currentStudent.getAccountBalance()) {
                amount = currentStudent.getAccountBalance();
                JOptionPane.showMessageDialog(this, 
                    "Payment amount exceeds balance. Paying full balance: $" + String.format("%.2f", amount));
            }
            
            TuitionPayment payment = studentService.payTuition(currentStudent, amount);
            
            JOptionPane.showMessageDialog(this, 
                "Payment successful!\n" +
                "Amount paid: $" + String.format("%.2f", amount) + "\n" +
                "Remaining balance: $" + String.format("%.2f", currentStudent.getAccountBalance()),
                "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
            
            paymentAmountField.setText("");
            updateFinancialInfo();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid amount format. Please enter a valid number.",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, 
                ex.getMessage(),
                "Payment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ========== COURSEWORK PANEL ==========
    
    private JPanel createCourseworkPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Coursework Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Course selection
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel("Select Course:"));
        courseworkCourseComboBox = new JComboBox<>();
        courseworkCourseComboBox.addActionListener(e -> loadAssignments());
        selectionPanel.add(courseworkCourseComboBox);
        
        // Assignments table
        String[] columns = {"Assignment", "Description", "Max Points", "Your Score", "Status", "Due Date"};
        assignmentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignmentsTable = new JTable(assignmentsTableModel);
        JScrollPane tableScrollPane = new JScrollPane(assignmentsTable);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitAssignmentButton = new JButton("Mark as Submitted");
        submitAssignmentButton.addActionListener(e -> submitAssignment());
        buttonPanel.add(submitAssignmentButton);
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(selectionPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadAssignments() {
        assignmentsTableModel.setRowCount(0);
        
        CourseOffering selected = (CourseOffering) courseworkCourseComboBox.getSelectedItem();
        if (selected == null) return;
        
        ArrayList<Assignment> assignments = studentService.getCourseAssignments(currentStudent, selected);
        
        for (Assignment assignment : assignments) {
            Double score = studentService.getAssignmentScore(currentStudent, assignment);
            String status = score != null ? "Submitted" : "Not Submitted";
            String scoreStr = score != null ? String.format("%.2f", score) : "N/A";
            
            assignmentsTableModel.addRow(new Object[]{
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getMaxPoints(),
                scoreStr,
                status,
                assignment.getDueDate() != null ? assignment.getDueDate().toString() : "N/A"
            });
        }
    }
    
    private void submitAssignment() {
        int selectedRow = assignmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an assignment to submit.");
            return;
        }
        
        CourseOffering selected = (CourseOffering) courseworkCourseComboBox.getSelectedItem();
        if (selected == null) return;
        
        String assignmentTitle = (String) assignmentsTableModel.getValueAt(selectedRow, 0);
        
        // Find the assignment
        Assignment assignment = null;
        for (Assignment a : selected.getAssignments()) {
            if (a.getTitle().equals(assignmentTitle)) {
                assignment = a;
                break;
            }
        }
        
        if (assignment != null) {
            boolean success = studentService.submitAssignment(currentStudent, assignment);
            if (success) {
                JOptionPane.showMessageDialog(this, "Assignment submitted successfully!");
                loadAssignments();
            } else {
                JOptionPane.showMessageDialog(this, "Assignment already submitted.");
            }
        }
    }
    
    // ========== PROFILE PANEL ==========
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Profile Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        
        formPanel.add(new JLabel("First Name:"));
        profileFirstNameField = new JTextField();
        formPanel.add(profileFirstNameField);
        
        formPanel.add(new JLabel("Last Name:"));
        profileLastNameField = new JTextField();
        formPanel.add(profileLastNameField);
        
        formPanel.add(new JLabel("Email:"));
        profileEmailField = new JTextField();
        formPanel.add(profileEmailField);
        
        formPanel.add(new JLabel("Phone:"));
        profilePhoneField = new JTextField();
        formPanel.add(profilePhoneField);
        
        formPanel.add(new JLabel("Address:"));
        profileAddressField = new JTextField();
        formPanel.add(profileAddressField);
        
        formPanel.add(new JLabel("")); // Spacer
        saveProfileButton = new JButton("Save Changes");
        saveProfileButton.setBackground(new Color(33, 150, 243));
        saveProfileButton.setForeground(Color.WHITE);
        saveProfileButton.addActionListener(e -> saveProfile());
        formPanel.add(saveProfileButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadProfileData() {
        if (currentStudent != null) {
            profileFirstNameField.setText(currentStudent.getFirstName());
            profileLastNameField.setText(currentStudent.getLastName());
            profileEmailField.setText(currentStudent.getEmail());
            profilePhoneField.setText(currentStudent.getPhoneNumber() != null ? currentStudent.getPhoneNumber() : "");
            profileAddressField.setText(currentStudent.getAddress() != null ? currentStudent.getAddress() : "");
        }
    }
    
    private void saveProfile() {
        if (currentStudent == null) return;
        
        String firstName = profileFirstNameField.getText().trim();
        String lastName = profileLastNameField.getText().trim();
        String email = profileEmailField.getText().trim();
        String phone = profilePhoneField.getText().trim();
        String address = profileAddressField.getText().trim();
        
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "First name, last name, and email are required.",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        currentStudent.setFirstName(firstName);
        currentStudent.setLastName(lastName);
        currentStudent.setEmail(email);
        currentStudent.setPhoneNumber(phone);
        currentStudent.setAddress(address);
        
        JOptionPane.showMessageDialog(this, 
            "Profile updated successfully!",
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ========== INITIAL DATA LOADING ==========
    
    private void loadInitialData() {
        // Load semesters
        for (Semester semester : directory.getSemesters()) {
            semesterComboBox.addItem(semester);
            transcriptSemesterComboBox.addItem(semester.getFullName());
        }
        
        // Load enrolled courses for coursework
        for (Enrollment e : currentStudent.getEnrollments()) {
            if (e.isActive()) {
                courseworkCourseComboBox.addItem(e.getCourseOffering());
            }
        }
        
        // Load initial data for all panels
        if (semesterComboBox.getItemCount() > 0) {
            semesterComboBox.setSelectedIndex(0);
            loadCourseOfferings();
        }
        
        updateGraduationAudit();
        loadTranscript();
        updateFinancialInfo();
        loadProfileData();
        
        if (courseworkCourseComboBox.getItemCount() > 0) {
            loadAssignments();
        }
    }
}