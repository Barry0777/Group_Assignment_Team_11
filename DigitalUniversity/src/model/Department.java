package model;

import java.util.ArrayList;

/**
 * Department class representing an academic department
 * Author: [Your Name]
 */
public class Department {
    private String departmentId;
    private String name;
    private String location;
    private ArrayList<Faculty> facultyMembers;
    private ArrayList<Student> students;
    
    // Constructor
    public Department(String departmentId, String name) {
        this.departmentId = departmentId;
        this.name = name;
        this.facultyMembers = new ArrayList<>();
        this.students = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getDepartmentId() {
        return departmentId;
    }
    
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public ArrayList<Faculty> getFacultyMembers() {
        return facultyMembers;
    }
    
    public void addFaculty(Faculty faculty) {
        if (!facultyMembers.contains(faculty)) {
            this.facultyMembers.add(faculty);
        }
    }
    
    public void removeFaculty(Faculty faculty) {
        this.facultyMembers.remove(faculty);
    }
    
    public ArrayList<Student> getStudents() {
        return students;
    }
    
    public void addStudent(Student student) {
        if (!students.contains(student)) {
            this.students.add(student);
        }
    }
    
    public void removeStudent(Student student) {
        this.students.remove(student);
    }
    
    @Override
    public String toString() {
        return name + " (" + departmentId + ")";
    }
}