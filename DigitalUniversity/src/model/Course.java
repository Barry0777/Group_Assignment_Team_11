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
    // ========  (from Jiayu’s version) ========
    private String number;
    private String name;
    private int credits;
    private int price = 1500; // per credit hour

    // ======== Constructors ========

    
    public Course(String courseId, String title, int creditHours, Department department) {
        this.courseId = courseId;
        this.title = title;
        this.creditHours = creditHours;
        this.department = department;
        this.isCoreRequired = false;
    }

   // Jiayu’s constructor
    public Course(String n, String nm, int ch) {
        this.number = n;
        this.name = nm;
        this.credits = ch;
    }

    // ======== Combined Methods ========


    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCreditHours() { return creditHours; }
    public void setCreditHours(int creditHours) { this.creditHours = creditHours; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public boolean isCoreRequired() { return isCoreRequired; }
    public void setCoreRequired(boolean coreRequired) { isCoreRequired = coreRequired; }

    // --- Jiayu’s Getters/Setters ---
    public String getCOurseNumber() { return number; }
    public int getCoursePrice() { return price * credits; }
    public int getCredits() { return credits; }
    public String getName() { return name; }

    // ======== Utility Methods ========
    @Override
    public String toString() {
        if (courseId != null && title != null) {
            return courseId + " - " + title + " (" + creditHours + " credits)";
        } else {
            return name;
        }
    }

    public void displayCourseInfo() {
        System.out.println("Course ID: " + (courseId != null ? courseId : number));
        System.out.println("Course Name: " + (title != null ? title : name));
        System.out.println("Credits: " + (creditHours != 0 ? creditHours : credits));
        System.out.println("Department: " + (department != null ? department.getName() : "N/A"));
        System.out.println("Total Price: $" + getCoursePrice());
        System.out.println("Core Course: " + (isCoreRequired ? "Yes" : "No"));
    }
}