package model;

import java.time.LocalDateTime;

/**
 * TuitionPayment class representing a tuition payment transaction
 * Author: [Your Name]
 */
public class TuitionPayment {
    private String paymentId;
    private Student student;
    private Enrollment enrollment;
    private double amount;
    private LocalDateTime paymentDate;
    private String paymentMethod; // e.g., "Credit Card", "Bank Transfer"
    private String semester;
    private String description;
    
    // Constructor
    public TuitionPayment(String paymentId, Student student, Enrollment enrollment, double amount) {
        this.paymentId = paymentId;
        this.student = student;
        this.enrollment = enrollment;
        this.amount = amount;
        this.paymentDate = LocalDateTime.now();
        this.semester = enrollment.getCourseOffering().getSemester().getFullName();
        this.paymentMethod = "Online Payment";
        this.description = "Tuition for " + enrollment.getCourseOffering().getCourse().getCourseId();
    }
    
    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public Enrollment getEnrollment() {
        return enrollment;
    }
    
    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getSemester() {
        return semester;
    }
    
    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "Payment " + paymentId + " - $" + String.format("%.2f", amount) + 
               " (" + paymentDate.toString() + ") - " + description;
    }
}