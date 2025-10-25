package model;

import java.time.LocalDateTime;

/**
 * TuitionPayment class representing a tuition payment transaction
 * Author: [Your Name]
 */
public class TuitionPayment {
    private String paymentId;
    private Student student;
    private double amount;
    private LocalDateTime paymentDate;
    private String paymentMethod; // e.g., "Credit Card", "Bank Transfer"
    private String semester;
    private String description;
    
    // Constructor
    public TuitionPayment(String paymentId, Student student, double amount, String semester) {
        this.paymentId = paymentId;
        this.student = student;
        this.amount = amount;
        this.paymentDate = LocalDateTime.now();
        this.semester = semester;
        this.paymentMethod = "Online Payment";
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
               " (" + paymentDate.toString() + ")";
    }
}