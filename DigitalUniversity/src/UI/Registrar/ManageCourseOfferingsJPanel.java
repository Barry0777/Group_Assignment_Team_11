/*
 * This panel allows the Registrar to view and manage course offerings.
 */
package UI.Registrar;



import accesscontrol.Business;
import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import model.Course;
import model.CourseOffer;
import model.CourseSchedule;
import model.Department;

/**
 *
 * @author Administrator
 */
public class ManageCourseOfferingsJPanel extends javax.swing.JPanel {
    private Business business;
    private JPanel CardSequencePanel;

    public ManageCourseOfferingsJPanel(Business b, JPanel cardSequencePanel) {
        initComponents();
        this.business = b;
        this.CardSequencePanel = cardSequencePanel;
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
               
                populateTable();
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        btnBack = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCourseOfferings = new javax.swing.JTable();
        btnCreate = new javax.swing.JButton();

        btnBack.setText("< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        tblCourseOfferings.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null}
                },
                new String [] {
                        "Course ID", "Course Name", "Professor", "Semester", "Seats"
                }
        ));
        jScrollPane1.setViewportView(tblCourseOfferings);

        btnCreate.setText("Create New Offering...");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(btnBack)
                                .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addGap(277, 277, 277)
                                .addComponent(btnCreate)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(btnBack)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCreate)
                                .addContainerGap(9, Short.MAX_VALUE))
        );
    }// </editor-fold>

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {
        CardSequencePanel.remove(this);
        CardLayout layout = (CardLayout) CardSequencePanel.getLayout();
        layout.previous(CardSequencePanel);
    }

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {
        CreateCourseOfferingJPanel createPanel = new CreateCourseOfferingJPanel(business, CardSequencePanel);
        CardSequencePanel.add("CreateCourseOfferingJPanel", createPanel);
        CardLayout layout = (CardLayout) CardSequencePanel.getLayout();
        layout.show(CardSequencePanel, "CreateCourseOfferingJPanel");
    }

    private void populateTable() {
        DefaultTableModel dtm = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String[] columnNames = {"Course ID", "Course Name", "Professor", "Semester", "Seats"};
        dtm.setColumnIdentifiers(columnNames);
        tblCourseOfferings.setModel(dtm);

        // This assumes a logical path to your data. You may need to adjust the initial get...() methods.
        for (Department department : business.getUniversity().getDepartmentDirectory().getDepartmentList()) {
            for (CourseSchedule courseSchedule : department.getMasterCourseCatalog().values()) {
                for (CourseOffer courseOffer : courseSchedule.getSchedule()) {
                    Course course = courseOffer.getSubjectCourse();

                    Object[] row = new Object[5];
                    row[0] = course.getCOurseNumber();
                    row[1] = course.getName(); 
                    row[2] = courseOffer.getFacultyProfile().getPerson().getPersonId();
                    row[3] = courseSchedule.getSemester(); 
                    row[4] = courseOffer.getTotalCourseRevenues();

                    dtm.addRow(row);
                }
            }
        }
       
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCreate;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCourseOfferings;
    // End of variables declaration
}