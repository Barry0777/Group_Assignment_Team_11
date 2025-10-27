package business;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * FacultyService - Business logic for faculty operations
 * Author: [Your Name - Faculty Use Case]
 */
public class FacultyService {
    
    private UniversityDirectory directory;
    
    public FacultyService() {
        this.directory = UniversityDirectory.getInstance();
    }
    
    // ========== COURSE MANAGEMENT ==========
    
    /**
     * Update course offering details
     */
    public boolean updateCourseOffering(CourseOffering offering, String title, 
                                       String description, String schedule, 
                                       String roomLocation, int maxCapacity) {
        if (offering == null) {
            return false;
        }
        
        if (title != null && !title.trim().isEmpty()) {
            offering.getCourse().setTitle(title);
        }
        if (description != null && !description.trim().isEmpty()) {
            offering.getCourse().setDescription(description);
        }
        if (schedule != null && !schedule.trim().isEmpty()) {
            offering.setSchedule(schedule);
        }
        if (roomLocation != null && !roomLocation.trim().isEmpty()) {
            offering.setRoomLocation(roomLocation);
        }
        if (maxCapacity > 0 && maxCapacity >= offering.getCurrentEnrollment()) {
            offering.setMaxCapacity(maxCapacity);
        }
        
        return true;
    }
    
    /**
     * Upload or modify syllabus
     */
    public boolean updateSyllabus(CourseOffering offering, String syllabus) {
        if (offering == null || syllabus == null) {
            return false;
        }
        
        offering.setSyllabus(syllabus);
        return true;
    }
    
    /**
     * Open course enrollment
     */
    public boolean openEnrollment(CourseOffering offering) {
        if (offering == null) {
            return false;
        }
        
        offering.setEnrollmentOpen(true);
        return true;
    }
    
    /**
     * Close course enrollment
     */
    public boolean closeEnrollment(CourseOffering offering) {
        if (offering == null) {
            return false;
        }
        
        offering.setEnrollmentOpen(false);
        return true;
    }
    
    // ========== STUDENT MANAGEMENT ==========
    
    /**
     * Get list of enrolled students for a course
     */
    public ArrayList<Student> getEnrolledStudents(CourseOffering offering) {
        ArrayList<Student> students = new ArrayList<>();
        
        if (offering == null) {
            return students;
        }
        
        for (Enrollment e : offering.getEnrollments()) {
            if (e.isActive()) {
                students.add(e.getStudent());
            }
        }
        
        return students;
    }
    
    /**
     * Get student's transcript summary for a specific student
     */
    public ArrayList<Enrollment> getStudentTranscript(Student student) {
        if (student == null) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(student.getEnrollments());
    }
    
    /**
     * Get student's progress in a specific course
     */
    public HashMap<String, Object> getStudentProgress(Student student, CourseOffering offering) {
        HashMap<String, Object> progress = new HashMap<>();
        
        if (student == null || offering == null) {
            return progress;
        }
        
        // Find enrollment
        Enrollment enrollment = null;
        for (Enrollment e : student.getEnrollments()) {
            if (e.getCourseOffering().equals(offering)) {
                enrollment = e;
                break;
            }
        }
        
        if (enrollment == null) {
            return progress;
        }
        
        // Calculate progress
        ArrayList<Assignment> assignments = offering.getAssignments();
        int totalAssignments = assignments.size();
        int completedAssignments = 0;
        double totalScore = 0.0;
        double maxScore = 0.0;
        
        for (Assignment assignment : assignments) {
            maxScore += assignment.getMaxPoints();
            if (assignment.hasSubmitted(student)) {
                completedAssignments++;
                totalScore += assignment.getStudentScore(student);
            }
        }
        
        double percentage = maxScore > 0 ? (totalScore / maxScore) * 100 : 0.0;
        
        progress.put("totalAssignments", totalAssignments);
        progress.put("completedAssignments", completedAssignments);
        progress.put("totalScore", totalScore);
        progress.put("maxScore", maxScore);
        progress.put("percentage", percentage);
        progress.put("currentGrade", enrollment.getGrade());
        
        return progress;
    }
    
    // ========== GRADING ==========
    
    /**
     * Create a new assignment
     */
    public Assignment createAssignment(CourseOffering offering, String title, 
                                      String description, double maxPoints) {
        if (offering == null || title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid assignment data");
        }
        
        if (maxPoints <= 0) {
            throw new IllegalArgumentException("Max points must be greater than 0");
        }
        
        String assignmentId = directory.generateAssignmentId();
        Assignment assignment = new Assignment(assignmentId, title, offering, maxPoints);
        assignment.setDescription(description);
        
        offering.addAssignment(assignment);
        
        return assignment;
    }
    
    /**
     * Grade an assignment for a student
     */
    public boolean gradeAssignment(Assignment assignment, Student student, double score) {
        if (assignment == null || student == null) {
            return false;
        }
        
        if (score < 0 || score > assignment.getMaxPoints()) {
            throw new IllegalArgumentException("Score must be between 0 and " + assignment.getMaxPoints());
        }
        
        assignment.gradeStudent(student, score);
        return true;
    }
    
