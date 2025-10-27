package business;

import model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * RegistrarService - Business logic for registrar operations
 * Author: [Your Name - Registrar Use Case]
 */
public class RegistrarService {
    
    private UniversityDirectory directory;
    
    public RegistrarService() {
        this.directory = UniversityDirectory.getInstance();
    }
    
    // ========== COURSE OFFERING MANAGEMENT ==========
    
    /**
     * Create a new course offering
     */
    public CourseOffering createCourseOffering(Course course, Semester semester, 
                                              Faculty instructor, String schedule, 
                                              String roomLocation, int maxCapacity) {
        // Validation
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (semester == null) {
            throw new IllegalArgumentException("Semester cannot be null");
        }
        if (instructor == null) {
            throw new IllegalArgumentException("Instructor cannot be null");
        }
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be greater than 0");
        }
        
        // Generate offering ID
        String offeringId = directory.generateOfferingId();
        
        // Create offering
        CourseOffering offering = new CourseOffering(offeringId, course, semester, instructor);
        offering.setSchedule(schedule);
        offering.setRoomLocation(roomLocation);
        offering.setMaxCapacity(maxCapacity);
        offering.setEnrollmentOpen(true);
        
        // Add to directory
        directory.addCourseOffering(offering);
        
        // Add to faculty's courses
        instructor.addCourse(offering);
        
