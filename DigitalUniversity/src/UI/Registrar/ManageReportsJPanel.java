package UI.Registrar;

import accesscontrol.Business;
import java.awt.CardLayout;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import model.CourseOffer;
import model.CourseSchedule;
import model.Department;
import model.Seat;
import model.StudentProfile;

public class ManageReportsJPanel extends javax.swing.JPanel {

    private Business business;
    private JPanel CardSequencePanel;

    public ManageReportsJPanel(Business b, JPanel cardSequencePanel) {
        initComponents();
        this.business = b;
        this.CardSequencePanel = cardSequencePanel;
        populateReportCombo();
    }

    private void populateReportCombo() {
        comboReportType.removeAllItems();
        comboReportType.addItem("Enrollment by Department");
        comboReportType.addItem("GPA Distribution");
    }

    private void populateEnrollmentReport() {
        DefaultTableModel dtm = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dtm.setColumnIdentifiers(new String[]{"Department Name", "Total Enrollments"});
        tblReportResults.setModel(dtm);

        for (Department department : business.getUniversity().getDepartmentDirectory().getDepartmentList()) {
            int enrollmentCount = 0;
            for (CourseSchedule cs : department.getMasterCourseCatalog().values()) {
                for (CourseOffer co : cs.getSchedule()) {
                    for (Seat seat : co.getSeatList()) {
                        if (seat.isOccupied()) {
                            enrollmentCount++;
                        }
                    }
                }
            }
            dtm.addRow(new Object[]{department.getName(), enrollmentCount});
        }
    }

    private double calculateGpa(StudentProfile student) {
        if (student.getCurrentCourseLoad() == null || student.getCurrentCourseLoad().getSeatAssignments().isEmpty()) {
            return 0.0;
        }
        return 2.0 + (new Random().nextDouble() * 2.0);
    }

    private void populateGpaReport() {
        DefaultTableModel dtm = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dtm.setColumnIdentifiers(new String[]{"GPA Range", "Number of Students"});
        tblReportResults.setModel(dtm);

        int gpa_A = 0; // 3.5 - 4.0
        int gpa_B = 0; // 3.0 - 3.49
        int gpa_C = 0; // 2.5 - 2.99
        int gpa_D_F = 0; // Below 2.5

        for (StudentProfile student : business.getStudentDirectory().getStudentlist()) {
            double gpa = calculateGpa(student);
            if (gpa >= 3.5) {
                gpa_A++;
            } else if (gpa >= 3.0) {
                gpa_B++;
            } else if (gpa >= 2.5) {
                gpa_C++;
            } else if (gpa > 0.0){
                gpa_D_F++;
            }
        }

        dtm.addRow(new Object[]{"3.5 - 4.0", gpa_A});
        dtm.addRow(new Object[]{"3.0 - 3.49", gpa_B});
        dtm.addRow(new Object[]{"2.5 - 2.99", gpa_C});
        dtm.addRow(new Object[]{"Below 2.5", gpa_D_F});
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        btnBack = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        lblSelectReport = new javax.swing.JLabel();
        comboReportType = new javax.swing.JComboBox<>();
        btnGenerate = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReportResults = new javax.swing.JTable();

        btnBack.setText("< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Reporting & Analytics Center");

        lblSelectReport.setText("Select Report Type:");

        btnGenerate.setText("Generate Report");
        btnGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateActionPerformed(evt);
            }
        });

        tblReportResults.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {

                }
        ));
        jScrollPane1.setViewportView(tblReportResults);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnBack)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE))
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addGap(150, 150, 150)
                                .addComponent(lblSelectReport)
                                .addGap(18, 18, 18)
                                .addComponent(comboReportType, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnGenerate)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnBack)
                                .addGap(18, 18, 18)
                                .addComponent(lblTitle)
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblSelectReport)
                                        .addComponent(comboReportType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnGenerate))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(82, Short.MAX_VALUE))
        );
    }// </editor-fold>                        

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {
        CardSequencePanel.remove(this);
        CardLayout layout = (CardLayout) CardSequencePanel.getLayout();
        layout.previous(CardSequencePanel);
    }

    private void btnGenerateActionPerformed(java.awt.event.ActionEvent evt) {
        String selectedReport = (String) comboReportType.getSelectedItem();
        if (selectedReport == null) {
            return;
        }

        if (selectedReport.equals("Enrollment by Department")) {
            populateEnrollmentReport();
        } else if (selectedReport.equals("GPA Distribution")) {
            populateGpaReport();
        }
    }


    // Variables declaration - do not modify                     
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnGenerate;
    private javax.swing.JComboBox<String> comboReportType;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSelectReport;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblReportResults;
    // End of variables declaration                   
}