package UI.Faculty;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;
import model.*;
import business.*;
import utility.ValidationUtility;

/**
 * Complete Faculty Dashboard Implementation
 * @author Faculty Use Case
 */
public class FacultyDashboard extends javax.swing.JPanel {
    
    private model.Faculty me;
    
    private final business.FacultyService fs = new business.FacultyService();
    private final business.UniversityDirectory dir = business.UniversityDirectory.getInstance();
    
    private javax.swing.table.DefaultTableModel courseModel;
    private java.util.List<model.CourseOffering> current = new java.util.ArrayList<>();
    
    private javax.swing.table.DefaultTableModel studentsModel;
    
    private javax.swing.DefaultListModel<model.Assignment> assignmentsModel;
    private javax.swing.table.DefaultTableModel gradesModel;
    
    private javax.swing.table.DefaultTableModel reportModel;
    
    // UI Components
    private JTabbedPane tabs;
    private JPanel tabCourses, tabStudents, tabGrading, tabReports, tabProfile;
    
    // Course Management Components
    private JTable courseTable;
    private JComboBox<Semester> cmbSem;
    private JButton btnSave, btnOpen, btnClose, btnUpload;
    
    // Student Management Components
    private JTable tblStudents;
    private JComboBox<CourseOffering> cmbStuCourse;
    private JButton btnStuRefresh, btnViewProgress, btnTranscript, btnRank;
    private JLabel lblTuitionCollected;
    
    // Grading Components
    private JComboBox<CourseOffering> cmbGradeCourse;
    private JList<Assignment> lstAssignments;
    private JTable tblGrades;
    private JButton btnLoadAssgn, btnAddAssgn, btnSaveGrades, btnAutoFinal, btnClassGpa;
    
    // Reports Components
    private JComboBox<CourseOffering> cmbRepCourse;
    private JComboBox<Semester> cmbRepSemester;
    private JTextArea txtReport;
    private JTable tblReport;
    private JButton btnRepRefresh;
    
    // Profile Components
    private JTextField txtProfFirstName, txtProfLastName, txtProfEmail;
    private JButton btnProfSave;
    
    private JLabel lblHeader;

    public FacultyDashboard() {
        initComponents();
    }
    
    public FacultyDashboard(model.Faculty me) {  
        this();
        this.me = me;
        afterInit();                 
    }
    
    private void afterInit() {
        String name = (me != null) ? me.getFirstName() + " " + me.getLastName() : "";
        lblHeader.setText("Faculty Dashboard — " + name);

        initCoursesTab();
        initStudentsTab();
        initGradingTab();
        initReportsTab();
        initProfileTab();
    }
    
    // ==================== COURSE MANAGEMENT TAB ====================
    
    private void initCoursesTab() {
        courseModel = new javax.swing.table.DefaultTableModel(
            new Object[]{"Course#", "Title", "Description", "Room/Time", "Capacity", "Open?", "Syllabus"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c==1||c==2||c==3||c==4||c==6; }
        };
        courseTable.setModel(courseModel);
        courseTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        courseTable.setAutoCreateRowSorter(true);         
        courseTable.setFillsViewportHeight(true);

        cmbSem.removeAllItems();
        for (model.Semester s : dir.getSemesters()) cmbSem.addItem(s);

        cmbSem.addActionListener(e -> loadCourses());
        btnSave.addActionListener(e -> saveCourses());
        btnOpen.addActionListener(e -> toggleEnrollment(true));
        btnClose.addActionListener(e -> toggleEnrollment(false));
        btnUpload.addActionListener(e -> chooseSyllabus());

        if (cmbSem.getItemCount() > 0) cmbSem.setSelectedIndex(0);
        loadCourses();
    }

