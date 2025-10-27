package model;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * Assignment class representing an assignment in a course offering
 * Author: [Your Name]
 */
public class Assignment {
    private String assignmentId;
    private String title;
    private String description;
    private CourseOffering courseOffering;
    private LocalDate dueDate;
    private double maxPoints;
    private HashMap<Student, Double> submissions; // Student -> score mapping
    
    // Updated Constructor to match the createAssignment call
    public Assignment(String title, String description, CourseOffering courseOffering, double maxPoints) {
        this.assignmentId = generateAssignmentId(); // Auto-generate ID
        this.title = title;
        this.description = description;
        this.courseOffering = courseOffering;
        this.maxPoints = maxPoints;
        this.submissions = new HashMap<>();
    }
    
    // Helper method to generate unique assignment IDs
    private static int assignmentCounter = 1000;
    private String generateAssignmentId() {
        return "ASSGN-" + (assignmentCounter++);
    }
    
    // Getters and Setters
    public String getAssignmentId() {
        return assignmentId;
    }
    
    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public CourseOffering getCourseOffering() {
        return courseOffering;
    }
    
    public void setCourseOffering(CourseOffering courseOffering) {
        this.courseOffering = courseOffering;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public double getMaxPoints() {
        return maxPoints;
    }
    
    public void setMaxPoints(double maxPoints) {
        this.maxPoints = maxPoints;
    }
    
    public HashMap<Student, Double> getSubmissions() {
        return submissions;
    }
    
    /**
     * Submit or grade an assignment for a student
     */
    public void gradeStudent(Student student, double score) {
        if (score < 0) score = 0;
        if (score > maxPoints) score = maxPoints;
        submissions.put(student, score);
    }
    
    /**
     * Get a student's score for this assignment
     */
    public Double getStudentScore(Student student) {
        return submissions.getOrDefault(student, 0.0);
    }
    
    /**
     * Check if student has submitted
     */
    public boolean hasSubmitted(Student student) {
        return submissions.containsKey(student);
    }
    
    @Override
    public String toString() {
        return title + " (" + maxPoints + " pts) - Due: " + 
               (dueDate != null ? dueDate.toString() : "No due date");
    }
}