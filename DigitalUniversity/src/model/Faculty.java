package model;

import java.util.ArrayList;

/**
 * Faculty class representing a faculty member
 * Author: [Your Name]
 */
public class Faculty extends Person {
    private Department department;
    private String officeLocation;
    private String officeHours;
    private ArrayList<CourseOffering> assignedCourses;
    
    // Constructor
    public Faculty(String universityId, String firstName, String lastName, String email, Department department) {
        super(universityId, firstName, lastName, email);
        this.department = department;
        this.assignedCourses = new ArrayList<>();
    }
    
    // Getters and Setters
    public Department getDepartment() {
        return department;
    }
    
    public void setDepartment(Department department) {
        this.department = department;
    }
    
    public String getOfficeLocation() {
        return officeLocation;
    }
    
    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }
    
    public String getOfficeHours() {
        return officeHours;
    }
    
    public void setOfficeHours(String officeHours) {
        this.officeHours = officeHours;
    }
    
    public ArrayList<CourseOffering> getAssignedCourses() {
        return assignedCourses;
    }
    
    public void addCourse(CourseOffering courseOffering) {
        if (!assignedCourses.contains(courseOffering)) {
            this.assignedCourses.add(courseOffering);
        }
    }
    
    public void removeCourse(CourseOffering courseOffering) {
        this.assignedCourses.remove(courseOffering);
    }
    
    /**
     * Get courses for a specific semester
     */
    public ArrayList<CourseOffering> getCoursesBySemester(Semester semester) {
        ArrayList<CourseOffering> semesterCourses = new ArrayList<>();
        for (CourseOffering co : assignedCourses) {
            if (co.getSemester().equals(semester)) {
                semesterCourses.add(co);
            }
        }
        return semesterCourses;
    }
    
    @Override
    public String toString() {
        return super.toString() + " - " + (department != null ? department.getName() : "No Department");
    }
}