    private void loadCourses() {
        courseModel.setRowCount(0);
        var sem = (model.Semester) cmbSem.getSelectedItem();
        if (sem == null || me == null) return;
        try {
            current = new java.util.ArrayList<>();
            for (var co : me.getAssignedCourses()) {
                if (sem.equals(co.getSemester())) current.add(co);
            }
            for (var co : current) {
                courseModel.addRow(new Object[]{
                    co.getCourse().getCourseId(),  
                    co.getCourse().getTitle(),   
                    co.getCourse().getDescription(),  
                    co.getRoomLocation() + " - " + co.getSchedule(),           
                    co.getMaxCapacity(),             
                    co.isEnrollmentOpen() ? "Yes" : "No",
                    co.getSyllabus() == null ? "" : co.getSyllabus() 
                });
            }
        } catch (Exception ex) {
            error("Error loading courses: " + ex.getMessage());
        }
    }

    private void saveCourses() {
        for (int r=0; r<courseModel.getRowCount(); r++) {
            try {
                model.CourseOffering co = current.get(r);
                String title = String.valueOf(courseModel.getValueAt(r,1)).trim();
                String desc = String.valueOf(courseModel.getValueAt(r,2)).trim();
                String room = String.valueOf(courseModel.getValueAt(r,3)).trim();
                String capStr = String.valueOf(courseModel.getValueAt(r,4)).trim();
                String syllabus = String.valueOf(courseModel.getValueAt(r,6)).trim();

                if (!ValidationUtility.isNotEmpty(title) || !ValidationUtility.isNotEmpty(room))
                    throw new IllegalArgumentException("Title and Room cannot be empty.");
                if (!ValidationUtility.isValidInteger(capStr) || Integer.parseInt(capStr) <= 0)
                    throw new IllegalArgumentException("Capacity must be a positive integer.");

                co.getCourse().setTitle(title);
                co.getCourse().setDescription(desc);
                co.setRoomLocation(room);
                co.setMaxCapacity(Integer.parseInt(capStr));
                if (ValidationUtility.isNotEmpty(syllabus)) 
                    co.setSyllabus(syllabus);
            } catch (Exception ex) {
                error("Row "+(r+1)+" error: " + ex.getMessage());
                return;
            }
        }
        info("Courses saved successfully!");
        loadCourses();
    }

