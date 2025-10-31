package business;

import model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * StudentService - Business logic for student operations
 * Author: [Your Name - Student Use Case]
 */
public class StudentService {
    
    private UniversityDirectory directory;
    
    public StudentService() {
        this.directory = UniversityDirectory.getInstance();
    }
    
    // ========== COURSE REGISTRATION ==========
    
    /**
     * Enroll student in a course offering
     */
    public Enrollment enrollInCourse(Student student, CourseOffering offering) {
        // Validation
        if (student == null || offering == null) {
            throw new IllegalArgumentException("Student and course offering cannot be null");
        }
        
        // Check if enrollment is open
        if (!offering.isEnrollmentOpen()) {
            throw new IllegalArgumentException("Enrollment is closed for this course");
        }
        
        // Check if course has available seats
        if (!offering.hasAvailableSeats()) {
            throw new IllegalArgumentException("Course is full");
        }
        
        // Check if student is already enrolled
        for (Enrollment e : student.getEnrollments()) {
            if (e.getCourseOffering().equals(offering) && e.isActive()) {
                throw new IllegalArgumentException("Already enrolled in this course");
            }
        }
        
        // Check credit hour limit (max 8 credits per semester)
        int currentCredits = student.getCurrentSemesterCredits(offering.getSemester());
        int courseCredits = offering.getCourse().getCreditHours();
        
        if (currentCredits + courseCredits > 8) {
            throw new IllegalArgumentException(
                "Cannot exceed 8 credit hours per semester. Current: " + currentCredits + 
                ", Course: " + courseCredits
            );
        }
        
        // Create enrollment
        String enrollmentId = directory.generateEnrollmentId();
        Enrollment enrollment = new Enrollment(enrollmentId, student, offering);
        
        // Add enrollment to all relevant places
        directory.addEnrollment(enrollment);
        student.addEnrollment(enrollment); // This now adds tuition to balance automatically
        offering.addEnrollment(enrollment);
        
        return enrollment;
    }
    
    /**
     * Drop a course
     */
    public boolean dropCourse(Student student, Enrollment enrollment) {
        if (student == null || enrollment == null) {
            return false;
        }
        
        if (!enrollment.isActive()) {
            throw new IllegalArgumentException("Cannot drop an inactive enrollment");
        }
        
        CourseOffering offering = enrollment.getCourseOffering();
        
        // Mark enrollment as inactive
        enrollment.setActive(false);
        
        // Remove from course offering
        offering.removeEnrollment(enrollment);
        
        // Refund tuition if already paid
        if (enrollment.isPaid()) {
            student.refundEnrollment(enrollment);
            
            // Create refund payment record
            String paymentId = directory.generatePaymentId();
            TuitionPayment refund = new TuitionPayment(
                paymentId, 
                student, 
                enrollment,
                enrollment.getTuitionAmount()
            );
            refund.setAmount(-enrollment.getTuitionAmount()); // Negative for refund
            refund.setDescription("Refund for dropping " + offering.getCourse().getCourseId());
            student.addPayment(refund);
        } else {
            // Just remove the tuition charge
            student.setAccountBalance(student.getAccountBalance() - enrollment.getTuitionAmount());
        }
        
        return true;
    }
    
    /**
     * Get available course offerings for a semester
     */
    public ArrayList<CourseOffering> getAvailableCourses(Semester semester) {
        if (semester == null) {
            return new ArrayList<>();
        }
        
        ArrayList<CourseOffering> available = new ArrayList<>();
        
        for (CourseOffering co : directory.getCourseOfferingsBySemester(semester)) {
            if (co.isEnrollmentOpen() && co.hasAvailableSeats()) {
                available.add(co);
            }
        }
        
        return available;
    }
    
    // ========== COURSE SEARCH ==========
    
