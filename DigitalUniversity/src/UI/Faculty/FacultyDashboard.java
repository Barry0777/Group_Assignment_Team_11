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

        // 2) 下拉框填充
        cmbStuCourse.removeAllItems();
        if (me != null) {
            for (model.CourseOffering co : me.getAssignedCourses()) {
                cmbStuCourse.addItem(co);
            }
        }

        // 3) 下拉渲染（可选）
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

        // 4) 事件（只接线，不改布局）
        java.awt.event.ActionListener reload = e -> reloadStudents();
        cmbStuCourse.addActionListener(reload);
        btnStuRefresh.addActionListener(reload);
        btnViewProgress.addActionListener(e -> showProgress());
        btnTranscript.addActionListener(e -> showTranscript());

        // 5) 首次加载
        if (cmbStuCourse.getItemCount() > 0) {
            cmbStuCourse.setSelectedIndex(0);
            reloadStudents();
        }
    }
    private void initGradingTab()  { }
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
        tabReports = new javax.swing.JPanel();
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

        javax.swing.GroupLayout tabGradingLayout = new javax.swing.GroupLayout(tabGrading);
        tabGrading.setLayout(tabGradingLayout);
        tabGradingLayout.setHorizontalGroup(
            tabGradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 746, Short.MAX_VALUE)
        );
        tabGradingLayout.setVerticalGroup(
            tabGradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 478, Short.MAX_VALUE)
        );

        tabs.addTab("Grading & Ranking", tabGrading);

        javax.swing.GroupLayout tabReportsLayout = new javax.swing.GroupLayout(tabReports);
        tabReports.setLayout(tabReportsLayout);
        tabReportsLayout.setHorizontalGroup(
            tabReportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 746, Short.MAX_VALUE)
        );
        tabReportsLayout.setVerticalGroup(
            tabReportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 478, Short.MAX_VALUE)
        );

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnStuRefresh;
    private javax.swing.JButton btnTranscript;
    private javax.swing.JButton btnUpload;
    private javax.swing.JButton btnViewProgress;
    private javax.swing.JComboBox<model.Semester> cmbSem;
    private javax.swing.JComboBox<model.CourseOffering> cmbStuCourse;
    private javax.swing.JTable courseTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCourse;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblSemester;
    private javax.swing.JPanel tabCourses;
    private javax.swing.JPanel tabGrading;
    private javax.swing.JPanel tabProfile;
    private javax.swing.JPanel tabReports;
    private javax.swing.JPanel tabStudents;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblStudents;
    // End of variables declaration//GEN-END:variables
}