    private void toggleEnrollment(boolean open) {
        int row = courseTable.getSelectedRow();
        if (row < 0) { 
            info("Please select a course first."); 
            return; 
        }
        try {
            var co = current.get(row);
            if (open) fs.openEnrollment(co); 
            else fs.closeEnrollment(co);
            info(open ? "Enrollment opened." : "Enrollment closed.");
            loadCourses();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void chooseSyllabus() {
        int row = courseTable.getSelectedRow();
        if (row < 0) { 
            info("Please select a course first."); 
            return; 
        }
        var fc = new javax.swing.JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF/DOC", "pdf","doc","docx"));
        if (fc.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            courseModel.setValueAt(fc.getSelectedFile().getAbsolutePath(), row, 6);
        }
    }
    
    // ==================== STUDENT MANAGEMENT TAB ====================
    
    private void initStudentsTab() {
        String[] cols = {"Rank", "Student ID", "Name", "Email", "Grade %", "Letter Grade"};
        studentsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblStudents.setModel(studentsModel);
        tblStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cmbStuCourse.removeAllItems();
        if (me != null) {
            for (CourseOffering co : me.getAssignedCourses()) {
                cmbStuCourse.addItem(co);
            }
        }

        cmbStuCourse.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CourseOffering) {
                    CourseOffering co = (CourseOffering) value;
                    setText(co.getCourse().getCourseId() + " - " + co.getCourse().getTitle());
                }
                return this;
            }
        });

        btnStuRefresh.addActionListener(e -> loadStudents());
        btnViewProgress.addActionListener(e -> viewStudentProgress());
        btnTranscript.addActionListener(e -> viewStudentTranscript());
        btnRank.addActionListener(e -> rankStudents());

        if (cmbStuCourse.getItemCount() > 0) {
            cmbStuCourse.setSelectedIndex(0);
            loadStudents();
        }
    }

    private void loadStudents() {
        studentsModel.setRowCount(0);
        CourseOffering selected = (CourseOffering) cmbStuCourse.getSelectedItem();
        if (selected == null) return;

        ArrayList<Student> students = fs.getEnrolledStudents(selected);
        
        for (Student s : students) {
            double percentage = GradeCalculator.calculateCoursePercentage(s, selected);
            String letterGrade = GradeCalculator.calculateLetterGrade(percentage);
            
            studentsModel.addRow(new Object[]{
                "", // Rank - will be filled when ranking
                s.getUniversityId(),
                s.getFullName(),
                s.getEmail(),
                String.format("%.2f%%", percentage),
                letterGrade
            });
        }
        
        // Update tuition collected
        double tuition = fs.getTotalTuitionCollected(selected);
        if (lblTuitionCollected != null) {
            lblTuitionCollected.setText("Total Tuition Collected: $" + String.format("%.2f", tuition));
        }
    }

    private void viewStudentProgress() {
        int row = tblStudents.getSelectedRow();
        if (row < 0) {
            info("Please select a student first.");
            return;
        }
        
        CourseOffering selected = (CourseOffering) cmbStuCourse.getSelectedItem();
        String studentId = (String) studentsModel.getValueAt(row, 1);
        Student student = dir.findStudentByUniversityId(studentId);
        
        if (student == null || selected == null) return;
        
        // Create progress dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Student Progress - " + student.getFullName(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(600, 400);
        
        // Header info
        JPanel headerPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        headerPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        headerPanel.add(new JLabel("Student ID:"));
        headerPanel.add(new JLabel(student.getUniversityId()));
        headerPanel.add(new JLabel("Name:"));
        headerPanel.add(new JLabel(student.getFullName()));
        headerPanel.add(new JLabel("Course:"));
        headerPanel.add(new JLabel(selected.getCourse().getCourseId() + " - " + selected.getCourse().getTitle()));
        headerPanel.add(new JLabel("Overall GPA:"));
        headerPanel.add(new JLabel(String.format("%.2f", student.getOverallGPA())));
        
        // Assignment progress table
        String[] cols = {"Assignment", "Max Points", "Your Score", "Percentage"};
        DefaultTableModel progressModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable progressTable = new JTable(progressModel);
        
        ArrayList<Assignment> assignments = selected.getAssignments();
        double totalScore = 0, maxScore = 0;
        
        for (Assignment a : assignments) {
            Double score = a.getStudentScore(student);
            if (score != null) {
                totalScore += score;
            }
            maxScore += a.getMaxPoints();
            
            progressModel.addRow(new Object[]{
                a.getTitle(),
                a.getMaxPoints(),
                score != null ? String.format("%.2f", score) : "Not Graded",
                score != null ? String.format("%.1f%%", (score / a.getMaxPoints()) * 100) : "N/A"
            });
        }
        
        // Summary
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Course Summary"));
        summaryPanel.add(new JLabel("Total Score:"));
        summaryPanel.add(new JLabel(String.format("%.2f / %.2f", totalScore, maxScore)));
        summaryPanel.add(new JLabel("Course Percentage:"));
        double percentage = maxScore > 0 ? (totalScore / maxScore) * 100 : 0;
        summaryPanel.add(new JLabel(String.format("%.2f%% (%s)", percentage, 
            GradeCalculator.calculateLetterGrade(percentage))));
        
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(progressTable), BorderLayout.CENTER);
        dialog.add(summaryPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void viewStudentTranscript() {
        int row = tblStudents.getSelectedRow();
        if (row < 0) {
            info("Please select a student first.");
            return;
        }
        
        String studentId = (String) studentsModel.getValueAt(row, 1);
        Student student = dir.findStudentByUniversityId(studentId);
        
        if (student == null) return;
        
        // Create transcript dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Transcript - " + student.getFullName(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(800, 500);
        
        // Student info
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        infoPanel.add(new JLabel("Student ID:"));
        infoPanel.add(new JLabel(student.getUniversityId()));
        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(new JLabel(student.getFullName()));
        infoPanel.add(new JLabel("Program:"));
        infoPanel.add(new JLabel(student.getProgram()));
        infoPanel.add(new JLabel("Overall GPA:"));
        infoPanel.add(new JLabel(String.format("%.2f", student.getOverallGPA())));
        
        // Transcript table
        String[] cols = {"Semester", "Course ID", "Course Title", "Credits", "Grade", "Grade Points"};
        DefaultTableModel transcriptModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable transcriptTable = new JTable(transcriptModel);
        
        for (Enrollment e : student.getEnrollments()) {
            if (e.getGrade() != null) {
                transcriptModel.addRow(new Object[]{
                    e.getCourseOffering().getSemester().getFullName(),
                    e.getCourseOffering().getCourse().getCourseId(),
                    e.getCourseOffering().getCourse().getTitle(),
                    e.getCourseOffering().getCourse().getCreditHours(),
                    e.getGrade(),
                    String.format("%.2f", e.getGradePoints())
                });
            }
        }
        
        dialog.add(infoPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(transcriptTable), BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void rankStudents() {
        CourseOffering selected = (CourseOffering) cmbStuCourse.getSelectedItem();
        if (selected == null) return;
        
        ArrayList<HashMap<String, Object>> rankings = fs.rankStudentsByGrade(selected);
        
        studentsModel.setRowCount(0);
        int rank = 1;
        for (HashMap<String, Object> entry : rankings) {
            Student s = (Student) entry.get("student");
            double percentage = (double) entry.get("percentage");
            String grade = (String) entry.get("grade");
            if (grade == null) grade = GradeCalculator.calculateLetterGrade(percentage);
            
            studentsModel.addRow(new Object[]{
                rank++,
                s.getUniversityId(),
                s.getFullName(),
                s.getEmail(),
                String.format("%.2f%%", percentage),
                grade
            });
        }
        
        info("Students ranked by grade percentage!");
    }
    
    // ==================== GRADING TAB ====================
    
    private void initGradingTab() {
        assignmentsModel = new DefaultListModel<>();
        lstAssignments.setModel(assignmentsModel);
        
        String[] cols = {"Student ID", "Student Name", "Score", "Max Points", "Percentage"};
        gradesModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 2; } // Only score editable
        };
        tblGrades.setModel(gradesModel);

        cmbGradeCourse.removeAllItems();
        if (me != null) {
            for (CourseOffering co : me.getAssignedCourses()) {
                cmbGradeCourse.addItem(co);
            }
        }

        cmbGradeCourse.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CourseOffering) {
                    CourseOffering co = (CourseOffering) value;
                    setText(co.getCourse().getCourseId() + " - " + co.getCourse().getTitle());
                }
                return this;
            }
        });

        btnLoadAssgn.addActionListener(e -> {
            loadAssignments();
            if (assignmentsModel.getSize() > 0) {
                lstAssignments.setSelectedIndex(0);
                loadGradesForSelectedAssignment();
            }
        });
        
        btnAddAssgn.addActionListener(e -> createNewAssignment());
        btnSaveGrades.addActionListener(e -> saveGrades());
        btnAutoFinal.addActionListener(e -> autoComputeFinalGrades());
        btnClassGpa.addActionListener(e -> showClassGPA());
        
        lstAssignments.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadGradesForSelectedAssignment();
            }
        });

        if (cmbGradeCourse.getItemCount() > 0) {
            cmbGradeCourse.setSelectedIndex(0);
            loadAssignments();
        }
    }

    private void loadAssignments() {
        assignmentsModel.clear();
        CourseOffering selected = (CourseOffering) cmbGradeCourse.getSelectedItem();
        if (selected == null) return;
        
        for (Assignment a : selected.getAssignments()) {
            assignmentsModel.addElement(a);
        }
    }

    private void loadGradesForSelectedAssignment() {
        gradesModel.setRowCount(0);
        Assignment selected = lstAssignments.getSelectedValue();
        if (selected == null) return;
        
        CourseOffering course = selected.getCourseOffering();
        ArrayList<Student> students = fs.getEnrolledStudents(course);
        
        for (Student s : students) {
            Double score = selected.getStudentScore(s);
            double percentage = score != null ? (score / selected.getMaxPoints()) * 100 : 0;
            
            gradesModel.addRow(new Object[]{
                s.getUniversityId(),
                s.getFullName(),
                score != null ? score : 0.0,
                selected.getMaxPoints(),
                String.format("%.1f%%", percentage)
            });
        }
    }

    private void createNewAssignment() {
        CourseOffering selected = (CourseOffering) cmbGradeCourse.getSelectedItem();
        if (selected == null) {
            info("Please select a course first.");
            return;
        }
        
        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Create New Assignment", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(400, 300);
        
        JTextField txtTitle = new JTextField();
        JTextArea txtDesc = new JTextArea(3, 20);
        JTextField txtMaxPoints = new JTextField();
        JTextField txtDueDate = new JTextField();
        txtDueDate.setText("YYYY-MM-DD");
        
        dialog.add(new JLabel("Title:"));
        dialog.add(txtTitle);
        dialog.add(new JLabel("Description:"));
        dialog.add(new JScrollPane(txtDesc));
        dialog.add(new JLabel("Max Points:"));
        dialog.add(txtMaxPoints);
        dialog.add(new JLabel("Due Date:"));
        dialog.add(txtDueDate);
        
        JButton btnCreate = new JButton("Create");
        JButton btnCancel = new JButton("Cancel");
        
        btnCreate.addActionListener(e -> {
            try {
                String title = txtTitle.getText().trim();
                String desc = txtDesc.getText().trim();
                String pointsStr = txtMaxPoints.getText().trim();
                String dateStr = txtDueDate.getText().trim();
                
                if (!ValidationUtility.isNotEmpty(title)) {
                    throw new IllegalArgumentException("Title cannot be empty.");
                }
                if (!ValidationUtility.isValidDouble(pointsStr)) {
                    throw new IllegalArgumentException("Max points must be a valid number.");
                }
                double maxPoints = Double.parseDouble(pointsStr);
                if (maxPoints <= 0) {
                    throw new IllegalArgumentException("Max points must be greater than 0.");
                }
                
                Assignment assignment = fs.createAssignment(selected, title, desc, maxPoints);
                
                // Set due date if provided
                if (!dateStr.equals("YYYY-MM-DD") && ValidationUtility.isNotEmpty(dateStr)) {
                    try {
                        LocalDate dueDate = LocalDate.parse(dateStr);
                        assignment.setDueDate(dueDate);
                    } catch (Exception ex) {
                        // Invalid date format, skip
                    }
                }
                
                info("Assignment created successfully!");
                loadAssignments();
                dialog.dispose();
                
            } catch (Exception ex) {
                error(ex.getMessage());
            }
        });
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        dialog.add(btnCreate);
        dialog.add(btnCancel);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void saveGrades() {
        Assignment selected = lstAssignments.getSelectedValue();
        if (selected == null) {
            info("Please select an assignment first.");
            return;
        }
        
        CourseOffering course = selected.getCourseOffering();
        
        for (int r = 0; r < gradesModel.getRowCount(); r++) {
            try {
                String studentId = (String) gradesModel.getValueAt(r, 0);
                Student student = dir.findStudentByUniversityId(studentId);
                Object scoreObj = gradesModel.getValueAt(r, 2);
                
                double score = 0;
                if (scoreObj instanceof Double) {
                    score = (Double) scoreObj;
                } else if (scoreObj instanceof String) {
                    score = Double.parseDouble((String) scoreObj);
                }
                
                if (student != null) {
                    fs.gradeAssignment(selected, student, score);
                }
                
            } catch (Exception ex) {
                error("Error saving grade for row " + (r+1) + ": " + ex.getMessage());
                return;
            }
        }
        
        info("Grades saved successfully!");
        loadGradesForSelectedAssignment();
    }

    private void autoComputeFinalGrades() {
        CourseOffering selected = (CourseOffering) cmbGradeCourse.getSelectedItem();
        if (selected == null) {
            info("Please select a course first.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "This will calculate and assign final grades for all students in this course.\n" +
            "Final grades will be based on all assignment scores.\n\n" +
            "Continue?",
            "Confirm Final Grade Calculation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        ArrayList<Student> students = fs.getEnrolledStudents(selected);
        int successCount = 0;
        
        for (Student student : students) {
            try {
                boolean success = fs.assignFinalGrade(student, selected);
                if (success) successCount++;
            } catch (Exception ex) {
                error("Error computing grade for " + student.getFullName() + ": " + ex.getMessage());
            }
        }
        
        info("Final grades computed for " + successCount + " students!");
        
        // Refresh student list if on that tab
        if (cmbStuCourse.getSelectedItem() != null) {
            loadStudents();
        }
    }

    private void showClassGPA() {
        CourseOffering selected = (CourseOffering) cmbGradeCourse.getSelectedItem();
        if (selected == null) {
            info("Please select a course first.");
            return;
        }
        
        double classGPA = fs.calculateClassGPA(selected);
        double averagePercentage = GradeCalculator.calculateCourseAverage(selected);
        
        JOptionPane.showMessageDialog(this,
            String.format("Class Statistics for %s:\n\n" +
                "Average Grade: %.2f%%\n" +
                "Average GPA: %.2f\n" +
                "Total Students: %d",
                selected.getCourse().getCourseId(),
                averagePercentage,
                classGPA,
                selected.getCurrentEnrollment()),
            "Class GPA",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ==================== REPORTS TAB ====================
    
    private void initReportsTab() {
        String[] cols = {"Metric", "Value"};
        reportModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblReport.setModel(reportModel);

        cmbRepCourse.removeAllItems();
        cmbRepSemester.removeAllItems();
        
        for (Semester s : dir.getSemesters()) {
            cmbRepSemester.addItem(s);
        }
        
        if (me != null) {
            for (CourseOffering co : me.getAssignedCourses()) {
                cmbRepCourse.addItem(co);
            }
        }

        cmbRepCourse.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CourseOffering) {
                    CourseOffering co = (CourseOffering) value;
                    setText(co.getCourse().getCourseId() + " - " + co.getCourse().getTitle());
                }
                return this;
            }
        });

        btnRepRefresh.addActionListener(e -> generateReport());
        
        cmbRepSemester.addActionListener(e -> filterCoursesBySemester());

        if (cmbRepCourse.getItemCount() > 0) {
            cmbRepCourse.setSelectedIndex(0);
            generateReport();
        }
    }

    private void filterCoursesBySemester() {
        Semester selected = (Semester) cmbRepSemester.getSelectedItem();
        if (selected == null || me == null) return;
        
        cmbRepCourse.removeAllItems();
        for (CourseOffering co : me.getAssignedCourses()) {
            if (co.getSemester().equals(selected)) {
                cmbRepCourse.addItem(co);
            }
        }
        
        if (cmbRepCourse.getItemCount() > 0) {
            generateReport();
        }
    }

    private void generateReport() {
        CourseOffering selected = (CourseOffering) cmbRepCourse.getSelectedItem();
        if (selected == null) return;
        
        HashMap<String, Object> report = fs.generateCourseReport(selected);
        
        // Display in text area
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("       COURSE PERFORMANCE REPORT\n");
        sb.append("═══════════════════════════════════════════════════\n\n");
        sb.append("Course: ").append(report.get("courseId")).append(" - ").append(report.get("courseTitle")).append("\n");
        sb.append("Semester: ").append(report.get("semester")).append("\n");
        sb.append("Instructor: ").append(report.get("instructor")).append("\n\n");
        sb.append("───────────────────────────────────────────────────\n");
        sb.append("ENROLLMENT STATISTICS\n");
        sb.append("───────────────────────────────────────────────────\n");
        sb.append("Enrolled Students: ").append(report.get("enrollmentCount")).append("\n");
        sb.append("Maximum Capacity: ").append(report.get("maxCapacity")).append("\n");
        double utilization = ((int)report.get("enrollmentCount") * 100.0) / (int)report.get("maxCapacity");
        sb.append("Utilization Rate: ").append(String.format("%.1f%%", utilization)).append("\n\n");
        sb.append("───────────────────────────────────────────────────\n");
        sb.append("PERFORMANCE METRICS\n");
        sb.append("───────────────────────────────────────────────────\n");
        sb.append("Average Grade: ").append(String.format("%.2f%%", report.get("averageGrade"))).append("\n");
        sb.append("Class GPA: ").append(String.format("%.2f", report.get("classGPA"))).append("\n\n");
        sb.append("───────────────────────────────────────────────────\n");
        sb.append("GRADE DISTRIBUTION\n");
        sb.append("───────────────────────────────────────────────────\n");
        
        @SuppressWarnings("unchecked")
        HashMap<String, Integer> gradeDistribution = (HashMap<String, Integer>) report.get("gradeDistribution");
        String[] grades = {"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "F"};
        for (String grade : grades) {
            int count = gradeDistribution.getOrDefault(grade, 0);
            sb.append(String.format("%-4s: %2d students", grade, count)).append("\n");
        }
        
        sb.append("\n═══════════════════════════════════════════════════\n");
        
        txtReport.setText(sb.toString());
        
        // Display in table
        reportModel.setRowCount(0);
        reportModel.addRow(new Object[]{"Course ID", report.get("courseId")});
        reportModel.addRow(new Object[]{"Course Title", report.get("courseTitle")});
        reportModel.addRow(new Object[]{"Semester", report.get("semester")});
        reportModel.addRow(new Object[]{"Instructor", report.get("instructor")});
        reportModel.addRow(new Object[]{"Enrolled Students", report.get("enrollmentCount")});
        reportModel.addRow(new Object[]{"Maximum Capacity", report.get("maxCapacity")});
        reportModel.addRow(new Object[]{"Utilization Rate", String.format("%.1f%%", utilization)});
        reportModel.addRow(new Object[]{"Average Grade", String.format("%.2f%%", report.get("averageGrade"))});
        reportModel.addRow(new Object[]{"Class GPA", String.format("%.2f", report.get("classGPA"))});
        reportModel.addRow(new Object[]{"---", "---"});
        reportModel.addRow(new Object[]{"GRADE DISTRIBUTION", ""});
        for (String grade : grades) {
            int count = gradeDistribution.getOrDefault(grade, 0);
            reportModel.addRow(new Object[]{grade, count + " students"});
        }
    }
    
    // ==================== PROFILE TAB ====================
    
    private void initProfileTab() {
        if (me != null) {
            txtProfFirstName.setText(me.getFirstName());
            txtProfLastName.setText(me.getLastName());
            txtProfEmail.setText(me.getEmail());
            
            txtProfFirstName.setEditable(false);
            txtProfLastName.setEditable(false);
        }
        
        btnProfSave.addActionListener(e -> saveProfile());
    }

    private void saveProfile() {
        try {
            String email = txtProfEmail.getText().trim();

            if (!ValidationUtility.isNotEmpty(email)) {
                info("Email cannot be empty.");
                return;
            }
            if (!ValidationUtility.isValidEmail(email)) {
                info("Invalid email format.");
                return;
            }

            me.setEmail(email);
            info("Profile saved successfully!");
            
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // ==================== GENERATED UI CODE ====================
    
    @SuppressWarnings("unchecked")
    private void initComponents() {
        setLayout(new BorderLayout());
        
        lblHeader = new JLabel("Faculty Dashboard", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblHeader, BorderLayout.NORTH);
        
        tabs = new JTabbedPane();
        
        // Create tabs
        tabCourses = createCoursesTabUI();
        tabStudents = createStudentsTabUI();
        tabGrading = createGradingTabUI();
        tabReports = createReportsTabUI();
        tabProfile = createProfileTabUI();
        
        tabs.addTab("Course Management", tabCourses);
        tabs.addTab("Student Management", tabStudents);
        tabs.addTab("Grading & Assignments", tabGrading);
        tabs.addTab("Performance Reports", tabReports);
        tabs.addTab("My Profile", tabProfile);
        
        add(tabs, BorderLayout.CENTER);
    }
    
    private JPanel createCoursesTabUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Semester:"));
        cmbSem = new JComboBox<>();
        topPanel.add(cmbSem);
        
        btnSave = new JButton("Save Changes");
        btnOpen = new JButton("Open Enrollment");
        btnClose = new JButton("Close Enrollment");
        btnUpload = new JButton("Upload Syllabus");
        
        topPanel.add(btnSave);
        topPanel.add(btnOpen);
        topPanel.add(btnClose);
        topPanel.add(btnUpload);
        
        // Table
        courseTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(courseTable);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStudentsTabUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Course:"));
        cmbStuCourse = new JComboBox<>();
        topPanel.add(cmbStuCourse);
        
        btnStuRefresh = new JButton("Refresh");
        btnViewProgress = new JButton("View Progress");
        btnTranscript = new JButton("View Transcript");
        btnRank = new JButton("Rank Students");
        
        topPanel.add(btnStuRefresh);
        topPanel.add(btnViewProgress);
        topPanel.add(btnTranscript);
        topPanel.add(btnRank);
        
        // Tuition label
        lblTuitionCollected = new JLabel("Total Tuition Collected: $0.00");
        lblTuitionCollected.setFont(new Font("Arial", Font.BOLD, 14));
        JPanel tuitionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tuitionPanel.add(lblTuitionCollected);
        
        // Table
        tblStudents = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblStudents);
        
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(topPanel, BorderLayout.NORTH);
        topContainer.add(tuitionPanel, BorderLayout.SOUTH);
        
        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createGradingTabUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Course:"));
        cmbGradeCourse = new JComboBox<>();
        topPanel.add(cmbGradeCourse);
        
        btnLoadAssgn = new JButton("Load Assignments");
        btnAddAssgn = new JButton("Create Assignment");
        btnSaveGrades = new JButton("Save Grades");
        btnAutoFinal = new JButton("Compute Final Grades");
        btnClassGpa = new JButton("Show Class GPA");
        
        topPanel.add(btnLoadAssgn);
        topPanel.add(btnAddAssgn);
        topPanel.add(btnSaveGrades);
        topPanel.add(btnAutoFinal);
        topPanel.add(btnClassGpa);
        
        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Left: Assignments list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Assignments"));
        lstAssignments = new JList<>();
        leftPanel.add(new JScrollPane(lstAssignments), BorderLayout.CENTER);
        
        // Right: Grades table
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Student Grades"));
        tblGrades = new JTable();
        rightPanel.add(new JScrollPane(tblGrades), BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(250);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createReportsTabUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Semester:"));
        cmbRepSemester = new JComboBox<>();
        topPanel.add(cmbRepSemester);
        
        topPanel.add(new JLabel("Course:"));
        cmbRepCourse = new JComboBox<>();
        topPanel.add(cmbRepCourse);
        
        btnRepRefresh = new JButton("Generate Report");
        topPanel.add(btnRepRefresh);
        
        // Split pane for text and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // Top: Text report
        txtReport = new JTextArea();
        txtReport.setEditable(false);
        txtReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane textScroll = new JScrollPane(txtReport);
        textScroll.setBorder(BorderFactory.createTitledBorder("Detailed Report"));
        
        // Bottom: Table summary
        tblReport = new JTable();
        JScrollPane tableScroll = new JScrollPane(tblReport);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Summary"));
        
        splitPane.setTopComponent(textScroll);
        splitPane.setBottomComponent(tableScroll);
        splitPane.setDividerLocation(300);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProfileTabUI() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        
        formPanel.add(new JLabel("First Name:"));
        txtProfFirstName = new JTextField();
        formPanel.add(txtProfFirstName);
        
        formPanel.add(new JLabel("Last Name:"));
        txtProfLastName = new JTextField();
        formPanel.add(txtProfLastName);
        
        formPanel.add(new JLabel("Email:"));
        txtProfEmail = new JTextField();
        formPanel.add(txtProfEmail);
        
        formPanel.add(new JLabel(""));
        btnProfSave = new JButton("Save Changes");
        formPanel.add(btnProfSave);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        return panel;
    }
}