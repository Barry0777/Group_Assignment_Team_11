package model;

import java.time.LocalDate;

/**
 * Enrollment class representing a student's enrollment in a course offering
 * Author: [Your Name]
 */
public class Enrollment {
    private String enrollmentId;
    private Student student;
    private CourseOffering courseOffering;
    private LocalDate enrollmentDate;
    private LocalDate dropDate;
    private boolean isActive;
    private String grade; // A, A-, B+, B, etc.
    private double gradePoints; // 4.0, 3.7, 3.3, etc.
    private boolean isPaid;
    private double tuitionAmount;
    
    // Constructor
    public Enrollment(String enrollmentId, Student student, CourseOffering courseOffering) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.courseOffering = courseOffering;
        this.enrollmentDate = LocalDate.now();
        this.isActive = true;
        this.isPaid = false;
        // Calculate tuition (example: $1000 per credit hour)
        this.tuitionAmount = courseOffering.getCourse().getCreditHours() * 1000.0;
    }
    
    // Getters and Setters
    public String getEnrollmentId() {
        return enrollmentId;
    }
    
    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public CourseOffering getCourseOffering() {
        return courseOffering;
    }
    
    public void setCourseOffering(CourseOffering courseOffering) {
        this.courseOffering = courseOffering;
    }
    
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
    
    public LocalDate getDropDate() {
        return dropDate;
    }
    
    public void setDropDate(LocalDate dropDate) {
        this.dropDate = dropDate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public String getGrade() {
        return grade;
    }
    
    public void setGrade(String grade) {
        this.grade = grade;
        this.gradePoints = convertGradeToPoints(grade);
    }
    
    public double getGradePoints() {
        return gradePoints;
    }
    
    public boolean isPaid() {
        return isPaid;
    }
    
    public void setPaid(boolean paid) {
        this.isPaid = paid;
    }
    
    public double getTuitionAmount() {
        return tuitionAmount;
    }
    
    public void setTuitionAmount(double tuitionAmount) {
        this.tuitionAmount = tuitionAmount;
    }
    
    /**
     * Convert letter grade to grade points
     * A = 4.0, A- = 3.7, B+ = 3.3, B = 3.0, B- = 2.7, 
     * C+ = 2.3, C = 2.0, C- = 1.7, F = 0.0
     */
    private double convertGradeToPoints(String grade) {
        if (grade == null) return 0.0;
        
        switch (grade.toUpperCase()) {
            case "A": return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B": return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C": return 2.0;
            case "C-": return 1.7;
            case "F": return 0.0;
            default: return 0.0;
        }
    }
    
    /**
     * Calculate quality points (grade points * credit hours)
     */
    public double getQualityPoints() {
        if (grade == null) return 0.0;
        return gradePoints * courseOffering.getCourse().getCreditHours();
    }
    
    @Override
    public String toString() {
        return student.getFullName() + " - " + courseOffering.getCourse().getCourseId() + 
               " (" + courseOffering.getSemester().getFullName() + ")";
    }
}