        return offering;
    }
    
    /**
     * Update course offering details
     */
    public boolean updateCourseOffering(CourseOffering offering, Faculty instructor, 
                                       int maxCapacity, String schedule, String roomLocation) {
        if (offering == null) {
            return false;
        }
        
        // Update instructor if changed
        if (instructor != null && !instructor.equals(offering.getInstructor())) {
            // Remove from old instructor
            Faculty oldInstructor = offering.getInstructor();
            if (oldInstructor != null) {
                oldInstructor.removeCourse(offering);
            }
            
            // Assign to new instructor
            offering.setInstructor(instructor);
            instructor.addCourse(offering);
        }
        
        // Update capacity (only if greater than current enrollment)
        if (maxCapacity > 0 && maxCapacity >= offering.getCurrentEnrollment()) {
            offering.setMaxCapacity(maxCapacity);
        }
        
        // Update schedule and room
        if (schedule != null && !schedule.trim().isEmpty()) {
            offering.setSchedule(schedule);
        }
        if (roomLocation != null && !roomLocation.trim().isEmpty()) {
            offering.setRoomLocation(roomLocation);
        }
        
        return true;
    }
    
    /**
     * Delete a course offering (only if no students enrolled)
     */
    public boolean deleteCourseOffering(CourseOffering offering) {
        if (offering == null) {
            return false;
        }
        
        // Check if there are enrollments
        if (offering.getCurrentEnrollment() > 0) {
            throw new IllegalArgumentException("Cannot delete offering with enrolled students");
        }
        
        // Remove from instructor
        Faculty instructor = offering.getInstructor();
        if (instructor != null) {
            instructor.removeCourse(offering);
        }
        
        // Remove from directory
        directory.getCourseOfferings().remove(offering);
        
        return true;
    }
    
    // ========== STUDENT REGISTRATION (ADMIN SIDE) ==========
    
    /**
     * Enroll a student into a course offering (admin/registrar side)
     */
    public Enrollment enrollStudent(Student student, CourseOffering offering) {
        // Validation
        if (student == null || offering == null) {
            throw new IllegalArgumentException("Student and course offering cannot be null");
        }
        
        // Check if course has available seats
        if (!offering.hasAvailableSeats()) {
            throw new IllegalArgumentException("Course is full");
        }
        
        // Check if student is already enrolled
        for (Enrollment e : student.getEnrollments()) {
            if (e.getCourseOffering().equals(offering) && e.isActive()) {
                throw new IllegalArgumentException("Student is already enrolled in this course");
            }
        }
        
        // Check credit hour limit (max 8 credits per semester)
        int currentCredits = student.getCurrentSemesterCredits(offering.getSemester());
        int courseCredits = offering.getCourse().getCreditHours();
        
        if (currentCredits + courseCredits > 8) {
            throw new IllegalArgumentException(
                "Student cannot exceed 8 credit hours per semester. Current: " + currentCredits
            );
        }
        
        // Create enrollment
        String enrollmentId = directory.generateEnrollmentId();
        Enrollment enrollment = new Enrollment(enrollmentId, student, offering);
        
        // Add enrollment
        directory.addEnrollment(enrollment);
        student.addEnrollment(enrollment); // This now adds tuition to balance automatically
        offering.addEnrollment(enrollment);
        
        return enrollment;
    }
    
    /**
     * Drop a student from a course
     */
    public boolean dropStudent(Student student, CourseOffering offering) {
        if (student == null || offering == null) {
            return false;
        }
        
        // Find enrollment
        Enrollment enrollment = null;
        for (Enrollment e : student.getEnrollments()) {
            if (e.getCourseOffering().equals(offering) && e.isActive()) {
                enrollment = e;
                break;
            }
        }
        
        if (enrollment == null) {
            return false;
        }
        
        // Mark as inactive
        enrollment.setActive(false);
        enrollment.setDropDate(LocalDate.now());
        
        // Remove from offering
        offering.removeEnrollment(enrollment);
        
        // Refund tuition if paid
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
            refund.setDescription("Refund for dropping " + offering.getCourse().getCourseId());
            refund.setAmount(-enrollment.getTuitionAmount()); // Negative for refund
            student.addPayment(refund);
        } else {
            // Just remove the tuition charge
            student.setAccountBalance(student.getAccountBalance() - enrollment.getTuitionAmount());
        }
        
        return true;
    }
    
    // ========== TUITION & FINANCIAL RECONCILIATION ==========
    
    /**
     * Get tuition payment status for all students
     */
    public ArrayList<HashMap<String, Object>> getTuitionPaymentStatus() {
        ArrayList<HashMap<String, Object>> statusList = new ArrayList<>();
        
        for (Student student : directory.getStudents()) {
            HashMap<String, Object> status = new HashMap<>();
            status.put("student", student);
            status.put("balance", student.calculateUnpaidBalance());
            status.put("isPaid", student.calculateUnpaidBalance() <= 0);
            
            statusList.add(status);
        }
        
        return statusList;
    }
    
    /**
     * Generate financial report for a semester
     */
    public HashMap<String, Object> generateFinancialReport(Semester semester) {
        HashMap<String, Object> report = new HashMap<>();
        
        if (semester == null) {
            return report;
        }
        
        double totalTuitionCollected = 0.0;
        double unpaidTuition = 0.0;
        HashMap<String, Double> departmentRevenue = new HashMap<>();
        
        // Calculate tuition for each course offering in the semester
        for (CourseOffering co : directory.getCourseOfferingsBySemester(semester)) {
            double courseTuition = co.getTotalTuitionCollected();
            totalTuitionCollected += courseTuition;
            
            // Track by department
            String deptName = co.getCourse().getDepartment().getName();
            departmentRevenue.put(deptName, 
                departmentRevenue.getOrDefault(deptName, 0.0) + courseTuition);
            
            // Calculate unpaid
            for (Enrollment e : co.getEnrollments()) {
                if (e.isActive() && !e.isPaid()) {
                    unpaidTuition += e.getTuitionAmount();
                }
            }
        }
        
        report.put("semester", semester.getFullName());
        report.put("totalCollected", totalTuitionCollected);
        report.put("unpaidTuition", unpaidTuition);
        report.put("departmentRevenue", departmentRevenue);
        
        return report;
    }
    
    /**
     * Get students with unpaid tuition
     */
    public ArrayList<Student> getStudentsWithUnpaidTuition() {
        ArrayList<Student> unpaidStudents = new ArrayList<>();
        
        for (Student student : directory.getStudents()) {
            if (student.calculateUnpaidBalance() > 0) {
                unpaidStudents.add(student);
            }
        }
        
        return unpaidStudents;
    }
    
    // ========== REPORTING & ANALYTICS ==========
    
    /**
     * Generate enrollment report by department
     */
    public HashMap<String, Integer> getEnrollmentByDepartment() {
        HashMap<String, Integer> enrollmentCount = new HashMap<>();
        
        for (Department dept : directory.getDepartments()) {
            int count = 0;
            for (Student student : directory.getStudents()) {
                // Count active enrollments in department courses
                for (Enrollment e : student.getEnrollments()) {
                    if (e.isActive() && 
                        e.getCourseOffering().getCourse().getDepartment().equals(dept)) {
                        count++;
                    }
                }
            }
            enrollmentCount.put(dept.getName(), count);
        }
        
        return enrollmentCount;
    }
    
    /**
     * Generate enrollment report by course
     */
    public HashMap<String, Integer> getEnrollmentByCourse(Semester semester) {
        HashMap<String, Integer> enrollmentCount = new HashMap<>();
        
        for (CourseOffering co : directory.getCourseOfferingsBySemester(semester)) {
            String courseName = co.getCourse().getCourseId() + " - " + co.getCourse().getTitle();
            enrollmentCount.put(courseName, co.getCurrentEnrollment());
        }
        
        return enrollmentCount;
    }
    
    /**
     * Calculate GPA distribution by program
     */
    public HashMap<String, ArrayList<Double>> getGPADistributionByProgram() {
        HashMap<String, ArrayList<Double>> distribution = new HashMap<>();
        
        for (Student student : directory.getStudents()) {
            String program = student.getProgram();
            
            if (!distribution.containsKey(program)) {
                distribution.put(program, new ArrayList<>());
            }
            
            distribution.get(program).add(student.getOverallGPA());
        }
        
        return distribution;
    }
    
    /**
     * Get all course offerings for a semester
     */
    public ArrayList<CourseOffering> getSemesterCourseOfferings(Semester semester) {
        if (semester == null) {
            return new ArrayList<>();
        }
        
        return directory.getCourseOfferingsBySemester(semester);
    }
    
    /**
     * Create a new semester
     */
    public Semester createSemester(String term, int year, LocalDate startDate, LocalDate endDate) {
        // Validation
        if (term == null || term.trim().isEmpty()) {
            throw new IllegalArgumentException("Term cannot be empty");
        }
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Invalid year");
        }
        
        // Generate semester ID
        String semesterId = "SEM" + String.format("%03d", directory.getSemesters().size() + 1);
        
        // Create semester
        Semester semester = new Semester(semesterId, term, year);
        semester.setStartDate(startDate);
        semester.setEndDate(endDate);
        semester.setActive(true);
        
        // Add to directory
        directory.addSemester(semester);
        
        return semester;
    }
    
    /**
     * Get total enrollment across all courses
     */
    public int getTotalEnrollment() {
        return directory.getEnrollments().size();
    }
    
    /**
     * Get enrollment statistics
     */
    public HashMap<String, Object> getEnrollmentStatistics(Semester semester) {
        HashMap<String, Object> stats = new HashMap<>();
        
        if (semester == null) {
            return stats;
        }
        
        int totalEnrollments = 0;
        int totalCapacity = 0;
        int coursesOffered = 0;
        
        for (CourseOffering co : directory.getCourseOfferingsBySemester(semester)) {
            totalEnrollments += co.getCurrentEnrollment();
            totalCapacity += co.getMaxCapacity();
            coursesOffered++;
        }
        
        double utilizationRate = totalCapacity > 0 ? 
            ((double) totalEnrollments / totalCapacity) * 100 : 0.0;
        
        stats.put("totalEnrollments", totalEnrollments);
        stats.put("totalCapacity", totalCapacity);
        stats.put("coursesOffered", coursesOffered);
        stats.put("utilizationRate", utilizationRate);
        
        return stats;
    }
}