    /**
     * Calculate and assign final grade for a student in a course
     */
    public boolean assignFinalGrade(Student student, CourseOffering offering) {
        if (student == null || offering == null) {
            return false;
        }
        
        // Find enrollment
        Enrollment enrollment = null;
        for (Enrollment e : student.getEnrollments()) {
            if (e.getCourseOffering().equals(offering)) {
                enrollment = e;
                break;
            }
        }
        
        if (enrollment == null) {
            return false;
        }
        
        // Calculate percentage
        double percentage = GradeCalculator.calculateCoursePercentage(student, offering);
        
        // Convert to letter grade
        String letterGrade = GradeCalculator.calculateLetterGrade(percentage);
        
        // Assign grade
        enrollment.setGrade(letterGrade);
        enrollment.setActive(false); // Mark as completed
        
        // Update student's GPA
        GradeCalculator.updateStudentGPA(student);
        
        return true;
    }
    
    /**
     * Rank students by total grade percentage
     */
    public ArrayList<HashMap<String, Object>> rankStudentsByGrade(CourseOffering offering) {
        ArrayList<HashMap<String, Object>> rankings = new ArrayList<>();
        
        if (offering == null) {
            return rankings;
        }
        
        // Calculate percentage for each student
        for (Enrollment e : offering.getEnrollments()) {
            Student student = e.getStudent();
            double percentage = GradeCalculator.calculateCoursePercentage(student, offering);
            
            HashMap<String, Object> studentData = new HashMap<>();
            studentData.put("student", student);
            studentData.put("percentage", percentage);
            studentData.put("grade", e.getGrade());
            
            rankings.add(studentData);
        }
        
        // Sort by percentage (descending)
        Collections.sort(rankings, new Comparator<HashMap<String, Object>>() {
            @Override
            public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
                double p1 = (double) o1.get("percentage");
                double p2 = (double) o2.get("percentage");
                return Double.compare(p2, p1); // Descending order
            }
        });
        
        return rankings;
    }
    
    /**
     * Calculate class GPA for a course
     */
    public double calculateClassGPA(CourseOffering offering) {
        if (offering == null || offering.getEnrollments().isEmpty()) {
            return 0.0;
        }
        
        double totalGradePoints = 0.0;
        int count = 0;
        
        for (Enrollment e : offering.getEnrollments()) {
            if (e.getGrade() != null) {
                totalGradePoints += e.getGradePoints();
                count++;
            }
        }
        
        return count > 0 ? totalGradePoints / count : 0.0;
    }
    
    // ========== PERFORMANCE REPORTING ==========
    
    /**
     * Generate course performance report
     */
    public HashMap<String, Object> generateCourseReport(CourseOffering offering) {
        HashMap<String, Object> report = new HashMap<>();
        
        if (offering == null) {
            return report;
        }
        
        // Basic info
        report.put("courseId", offering.getCourse().getCourseId());
        report.put("courseTitle", offering.getCourse().getTitle());
        report.put("semester", offering.getSemester().getFullName());
        report.put("instructor", offering.getInstructor().getFullName());
        
        // Enrollment stats
        report.put("enrollmentCount", offering.getCurrentEnrollment());
        report.put("maxCapacity", offering.getMaxCapacity());
        
        // Grade statistics
        double averageGrade = GradeCalculator.calculateCourseAverage(offering);
        report.put("averageGrade", averageGrade);
        
        // Calculate class GPA
        double classGPA = calculateClassGPA(offering);
        report.put("classGPA", classGPA);
        
        // Grade distribution
        HashMap<String, Integer> gradeDistribution = calculateGradeDistribution(offering);
        report.put("gradeDistribution", gradeDistribution);
        
        return report;
    }
    
    /**
     * Calculate grade distribution
     */
    private HashMap<String, Integer> calculateGradeDistribution(CourseOffering offering) {
        HashMap<String, Integer> distribution = new HashMap<>();
        
        // Initialize all grades to 0
        for (String grade : GradeCalculator.getAllGrades()) {
            distribution.put(grade, 0);
        }
        
        // Count grades
        for (Enrollment e : offering.getEnrollments()) {
            String grade = e.getGrade();
            if (grade != null) {
                distribution.put(grade, distribution.getOrDefault(grade, 0) + 1);
            }
        }
        
        return distribution;
    }
    
    /**
     * Get course offerings for a faculty member by semester
     */
    public ArrayList<CourseOffering> getCoursesBySemester(Faculty faculty, Semester semester) {
        if (faculty == null) {
            return new ArrayList<>();
        }
        
        return faculty.getCoursesBySemester(semester);
    }
    
    /**
     * Calculate total tuition collected from enrolled students
     */
    public double getTotalTuitionCollected(CourseOffering offering) {
        if (offering == null) {
            return 0.0;
        }
        
        return offering.getTotalTuitionCollected();
    }
    
    /**
     * Get all course offerings for a faculty member
     */
    public ArrayList<CourseOffering> getAllFacultyCourses(Faculty faculty) {
        if (faculty == null) {
            return new ArrayList<>();
        }
        
        return faculty.getAssignedCourses();
    }

    
}