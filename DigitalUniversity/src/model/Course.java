package model;

/**
 * Course class representing a course in the university catalog
 * Author: [Your Name]
 */
public class Course {
    private String courseId; // e.g., "INFO 5100"
    private String title;
    private String description;
    private int creditHours;
    private Department department;
    private boolean isCoreRequired; // For MSIS, INFO 5100 is core
    
    // Constructor
    public Course(String courseId, String title, int creditHours, Department department) {
        this.courseId = courseId;
        this.title = title;
        this.creditHours = creditHours;
        this.department = department;
        this.isCoreRequired = false;
    }
    
    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }
    
    public void setCourseId(String courseId) {
        this.courseId = courseId;
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
    
    public int getCreditHours() {
        return creditHours;
    }
    
    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }
    
    public Department getDepartment() {
        return department;
    }
    
    public void setDepartment(Department department) {
        this.department = department;
    }
    
    public boolean isCoreRequired() {
        return isCoreRequired;
    }
    
    public void setCoreRequired(boolean coreRequired) {
        this.isCoreRequired = coreRequired;
    }
    
    @Override
    public String toString() {
        return courseId + " - " + title + " (" + creditHours + " credits)";
    }
}