package model;

import java.util.ArrayList;

/**
 * Student class representing a student in the university
 * Author: [Your Name]
 */
public class Student extends Person {
    private String program; // e.g., "MSIS"
    private String academicStanding; // Good Standing, Academic Warning, Academic Probation
    private double overallGPA;
    private int totalCreditsCompleted;
    private double accountBalance; // For tuition tracking
    private ArrayList<Enrollment> enrollments;
    private ArrayList<TuitionPayment> paymentHistory;
    
    // Constructor
    public Student(String universityId, String firstName, String lastName, String email, String program) {
        super(universityId, firstName, lastName, email);
        this.program = program;
        this.academicStanding = "Good Standing";
        this.overallGPA = 0.0;
        this.totalCreditsCompleted = 0;
        this.accountBalance = 0.0;
        this.enrollments = new ArrayList<>();
        this.paymentHistory = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getProgram() {
        return program;
    }
    
    public void setProgram(String program) {
        this.program = program;
    }
    
    public String getAcademicStanding() {
        return academicStanding;
    }
    
    public void setAcademicStanding(String academicStanding) {
        this.academicStanding = academicStanding;
    }
    
    public double getOverallGPA() {
        return overallGPA;
    }
    
    public void setOverallGPA(double overallGPA) {
        this.overallGPA = overallGPA;
    }
    
    public int getTotalCreditsCompleted() {
        return totalCreditsCompleted;
    }
    
    public void setTotalCreditsCompleted(int totalCreditsCompleted) {
        this.totalCreditsCompleted = totalCreditsCompleted;
    }
    
    public double getAccountBalance() {
        return accountBalance;
    }
    
    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }
    
    public ArrayList<Enrollment> getEnrollments() {
        return enrollments;
    }
    
    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        // Add tuition to account balance when enrolling
        this.accountBalance += enrollment.getTuitionAmount();
    }
    
    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
        // If the enrollment was paid, refund the student
        if (enrollment.isPaid()) {
            this.accountBalance -= enrollment.getTuitionAmount();
        }
    }
    
    public ArrayList<TuitionPayment> getPaymentHistory() {
        return paymentHistory;
    }
    
    public void addPayment(TuitionPayment payment) {
        this.paymentHistory.add(payment);
    }
    
    /**
     * Get list of unpaid enrollments
     */
    public ArrayList<Enrollment> getUnpaidEnrollments() {
        ArrayList<Enrollment> unpaid = new ArrayList<>();
        for (Enrollment e : enrollments) {
            if (!e.isPaid() && e.isActive()) {
                unpaid.add(e);
            }
        }
        return unpaid;
    }
    
    /**
     * Get list of paid enrollments (for transcript display)
     */
    public ArrayList<Enrollment> getPaidEnrollments() {
        ArrayList<Enrollment> paid = new ArrayList<>();
        for (Enrollment e : enrollments) {
            if (e.isPaid()) {
                paid.add(e);
            }
        }
        return paid;
    }
    
    /**
     * Calculate unpaid balance
     */
    public double calculateUnpaidBalance() {
        double unpaid = 0.0;
        for (Enrollment e : enrollments) {
            if (!e.isPaid() && e.isActive()) {
                unpaid += e.getTuitionAmount();
            }
        }
        return unpaid;
    }
    
    /**
     * Process payment for a specific enrollment
     */
    public boolean payForEnrollment(Enrollment enrollment, String paymentId) {
        if (enrollment == null || !enrollments.contains(enrollment)) {
            return false;
        }
        
        if (enrollment.isPaid()) {
            return false; // Already paid
        }
        
        // Mark enrollment as paid
        enrollment.setPaid(true);
        
        // Reduce account balance
        this.accountBalance -= enrollment.getTuitionAmount();
        
        // Create payment record
        TuitionPayment payment = new TuitionPayment(paymentId, this, enrollment, enrollment.getTuitionAmount());
        this.paymentHistory.add(payment);
        
        return true;
    }
    
    /**
     * Process refund when dropping a paid course
     */
    public void refundEnrollment(Enrollment enrollment) {
        if (enrollment != null && enrollment.isPaid()) {
            enrollment.setPaid(false);
            this.accountBalance -= enrollment.getTuitionAmount(); // Reduce debt (negative balance means credit)
        }
    }
    
    /**
     * Check if student is eligible to graduate (MSIS: 32 credits with INFO 5100)
     */
    public boolean isEligibleToGraduate() {
        if (totalCreditsCompleted < 32) {
            return false;
        }
        
        // Check if INFO 5100 is completed
        for (Enrollment e : enrollments) {
            if (e.getCourseOffering().getCourse().getCourseId().equals("INFO 5100") 
                && e.getGrade() != null && !e.getGrade().equals("F")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Calculate current semester credit load
     */
    public int getCurrentSemesterCredits(Semester semester) {
        int credits = 0;
        for (Enrollment e : enrollments) {
            if (e.getCourseOffering().getSemester().equals(semester) && e.isActive()) {
                credits += e.getCourseOffering().getCourse().getCreditHours();
            }
        }
        return credits;
    }
    
    @Override
    public String toString() {
        return super.toString() + " - " + program + " (GPA: " + String.format("%.2f", overallGPA) + ")";
    }
}