    /**
     * Search courses by course ID
     */
    public ArrayList<CourseOffering> searchByCourseId(String courseId, Semester semester) {
        ArrayList<CourseOffering> results = new ArrayList<>();
        
        if (courseId == null || courseId.trim().isEmpty()) {
            return results;
        }
        
        for (CourseOffering co : directory.getCourseOfferingsBySemester(semester)) {
            if (co.getCourse().getCourseId().toLowerCase().contains(courseId.toLowerCase())) {
                results.add(co);
            }
        }
        
        return results;
    }
    
    /**
     * Search courses by instructor name
     */
    public ArrayList<CourseOffering> searchByInstructor(String instructorName, Semester semester) {
        ArrayList<CourseOffering> results = new ArrayList<>();
        
        if (instructorName == null || instructorName.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = instructorName.toLowerCase();
        
        for (CourseOffering co : directory.getCourseOfferingsBySemester(semester)) {
            Faculty instructor = co.getInstructor();
            if (instructor != null) {
                String fullName = instructor.getFullName().toLowerCase();
                if (fullName.contains(searchTerm)) {
                    results.add(co);
                }
            }
        }
        
        return results;
    }
    
    /**
     * Search courses by course title
     */
    public ArrayList<CourseOffering> searchByTitle(String title, Semester semester) {
        ArrayList<CourseOffering> results = new ArrayList<>();
        
        if (title == null || title.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = title.toLowerCase();
        
        for (CourseOffering co : directory.getCourseOfferingsBySemester(semester)) {
            String courseTitle = co.getCourse().getTitle().toLowerCase();
            if (courseTitle.contains(searchTerm)) {
                results.add(co);
            }
        }
        
        return results;
    }
    
    // ========== GRADUATION AUDIT ==========
    
    /**
     * Check if student is ready to graduate
     * MSIS: 32 credits required, including INFO 5100 (4 credit core)
     */
    public boolean isEligibleToGraduate(Student student) {
        if (student == null) {
            return false;
        }
        
        return student.isEligibleToGraduate();
    }
    
    /**
     * Get graduation status details
     */
    public HashMap<String, Object> getGraduationStatus(Student student) {
        HashMap<String, Object> status = new HashMap<>();
        
        if (student == null) {
            return status;
        }
        
        int totalCredits = student.getTotalCreditsCompleted();
        int requiredCredits = 32; // MSIS requirement
        boolean hasCoreCourse = false;
        
        // Check for INFO 5100
        for (Enrollment e : student.getEnrollments()) {
            if (e.getCourseOffering().getCourse().getCourseId().equals("INFO 5100") 
                && e.getGrade() != null && !e.getGrade().equals("F")) {
                hasCoreCourse = true;
                break;
            }
        }
        
        status.put("totalCredits", totalCredits);
        status.put("requiredCredits", requiredCredits);
        status.put("creditsRemaining", Math.max(0, requiredCredits - totalCredits));
        status.put("hasCoreCourse", hasCoreCourse);
        status.put("isEligible", student.isEligibleToGraduate());
        status.put("overallGPA", student.getOverallGPA());
        
        return status;
    }
    
    /**
     * Update student's total credits completed
     */
    public void updateCreditsCompleted(Student student) {
        if (student == null) {
            return;
        }
        
        int totalCredits = 0;
        
        for (Enrollment e : student.getEnrollments()) {
            // Only count completed courses with passing grades
            if (!e.isActive() && e.getGrade() != null && !e.getGrade().equals("F")) {
                totalCredits += e.getCourseOffering().getCourse().getCreditHours();
            }
        }
        
        student.setTotalCreditsCompleted(totalCredits);
    }
    
    // ========== TRANSCRIPT ==========
    
    /**
     * Get transcript for a specific semester (only paid enrollments)
     */
    public ArrayList<Enrollment> getTranscriptBySemester(Student student, Semester semester) {
        ArrayList<Enrollment> transcript = new ArrayList<>();
        
        if (student == null || semester == null) {
            return transcript;
        }
        
        for (Enrollment e : student.getPaidEnrollments()) {
            if (e.getCourseOffering().getSemester().equals(semester)) {
                transcript.add(e);
            }
        }
        
        return transcript;
    }
    
    /**
     * Get complete transcript (only paid enrollments)
     */
    public ArrayList<Enrollment> getCompleteTranscript(Student student) {
        if (student == null) {
            return new ArrayList<>();
        }
        
        return student.getPaidEnrollments();
    }
    
    /**
     * Calculate term GPA for a semester
     */
    public double calculateTermGPA(Student student, Semester semester) {
        if (student == null || semester == null) {
            return 0.0;
        }
        
        return GradeCalculator.calculateTermGPA(student, semester);
    }
    
    /**
     * Check if student can view transcript (all tuition must be paid)
     */
    public boolean canViewTranscript(Student student) {
        if (student == null) {
            return false;
        }
        
        // Student can view transcript only if all active enrollments are paid
        return student.calculateUnpaidBalance() <= 0;
    }
    
    // ========== TUITION MANAGEMENT ==========
    
    /**
     * Pay tuition for a specific enrollment/course
     */
    public TuitionPayment payForCourse(Student student, Enrollment enrollment) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment cannot be null");
        }
        
        if (!student.getEnrollments().contains(enrollment)) {
            throw new IllegalArgumentException("Enrollment does not belong to this student");
        }
        
        if (enrollment.isPaid()) {
            throw new IllegalArgumentException("This course is already paid");
        }
        
        if (!enrollment.isActive()) {
            throw new IllegalArgumentException("Cannot pay for inactive enrollment");
        }
        
        // Generate payment ID
        String paymentId = directory.generatePaymentId();
        
        // Use student's payForEnrollment method
        boolean success = student.payForEnrollment(enrollment, paymentId);
        
        if (!success) {
            throw new IllegalArgumentException("Payment failed");
        }
        
        // Get the payment that was just created
        ArrayList<TuitionPayment> payments = student.getPaymentHistory();
        return payments.get(payments.size() - 1);
    }
    
