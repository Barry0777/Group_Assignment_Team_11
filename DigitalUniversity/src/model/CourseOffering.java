package model;

import java.util.ArrayList;

/**
 * CourseOffering class representing a specific offering of a course in a semester
 * Author: [Your Name]
 */
public class CourseOffering {
    private String offeringId;
    private Course course;
    private Semester semester;
    private Faculty instructor;
    private String schedule; // e.g., "Mon/Wed 2:00-3:30 PM"
    private String roomLocation;
    private int maxCapacity;
    private int currentEnrollment;
    private boolean enrollmentOpen;
    private String syllabus;
    private ArrayList<Enrollment> enrollments;
    private ArrayList<Assignment> assignments;
    
    // Constructor
    public CourseOffering(String offeringId, Course course, Semester semester, Faculty instructor) {
        this.offeringId = offeringId;
        this.course = course;
        this.semester = semester;
        this.instructor = instructor;
        this.maxCapacity = 30; // Default capacity
        this.currentEnrollment = 0;
        this.enrollmentOpen = true;
        this.enrollments = new ArrayList<>();
        this.assignments = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getOfferingId() {
        return offeringId;
    }
    
    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }
    
    public Course getCourse() {
        return course;
    }
    
    public void setCourse(Course course) {
        this.course = course;
    }
    
    public Semester getSemester() {
        return semester;
    }
    
    public void setSemester(Semester semester) {
        this.semester = semester;
    }
    
    public Faculty getInstructor() {
        return instructor;
    }
    
    public void setInstructor(Faculty instructor) {
        this.instructor = instructor;
    }
    
    public String getSchedule() {
        return schedule;
    }
    
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
    
    public String getRoomLocation() {
        return roomLocation;
    }
    
    public void setRoomLocation(String roomLocation) {
        this.roomLocation = roomLocation;
    }
    
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    
    public int getCurrentEnrollment() {
        return currentEnrollment;
    }
    
    public void setCurrentEnrollment(int currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }
    
    public boolean isEnrollmentOpen() {
        return enrollmentOpen;
    }
    
    public void setEnrollmentOpen(boolean enrollmentOpen) {
        this.enrollmentOpen = enrollmentOpen;
    }
    
    public String getSyllabus() {
        return syllabus;
    }
    
    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }
    
    public ArrayList<Enrollment> getEnrollments() {
        return enrollments;
    }
    
    public void addEnrollment(Enrollment enrollment) {
        if (!enrollments.contains(enrollment)) {
            this.enrollments.add(enrollment);
            this.currentEnrollment++;
        }
    }
    
    public void removeEnrollment(Enrollment enrollment) {
        if (this.enrollments.remove(enrollment)) {
            this.currentEnrollment--;
        }
    }
    
    public ArrayList<Assignment> getAssignments() {
        return assignments;
    }
    
    public void addAssignment(Assignment assignment) {
        if (!assignments.contains(assignment)) {
            this.assignments.add(assignment);
        }
    }
    
    public void removeAssignment(Assignment assignment) {
        this.assignments.remove(assignment);
    }
    
    /**
     * Check if course has available seats
     */
    public boolean hasAvailableSeats() {
        return currentEnrollment < maxCapacity;
    }
    
    /**
     * Get available seats
     */
    public int getAvailableSeats() {
        return maxCapacity - currentEnrollment;
    }
    
    /**
     * Calculate total tuition collected from enrolled students
     */
    public double getTotalTuitionCollected() {
        double total = 0.0;
        for (Enrollment e : enrollments) {
            if (e.isPaid()) {
                total += e.getTuitionAmount();
            }
        }
        return total;
    }
    
    @Override
    public String toString() {
        return course.getCourseId() + " - " + semester.getFullName() + 
               " (" + instructor.getFullName() + ")";
    }
}