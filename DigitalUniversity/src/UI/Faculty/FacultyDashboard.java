/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package UI.Faculty;


/**
 *
 * @author 123
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
        model.Semester sem = (model.Semester) cmbSem.getSelectedItem();

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
            for (var co : me.getAssignedCourses()) if (sem.equals(co.getSemester())) current.add(co);
            for (var co : current) {
                courseModel.addRow(new Object[]{
                    co.getCourse().getCourseId(),  
                    co.getCourse().getTitle(),   
                    co.getCourse().getDescription(),  
                    co.getRoomLocation(),           
                    co.getMaxCapacity(),             
                    co.isEnrollmentOpen(),
                    co.getSyllabus() == null ? "" : co.getSyllabus() 
                });
            }
        } catch (IllegalArgumentException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage(), "Load Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveCourses() {
        for (int r=0; r<courseModel.getRowCount(); r++) {
            try {
                model.CourseOffering co = current.get(r);
                String title    = String.valueOf(courseModel.getValueAt(r,1)).trim();
                String desc     = String.valueOf(courseModel.getValueAt(r,2)).trim();
                String room = String.valueOf(courseModel.getValueAt(r,3)).trim();
                String capStr   = String.valueOf(courseModel.getValueAt(r,4)).trim();
                String syllabus = String.valueOf(courseModel.getValueAt(r,6)).trim();

                if (!utility.ValidationUtility.isNotEmpty(title) || !utility.ValidationUtility.isNotEmpty(room))
                    throw new IllegalArgumentException("Title and Room cannot be empty.");
                if (!utility.ValidationUtility.isValidInteger(capStr) || Integer.parseInt(capStr) <= 0)
                    throw new IllegalArgumentException("Capacity must be a positive integer.");

                co.getCourse().setTitle(title);
                co.getCourse().setDescription(desc);
                co.setRoomLocation(room);
                co.setMaxCapacity(Integer.parseInt(capStr));
                if (utility.ValidationUtility.isNotEmpty(syllabus)) 
                co.setSyllabus(syllabus);
            }
                
            catch (IllegalArgumentException ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Row "+(r+1)+" ：" + ex.getMessage(),
                        "Save Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        javax.swing.JOptionPane.showMessageDialog(this, "Saved successfully.");
        loadCourses();
    }

    private void toggleEnrollment(boolean open) {
        int row = courseTable.getSelectedRow();
        if (row < 0) { javax.swing.JOptionPane.showMessageDialog(this, "Please select a course first."); return; }
        try {
            var co = current.get(row);
            if (open) fs.openEnrollment(co); else fs.closeEnrollment(co);
            javax.swing.JOptionPane.showMessageDialog(this, (open ? "Enrollment has been opened for this course." : "Enrollment has been closed for this course") );
            loadCourses();
        } catch (IllegalArgumentException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage(), "Action Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chooseSyllabus() {
        int row = courseTable.getSelectedRow();
        if (row < 0) { javax.swing.JOptionPane.showMessageDialog(this, "Please select a course first."); return; }
        var fc = new javax.swing.JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF/DOC", "pdf","doc","docx"));
        if (fc.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            courseModel.setValueAt(fc.getSelectedFile().getAbsolutePath(), row, 6);
        }
    }
    private void initStudentsTab() {
        studentsModel = (javax.swing.table.DefaultTableModel) tblStudents.getModel();
        studentsModel.setRowCount(0);

        cmbStuCourse.removeAllItems();
        if (me != null) {
            for (model.CourseOffering co : me.getAssignedCourses()) {
                cmbStuCourse.addItem(co);
            }
        }

        cmbStuCourse.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                javax.swing.JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof model.CourseOffering co) {
                    setText(co.getCourse().getTitle() + " (" + co.getCourse().getCourseId() + ") - " + co.getSemester());
                }
                return this;
            }
        });

        java.awt.event.ActionListener reload = e -> reloadStudents();
        cmbStuCourse.addActionListener(reload);
        btnStuRefresh.addActionListener(reload);
        btnViewProgress.addActionListener(e -> showProgress());
        btnTranscript.addActionListener(e -> showTranscript());

        if (cmbStuCourse.getItemCount() > 0) {
            cmbStuCourse.setSelectedIndex(0);
            reloadStudents();
        }
    }
    private void initGradingTab()  {
        
        cmbGradeCourse.removeAllItems();
        if (me != null) {
            for (model.CourseOffering co : me.getAssignedCourses()) {
                cmbGradeCourse.addItem(co);
            }
        }
        
        cmbGradeCourse.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof model.CourseOffering co) {
                    setText(co.getCourse().getTitle() + " (" + co.getCourse().getCourseId() + ") - " + co.getSemester());
                }
                return this;
            }
        });

        assignmentsModel = new javax.swing.DefaultListModel<>();
        lstAssignments.setModel(assignmentsModel);
        lstAssignments.setCellRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof model.Assignment a) {
                    setText(a.getTitle() + " (max " + a.getMaxPoints() + ")");
                }
                return this;
            }
        });

        gradesModel = new javax.swing.table.DefaultTableModel(
                new Object[]{"Student ID", "Name", "Score", "Letter"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 2; }
        };
        tblGrades.setModel(gradesModel);
        tblGrades.setAutoCreateRowSorter(true);
        tblGrades.setFillsViewportHeight(true);

        gradesModel.addTableModelListener(e -> {
            if (e.getColumn() == 2 && e.getFirstRow() >= 0) {
                int r = e.getFirstRow();
                var asg = lstAssignments.getSelectedValue();
                var scoreStr = String.valueOf(gradesModel.getValueAt(r, 2)).trim();
                if (asg != null && utility.ValidationUtility.isInteger(scoreStr)) {
                    int score = Integer.parseInt(scoreStr);
                    double pct = score * 100.0 / asg.getMaxPoints();
                    gradesModel.setValueAt(business.GradeCalculator.calculateLetterGrade(pct), r, 3);
                } else {
                    gradesModel.setValueAt("", r, 3);
                }
            }
        });

        java.awt.event.ActionListener reloadAssignments = e -> loadAssignments();
        cmbGradeCourse.addActionListener(reloadAssignments);
        btnLoadAssgn.addActionListener(reloadAssignments);

        lstAssignments.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadGradesForSelectedAssignment();
        });

        btnAddAssgn.addActionListener(e -> newAssignment());
        btnSaveGrades.addActionListener(e -> saveGrades());
        btnAutoFinal.addActionListener(e -> autoFinalGrade());
        btnRank.addActionListener(e -> showRanking());
        btnClassGpa.addActionListener(e -> showClassGpa());

        if (cmbGradeCourse.getItemCount() > 0) {
            cmbGradeCourse.setSelectedIndex(0);
            loadAssignments();
        }
    }
    private void initReportsTab()  { }
    private void initProfileTab()  { }
    
    private void reloadStudents() {
        studentsModel.setRowCount(0);
        model.CourseOffering co = (model.CourseOffering) cmbStuCourse.getSelectedItem();
        if (co == null) return;
        try {
            java.util.List<model.Student> list = fs.getEnrolledStudents(co);
            for (model.Student s : list) {
                var prog = fs.getStudentProgress(s, co);
                double pct = (double) prog.get("percentage");              // 0-100
                String letter = business.GradeCalculator.calculateLetterGrade(pct);
                studentsModel.addRow(new Object[]{
                    s.getUniversityId(),
                    s.getFirstName() + " " + s.getLastName(),
                    s.getEmail(),
                    String.format("%.2f", pct),
                    letter
                });
            }
        } catch (IllegalArgumentException ex) { error(ex.getMessage()); }
    }

    private model.Student getSelectedStudent() {
        int row = tblStudents.getSelectedRow();
        if (row < 0) return null;
        String sid = String.valueOf(studentsModel.getValueAt(row, 0));
        model.CourseOffering co = (model.CourseOffering) cmbStuCourse.getSelectedItem();
        if (co == null) return null;
        for (model.Student s : fs.getEnrolledStudents(co)) {
            if (sid.equals(s.getUniversityId())) return s;
        }
        return null;
    }

    private void showProgress() {
        model.CourseOffering co = (model.CourseOffering) cmbStuCourse.getSelectedItem();
        model.Student s = getSelectedStudent();
        if (co == null || s == null) { info("Please select a student row."); return; }
        try {
            var prog = fs.getStudentProgress(s, co);
            double pct = (double) prog.get("percentage");
            String letter = business.GradeCalculator.calculateLetterGrade(pct);
            javax.swing.JTextArea ta = new javax.swing.JTextArea(
                s.getFirstName()+" "+s.getLastName()+"\n"+
                "Progress: "+String.format("%.2f", pct)+"% ("+letter+")", 10, 40);
            ta.setEditable(false);
            javax.swing.JOptionPane.showMessageDialog(this, new javax.swing.JScrollPane(ta),
                "Progress", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) { error(ex.getMessage()); }
    }

    private void showTranscript() {
        model.Student s = getSelectedStudent();
        if (s == null) { info("Please select a student row."); return; }
        try {
            java.util.List<model.Enrollment> list = fs.getStudentTranscript(s);
        if (list == null || list.isEmpty()) { info("No transcript records found."); return; }

        StringBuilder sb = new StringBuilder();
        sb.append(s.getFirstName()).append(" ").append(s.getLastName()).append("\n\n");

        for (var e : list) {
            String line;
            try {
                    var co     = e.getCourseOffering();      
                    var course = co.getCourse();
                    String code  = course.getCourseId();      
                    String title = course.getTitle();          
                    String sem   = String.valueOf(co.getSemester());
                    
                    line = String.format("%s (%s) — %s", title, code, sem);
                } catch (Exception ignore) {
                    
                    line = String.valueOf(e);
                }
                sb.append(line).append("\n");
            }

            javax.swing.JTextArea ta = new javax.swing.JTextArea(sb.toString(), 18, 60);
            ta.setEditable(false);
            javax.swing.JOptionPane.showMessageDialog(
                this, new javax.swing.JScrollPane(ta),
                "Transcript Summary", javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IllegalArgumentException ex) { error(ex.getMessage()); }
    }
    
    private void info(String msg) {
        javax.swing.JOptionPane.showMessageDialog(
            this, msg, "Info", javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }
    private void error(String msg) {
        javax.swing.JOptionPane.showMessageDialog(
            this, msg, "Error", javax.swing.JOptionPane.ERROR_MESSAGE
        );
    }
    
    
    
    
    private model.CourseOffering getSelectedCourse() {
        return (model.CourseOffering) cmbGradeCourse.getSelectedItem();
    }

    private void loadAssignments() {
        assignmentsModel.clear();
        var co = getSelectedCourse();
        if (co == null) return;
        
        for (model.Assignment a : co.getAssignments()) {
            assignmentsModel.addElement(a);
        }
        if (!assignmentsModel.isEmpty()) {
            lstAssignments.setSelectedIndex(0);
        } else {
            gradesModel.setRowCount(0);
        }
    }

    private void loadGradesForSelectedAssignment() {
        gradesModel.setRowCount(0);
        var co  = getSelectedCourse();
        var asg = lstAssignments.getSelectedValue();
        if (co == null || asg == null) return;

        try {
            for (model.Student s : fs.getEnrolledStudents(co)) {
                
                String id   = s.getUniversityId();
                String name = s.getFirstName() + " " + s.getLastName();
                gradesModel.addRow(new Object[]{ id, name, "", "" });
            }
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        }
    }

    private model.Student findStudentById(model.CourseOffering co, String sid) {
        for (model.Student s : fs.getEnrolledStudents(co)) {
            if (sid.equals(s.getUniversityId())) return s;
        }
        return null;
    }

    private void saveGrades() {
        var co  = getSelectedCourse();
        var asg = lstAssignments.getSelectedValue();
        if (co == null || asg == null) { info("Please select a course and an assignment."); return; }

        try {
            for (int r = 0; r < gradesModel.getRowCount(); r++) {
                String sid      = String.valueOf(gradesModel.getValueAt(r, 0)).trim();
                String scoreStr = String.valueOf(gradesModel.getValueAt(r, 2)).trim();

                if (!utility.ValidationUtility.isNotEmpty(scoreStr)) continue; // 你们项目现成的方法
                int score;
                try {
                    score = Integer.parseInt(scoreStr);
                } catch (NumberFormatException ex) {
                    continue; // 非法输入就跳过该行
                }
                if (score < 0 || score > asg.getMaxPoints()) {
                    throw new IllegalArgumentException("Score must be between 0 and " + asg.getMaxPoints() + ".");
                }

                model.Student stu = findStudentById(co, sid);
                if (stu != null) {
                    // 注意参数顺序：Assignment, Student, double/int（按你们 FacultyService 实际签名）
                    fs.gradeAssignment(asg, stu, score);
                }
            }
            info("Grades saved.");
            loadGradesForSelectedAssignment(); // 若你有这个刷新方法
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        }
    }

    private void newAssignment() {
        var co = getSelectedCourse();
        if (co == null) { info("Please select a course."); return; }

        String title = javax.swing.JOptionPane.showInputDialog(this, "Assignment title:");
        if (title == null || !utility.ValidationUtility.isNotEmpty(title)) return;

        String maxStr = javax.swing.JOptionPane.showInputDialog(this, "Max points:");
        if (maxStr == null || !utility.ValidationUtility.isInteger(maxStr)) return;
        int maxPts = Integer.parseInt(maxStr);
        if (maxPts <= 0) { error("Max points must be a positive integer."); return; }

        try {
            
            String desc = javax.swing.JOptionPane.showInputDialog(this, "Description (optional):");
            fs.createAssignment(co, title.trim(), (desc==null?"":desc.trim()), maxPts);
            
            loadAssignments();
            info("Assignment created.");
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        }
    }

    private void autoFinalGrade() {
        var co = getSelectedCourse();
        if (co == null) { info("Please select a course."); return; }

        try {
            for (model.Student s : fs.getEnrolledStudents(co)) {
                fs.assignFinalGrade(s, co);
            }
            info("Final grades assigned.");
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        }
    }

    private void showRanking() {
        var co = getSelectedCourse();
        if (co == null) { info("Please select a course."); return; }

        try {
            java.util.ArrayList<java.util.HashMap<String,Object>> ranked = fs.rankStudentsByGrade(co);

            StringBuilder sb = new StringBuilder("Ranking:\n");
            int i = 1;
            for (java.util.HashMap<String,Object> row : ranked) {
                model.Student s = (model.Student) row.get("student");
                double pct      = ((Number) row.get("percentage")).doubleValue();
                String letter   = String.valueOf(row.get("letter"));
                sb.append(i++).append(". ")
                  .append(s.getFirstName()).append(" ").append(s.getLastName())
                  .append(" — ").append(String.format("%.2f", pct)).append("% (").append(letter).append(")\n");
            }

            javax.swing.JTextArea ta = new javax.swing.JTextArea(sb.toString(), 18, 60);
            ta.setEditable(false);
            javax.swing.JOptionPane.showMessageDialog(this, new javax.swing.JScrollPane(ta),
                    "Ranking", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        }
    }

    private void showClassGpa() {
        var co = getSelectedCourse();
        if (co == null) { info("Please select a course."); return; }
        try {
            double gpa = fs.calculateClassGPA(co);
            info(String.format("Class GPA: %.2f", gpa));
        } catch (IllegalArgumentException ex) { error(ex.getMessage()); }
    }




    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblHeader = new javax.swing.JLabel();
        tabs = new javax.swing.JTabbedPane();
        tabCourses = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblSemester = new javax.swing.JLabel();
        cmbSem = new javax.swing.JComboBox<>();
        btnSave = new javax.swing.JButton();
        btnOpen = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnUpload = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        courseTable = new javax.swing.JTable();
        tabStudents = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblCourse = new javax.swing.JLabel();
        cmbStuCourse = new javax.swing.JComboBox<>();
        btnStuRefresh = new javax.swing.JButton();
        btnViewProgress = new javax.swing.JButton();
        btnTranscript = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStudents = new javax.swing.JTable();
        tabGrading = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbGradeCourse = new javax.swing.JComboBox<>();
        btnLoadAssgn = new javax.swing.JButton();
        btnAddAssgn = new javax.swing.JButton();
        btnSaveGrades = new javax.swing.JButton();
        btnAutoFinal = new javax.swing.JButton();
        btnRank = new javax.swing.JButton();
        btnClassGpa = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstAssignments = new javax.swing.JList<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblGrades = new javax.swing.JTable();
        tabReports = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cmbRepCourse = new javax.swing.JComboBox<>();
        btnRepRefresh = new javax.swing.JButton();
        btnExportRoster = new javax.swing.JButton();
        btnExportGrades = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblReport = new javax.swing.JTable();
        tabProfile = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        lblHeader.setText("Faculty Dashboard");
        add(lblHeader, java.awt.BorderLayout.PAGE_START);

        tabCourses.setLayout(new java.awt.BorderLayout());

        lblSemester.setText("Semester");

        btnSave.setText("Save");

        btnOpen.setText("Open");

        btnClose.setText("Close");

        btnUpload.setText("Upload");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblSemester)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbSem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(105, 105, 105)
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnOpen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnClose)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUpload)
                .addContainerGap(166, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSemester)
                    .addComponent(cmbSem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave)
                    .addComponent(btnOpen)
                    .addComponent(btnClose)
                    .addComponent(btnUpload))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        tabCourses.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        courseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(courseTable);

        tabCourses.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        tabs.addTab("My Courses", tabCourses);

        tabStudents.setLayout(new java.awt.BorderLayout());

        lblCourse.setText("Course");

        cmbStuCourse.setModel(new javax.swing.DefaultComboBoxModel<model.CourseOffering>());

        btnStuRefresh.setText("Refresh");
        btnStuRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStuRefreshActionPerformed(evt);
            }
        });

        btnViewProgress.setText("View Progress");

        btnTranscript.setText("Transcript");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(lblCourse)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbStuCourse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(147, 147, 147)
                .addComponent(btnStuRefresh)
                .addGap(18, 18, 18)
                .addComponent(btnViewProgress)
                .addGap(18, 18, 18)
                .addComponent(btnTranscript)
                .addContainerGap(137, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCourse)
                    .addComponent(cmbStuCourse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnStuRefresh)
                    .addComponent(btnViewProgress)
                    .addComponent(btnTranscript))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        tabStudents.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        tblStudents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblStudents);

        tabStudents.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        tabs.addTab("Students", tabStudents);

        tabGrading.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Course");

        btnLoadAssgn.setText("Load");

        btnAddAssgn.setText("New");

        btnSaveGrades.setText("Save Grades");

        btnAutoFinal.setText("Auto Final Grade");
        btnAutoFinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAutoFinalActionPerformed(evt);
            }
        });

        btnRank.setText("Rank");

        btnClassGpa.setText("Class GPA");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAutoFinal)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(cmbGradeCourse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(118, 118, 118)
                        .addComponent(btnLoadAssgn)))
                .addGap(58, 58, 58)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAddAssgn)
                    .addComponent(btnRank))
                .addGap(58, 58, 58)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnSaveGrades)
                    .addComponent(btnClassGpa))
                .addContainerGap(99, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(cmbGradeCourse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLoadAssgn)
                            .addComponent(btnAddAssgn)
                            .addComponent(btnSaveGrades))))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAutoFinal)
                    .addComponent(btnRank)
                    .addComponent(btnClassGpa))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        tabGrading.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        lstAssignments.setToolTipText("");
        jScrollPane3.setViewportView(lstAssignments);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(185, Short.MAX_VALUE))
        );

        tabGrading.add(jPanel4, java.awt.BorderLayout.LINE_START);

        tblGrades.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(tblGrades);

        tabGrading.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        tabs.addTab("Grading & Ranking", tabGrading);

        tabReports.setLayout(new java.awt.BorderLayout());

        jLabel2.setText("Course");

        cmbRepCourse.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnRepRefresh.setText("Refresh");
        btnRepRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRepRefreshActionPerformed(evt);
            }
        });

        btnExportRoster.setText("Export Roster");
        btnExportRoster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportRosterActionPerformed(evt);
            }
        });

        btnExportGrades.setText("Export Grades");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(cmbRepCourse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(159, 159, 159)
                .addComponent(btnRepRefresh)
                .addGap(33, 33, 33)
                .addComponent(btnExportRoster)
                .addGap(29, 29, 29)
                .addComponent(btnExportGrades)
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cmbRepCourse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRepRefresh)
                    .addComponent(btnExportRoster)
                    .addComponent(btnExportGrades))
                .addContainerGap(59, Short.MAX_VALUE))
        );

        tabReports.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        tblReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(tblReport);

        tabReports.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        tabs.addTab("Reports & Tuition", tabReports);

        javax.swing.GroupLayout tabProfileLayout = new javax.swing.GroupLayout(tabProfile);
        tabProfile.setLayout(tabProfileLayout);
        tabProfileLayout.setHorizontalGroup(
            tabProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 746, Short.MAX_VALUE)
        );
        tabProfileLayout.setVerticalGroup(
            tabProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 478, Short.MAX_VALUE)
        );

        tabs.addTab("My Profile", tabProfile);

        add(tabs, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnStuRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStuRefreshActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnStuRefreshActionPerformed

    private void btnAutoFinalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutoFinalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAutoFinalActionPerformed

    private void btnExportRosterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportRosterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnExportRosterActionPerformed

    private void btnRepRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRepRefreshActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRepRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddAssgn;
    private javax.swing.JButton btnAutoFinal;
    private javax.swing.JButton btnClassGpa;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnExportGrades;
    private javax.swing.JButton btnExportRoster;
    private javax.swing.JButton btnLoadAssgn;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnRank;
    private javax.swing.JButton btnRepRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveGrades;
    private javax.swing.JButton btnStuRefresh;
    private javax.swing.JButton btnTranscript;
    private javax.swing.JButton btnUpload;
    private javax.swing.JButton btnViewProgress;
    private javax.swing.JComboBox<model.CourseOffering> cmbGradeCourse;
    private javax.swing.JComboBox<model.CourseOffering> cmbRepCourse;
    private javax.swing.JComboBox<model.Semester> cmbSem;
    private javax.swing.JComboBox<model.CourseOffering> cmbStuCourse;
    private javax.swing.JTable courseTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblCourse;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblSemester;
    private javax.swing.JList<model.Assignment> lstAssignments;
    private javax.swing.JPanel tabCourses;
    private javax.swing.JPanel tabGrading;
    private javax.swing.JPanel tabProfile;
    private javax.swing.JPanel tabReports;
    private javax.swing.JPanel tabStudents;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblGrades;
    private javax.swing.JTable tblReport;
    private javax.swing.JTable tblStudents;
    // End of variables declaration//GEN-END:variables
}
