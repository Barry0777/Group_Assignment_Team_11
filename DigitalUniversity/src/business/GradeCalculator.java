package business;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * GradeCalculator - Utility class for calculating GPAs and academic standing
 * Author: [Your Name]
 */
public class GradeCalculator {
    
    // Grade to points mapping
    private static final HashMap<String, Double> GRADE_MAP = new HashMap<>();
    
    static {
        GRADE_MAP.put("A", 4.0);
        GRADE_MAP.put("A-", 3.7);
        GRADE_MAP.put("B+", 3.3);
        GRADE_MAP.put("B", 3.0);
        GRADE_MAP.put("B-", 2.7);
        GRADE_MAP.put("C+", 2.3);
        GRADE_MAP.put("C", 2.0);
        GRADE_MAP.put("C-", 1.7);
        GRADE_MAP.put("F", 0.0);
    }
    
    /**
     * Convert grade letter to points
     */
    public static double getGradePoints(String grade) {
        return GRADE_MAP.getOrDefault(grade, 0.0);
    }
    
    /**
     * Get all valid grades
     */
    public static String[] getAllGrades() {
        return new String[]{"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "F"};
    }
    
    /**
     * Calculate term GPA for a specific semester
     */
    public static double calculateTermGPA(Student student, Semester semester) {
        double totalQualityPoints = 0.0;
        int totalCredits = 0;
        
        for (Enrollment e : student.getEnrollments()) {
            if (e.getCourseOffering().getSemester().equals(semester) && e.getGrade() != null) {
                int credits = e.getCourseOffering().getCourse().getCreditHours();
                double gradePoints = getGradePoints(e.getGrade());
                totalQualityPoints += gradePoints * credits;
                totalCredits += credits;
            }
        }
        
        if (totalCredits == 0) return 0.0;
        return totalQualityPoints / totalCredits;
    }
    
    /**
     * Calculate overall GPA for all semesters
     */
    public static double calculateOverallGPA(Student student) {
        double totalQualityPoints = 0.0;
        int totalCredits = 0;
        
        for (Enrollment e : student.getEnrollments()) {
            if (e.getGrade() != null && !e.isActive()) { // Only completed courses
                int credits = e.getCourseOffering().getCourse().getCreditHours();
                double gradePoints = getGradePoints(e.getGrade());
                totalQualityPoints += gradePoints * credits;
                totalCredits += credits;
            }
        }
        
        if (totalCredits == 0) return 0.0;
        return totalQualityPoints / totalCredits;
    }
    
    /**
     * Determine academic standing based on term and overall GPA
     * Good Standing: Term GPA ≥ 3.0 and Overall GPA ≥ 3.0
     * Academic Warning: Term GPA < 3.0 (even if overall GPA ≥ 3.0)
     * Academic Probation: Overall GPA < 3.0 (regardless of term GPA)
     */
    public static String determineAcademicStanding(double termGPA, double overallGPA) {
        if (overallGPA < 3.0) {
            return "Academic Probation";
        } else if (termGPA < 3.0) {
            return "Academic Warning";
        } else {
            return "Good Standing";
        }
    }
    
    /**
     * Update student's overall GPA and academic standing
     */
    public static void updateStudentGPA(Student student) {
        double overallGPA = calculateOverallGPA(student);
        student.setOverallGPA(overallGPA);
        
        // Get most recent semester
        Semester latestSemester = getLatestSemester(student);
        if (latestSemester != null) {
            double termGPA = calculateTermGPA(student, latestSemester);
            String standing = determineAcademicStanding(termGPA, overallGPA);
            student.setAcademicStanding(standing);
        }
    }
    
    /**
     * Get the latest semester the student is enrolled in
     */
    private static Semester getLatestSemester(Student student) {
        Semester latest = null;
        for (Enrollment e : student.getEnrollments()) {
            Semester sem = e.getCourseOffering().getSemester();
            if (latest == null || sem.getYear() > latest.getYear() || 
                (sem.getYear() == latest.getYear() && isLaterTerm(sem.getTerm(), latest.getTerm()))) {
                latest = sem;
            }
        }
        return latest;
    }
    
    /**
     * Determine if term1 comes after term2
     */
    private static boolean isLaterTerm(String term1, String term2) {
        String[] termOrder = {"Spring", "Summer", "Fall"};
        int index1 = -1, index2 = -1;
        for (int i = 0; i < termOrder.length; i++) {
            if (termOrder[i].equalsIgnoreCase(term1)) index1 = i;
            if (termOrder[i].equalsIgnoreCase(term2)) index2 = i;
        }
        return index1 > index2;
    }
    
    /**
     * Calculate letter grade from percentage score
     */
    public static String calculateLetterGrade(double percentage) {
        if (percentage >= 93) return "A";
        else if (percentage >= 90) return "A-";
        else if (percentage >= 87) return "B+";
        else if (percentage >= 83) return "B";
        else if (percentage >= 80) return "B-";
        else if (percentage >= 77) return "C+";
        else if (percentage >= 73) return "C";
        else if (percentage >= 70) return "C-";
        else return "F";
    }
    
    /**
     * Calculate total percentage for a student in a course
     */
    public static double calculateCoursePercentage(Student student, CourseOffering courseOffering) {
        ArrayList<Assignment> assignments = courseOffering.getAssignments();
        if (assignments.isEmpty()) return 0.0;
        
        double totalPoints = 0.0;
        double maxPoints = 0.0;
        
        for (Assignment a : assignments) {
            maxPoints += a.getMaxPoints();
            Double score = a.getStudentScore(student);
            if (score != null) {
                totalPoints += score;
            }
        }
        
        if (maxPoints == 0) return 0.0;
        return (totalPoints / maxPoints) * 100.0;
    }
    
    /**
     * Calculate average grade for a course offering
     */
    public static double calculateCourseAverage(CourseOffering courseOffering) {
        ArrayList<Enrollment> enrollments = courseOffering.getEnrollments();
        if (enrollments.isEmpty()) return 0.0;
        
        double totalPercentage = 0.0;
        int count = 0;
        
        for (Enrollment e : enrollments) {
            if (e.getGrade() != null) {
                double percentage = calculateCoursePercentage(e.getStudent(), courseOffering);
                totalPercentage += percentage;
                count++;
            }
        }
        
        if (count == 0) return 0.0;
        return totalPercentage / count;
    }
}