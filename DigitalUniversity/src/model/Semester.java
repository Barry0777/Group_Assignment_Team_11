package model;

import java.time.LocalDate;

/**
 * Semester class representing an academic semester
 * Author: [Your Name]
 */
public class Semester {
    private String semesterId;
    private String term; // e.g., "Fall", "Spring", "Summer"
    private int year;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
    
    // Constructor
    public Semester(String semesterId, String term, int year) {
        this.semesterId = semesterId;
        this.term = term;
        this.year = year;
        this.isActive = false;
    }
    
    // Getters and Setters
    public String getSemesterId() {
        return semesterId;
    }
    
    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
    }
    
    public String getTerm() {
        return term;
    }
    
    public void setTerm(String term) {
        this.term = term;
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    /**
     * Get full semester name (e.g., "Fall 2025")
     */
    public String getFullName() {
        return term + " " + year;
    }
    
    @Override
    public String toString() {
        return getFullName();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Semester semester = (Semester) obj;
        return semesterId.equals(semester.semesterId);
    }
}