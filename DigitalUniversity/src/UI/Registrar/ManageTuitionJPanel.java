package UI.Registrar;

import accesscontrol.Business;
import java.awt.CardLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import model.Course;
import model.CourseLoad;
import model.SeatAssignment;
import model.StudentProfile;

public class ManageTuitionJPanel extends javax.swing.JPanel {

    private Business business;
    private JPanel CardSequencePanel;

    public ManageTuitionJPanel(Business b, JPanel cardSequencePanel) {
        initComponents();
        this.business = b;
        this.CardSequencePanel = cardSequencePanel;
        populateTuitionTable();
    }

    private void populateTuitionTable() {
        DefaultTableModel dtm = (DefaultTableModel) tblTuitionStatus.getModel();
        dtm.setRowCount(0);

        for (StudentProfile student : business.getStudentDirectory().getStudentlist()) {
            int totalCredits = 0;
            int tuitionDue = 0;

            CourseLoad currentLoad = student.getCurrentCourseLoad();

            if (currentLoad != null) {
                for (SeatAssignment sa : currentLoad.getSeatAssignments()) {
                    Course course = sa.getSeat().getCourseOffer().getSubjectCourse();
                    totalCredits += course.getCredits();
                    tuitionDue += course.getCoursePrice();
                }
            }

            Object[] row = new Object[5];
            row[0] = student.getPerson().getPersonId();
            row[1] = student.getPerson().toString();
            row[2] = totalCredits;
            row[3] = tuitionDue;
            row[4] = "Unpaid";

            dtm.addRow(row);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        btnBack = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTuitionStatus = new javax.swing.JTable();
        btnGenerateReport = new javax.swing.JButton();

        btnBack.setText("< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Tuition & Financial Center");

        tblTuitionStatus.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                },
                new String [] {
                        "Student ID", "Student Name", "Total Credits", "Tuition Due ($)", "Payment Status"
                }
        ) {
            boolean[] canEdit = new boolean [] {
                    false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblTuitionStatus);

        btnGenerateReport.setText("Generate Financial Report");
        btnGenerateReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateReportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnBack)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE))
                                .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnGenerateReport, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(250, 250, 250))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnBack)
                                .addGap(18, 18, 18)
                                .addComponent(lblTitle)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnGenerateReport)
                                .addContainerGap(69, Short.MAX_VALUE))
        );
    }// </editor-fold>                        

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {
        CardSequencePanel.remove(this);
        CardLayout layout = (CardLayout) CardSequencePanel.getLayout();
        layout.previous(CardSequencePanel);
    }

    private void btnGenerateReportActionPerformed(java.awt.event.ActionEvent evt) {
        int totalRevenue = 0;

        for (StudentProfile student : business.getStudentDirectory().getStudentlist()) {
            CourseLoad currentLoad = student.getCurrentCourseLoad();
            if (currentLoad != null) {
                for (SeatAssignment sa : currentLoad.getSeatAssignments()) {
                    Course course = sa.getSeat().getCourseOffer().getSubjectCourse();
                    totalRevenue += course.getCoursePrice();
                }
            }
        }

        String reportMessage = "Total Expected University Revenue: $" + totalRevenue;
        JOptionPane.showMessageDialog(this, reportMessage, "Financial Report", JOptionPane.INFORMATION_MESSAGE);
    }


    // Variables declaration - do not modify                     
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnGenerateReport;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblTuitionStatus;
    // End of variables declaration                   
}