    /**
     * Get list of unpaid enrollments for payment selection
     */
    public ArrayList<Enrollment> getUnpaidEnrollments(Student student) {
        if (student == null) {
            return new ArrayList<>();
        }
        
        return student.getUnpaidEnrollments();
    }
    
    /**
     * Get payment history
     */
    public ArrayList<TuitionPayment> getPaymentHistory(Student student) {
        if (student == null) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(student.getPaymentHistory());
    }
    
    /**
     * Calculate total unpaid tuition
     */
    public double calculateUnpaidBalance(Student student) {
        if (student == null) {
            return 0.0;
        }
        
        return student.calculateUnpaidBalance();
    }
    
    /**
     * Calculate total tuition for all active enrollments
     */
    public double calculateTotalTuition(Student student) {
        if (student == null) {
            return 0.0;
        }
        
        return student.getAccountBalance();
    }
    
    // ========== ASSIGNMENT SUBMISSION ==========
    
    /**
     * Submit an assignment (for future implementation)
     * This would be used to track when student submits work
     */
    public boolean submitAssignment(Student student, Assignment assignment) {
        if (student == null || assignment == null) {
            return false;
        }
        
        // For now, just mark as submitted with 0 score
        // Faculty will grade it later
        if (!assignment.hasSubmitted(student)) {
            assignment.gradeStudent(student, 0.0);
            return true;
        }
        
        return false;
    }
    
    /**
     * Get student's assignments for a course
     */
    public ArrayList<Assignment> getCourseAssignments(Student student, CourseOffering offering) {
        if (offering == null) {
            return new ArrayList<>();
        }
        
        return offering.getAssignments();
    }
    
    /**
     * Get student's score for an assignment
     */
    public Double getAssignmentScore(Student student, Assignment assignment) {
        if (student == null || assignment == null) {
            return null;
        }
        
        return assignment.getStudentScore(student);
    }
}