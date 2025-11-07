package UI.Registrar;

import accesscontrol.Business;
import java.awt.CardLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import model.Course;
import model.CourseLoad;
import model.CourseOffer;
import model.CourseSchedule;
import model.Department;
import model.SeatAssignment;
import model.StudentProfile;

public class ManageStudentEnrollmentJPanel extends javax.swing.JPanel {

    private Business business;
    private JPanel CardSequencePanel;

    public ManageStudentEnrollmentJPanel(Business b, JPanel cardSequencePanel) {
        initComponents();
        this.business = b;
        this.CardSequencePanel = cardSequencePanel;
        populateStudentCombo();
        populateCourseOfferCombo();
        populateStudentScheduleTable();
    }

    private void populateStudentCombo() {
        DefaultComboBoxModel<StudentProfile> model = new DefaultComboBoxModel<>();
        for (StudentProfile sp : business.getStudentDirectory().getStudentlist()) {
            model.addElement(sp);
        }
        comboStudents.setModel(model);
    }

    private void populateCourseOfferCombo() {
        DefaultComboBoxModel<CourseOffer> model = new DefaultComboBoxModel<>();
        for (Department dept : business.getUniversity().getDepartmentDirectory().getDepartmentList()) {
            for (CourseSchedule cs : dept.getMasterCourseCatalog().values()) {
                for (CourseOffer co : cs.getSchedule()) {
                    model.addElement(co);
                }
            }
        }
        comboCourseOfferings.setModel(model);
    }

    private void populateStudentScheduleTable() {
        DefaultTableModel dtm = (DefaultTableModel) tblStudentSchedule.getModel();
        dtm.setRowCount(0);

        StudentProfile selectedStudent = (StudentProfile) comboStudents.getSelectedItem();
        if (selectedStudent == null) {
            return;
        }

        CourseLoad cl = selectedStudent.getCurrentCourseLoad();
        if (cl == null) {
            return;
        }

        for (SeatAssignment sa : cl.getSeatAssignments()) {
            CourseOffer offer = sa.getSeat().getCourseOffer();
            Course course = offer.getSubjectCourse();

            Object[] row = new Object[4];
            row[0] = course.getCOurseNumber();
            row[1] = course.getName();
            row[2] = course.getCredits();
            row[3] = offer.getCourseSchedule().getSemester();

            dtm.addRow(row);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        btnBack = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        lblSelectStudent = new javax.swing.JLabel();
        comboStudents = new javax.swing.JComboBox<>();
        lblSelectCourse = new javax.swing.JLabel();
        comboCourseOfferings = new javax.swing.JComboBox<>();
        btnEnroll = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblStudentSchedule = new javax.swing.JTable();
        lblCurrentSchedule = new javax.swing.JLabel();

        btnBack.setText("< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Student Enrollment Center");

        lblSelectStudent.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSelectStudent.setText("Select Student:");

        comboStudents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboStudentsActionPerformed(evt);
            }
        });

        lblSelectCourse.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSelectCourse.setText("Select Course Offering:");

        btnEnroll.setText("Enroll Student");
        btnEnroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnrollActionPerformed(evt);
            }
        });

        tblStudentSchedule.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                },
                new String [] {
                        "Course ID", "Course Name", "Credits", "Semester"
                }
        ) {
            boolean[] canEdit = new boolean [] {
                    false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblStudentSchedule);

        lblCurrentSchedule.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCurrentSchedule.setText("Current Schedule");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(btnBack))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(50, 50, 50)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(lblCurrentSchedule)
                                                        .addComponent(jScrollPane1)
                                                        .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                        .addComponent(lblSelectStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(lblSelectCourse, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(comboStudents, 0, 250, Short.MAX_VALUE)
                                                                        .addComponent(comboCourseOfferings, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(btnEnroll, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnBack)
                                .addGap(18, 18, 18)
                                .addComponent(lblTitle)
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblSelectStudent)
                                        .addComponent(comboStudents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblSelectCourse)
                                        .addComponent(comboCourseOfferings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(btnEnroll)
                                .addGap(25, 25, 25)
                                .addComponent(lblCurrentSchedule)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(45, Short.MAX_VALUE))
        );
    }// </editor-fold>                        

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {
        CardSequencePanel.remove(this);
        CardLayout layout = (CardLayout) CardSequencePanel.getLayout();
        layout.previous(CardSequencePanel);
    }

    private void comboStudentsActionPerformed(java.awt.event.ActionEvent evt) {
        populateStudentScheduleTable();
    }

    private void btnEnrollActionPerformed(java.awt.event.ActionEvent evt) {
        StudentProfile selectedStudent = (StudentProfile) comboStudents.getSelectedItem();
        CourseOffer selectedCourseOffer = (CourseOffer) comboCourseOfferings.getSelectedItem();

        if (selectedStudent == null || selectedCourseOffer == null) {
            JOptionPane.showMessageDialog(this, "Please select both a student and a course offering.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CourseLoad currentLoad = selectedStudent.getCurrentCourseLoad();
        if (currentLoad == null) {
            currentLoad = selectedStudent.newCourseLoad("Fall 2024");
        }

        for (SeatAssignment sa : currentLoad.getSeatAssignments()) {
            if (sa.getSeat().getCourseOffer() == selectedCourseOffer) {
                JOptionPane.showMessageDialog(this, "Student is already enrolled in this course.", "Enrollment Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        SeatAssignment newAssignment = selectedCourseOffer.assignEmptySeat(currentLoad);
        if (newAssignment == null) {
            JOptionPane.showMessageDialog(this, "Course is full. Enrollment failed.", "Enrollment Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Student enrolled successfully!");
        populateStudentScheduleTable();
    }


    // Variables declaration - do not modify                     
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnEnroll;
    private javax.swing.JComboBox<CourseOffer> comboCourseOfferings;
    private javax.swing.JComboBox<StudentProfile> comboStudents;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCurrentSchedule;
    private javax.swing.JLabel lblSelectCourse;
    private javax.swing.JLabel lblSelectStudent;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblStudentSchedule;
    // End of variables declaration                   
}