package business;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ReportService - Generate various reports and analytics
 * Author: [Your Name]
 */
public class ReportService {
    
    private UniversityDirectory directory;
    
    public ReportService() {
        this.directory = UniversityDirectory.getInstance();
    }
    
    // ========== ADMIN ANALYTICS DASHBOARD ==========
    
    /**
     * Get total active users by role
     */
    public HashMap<String, Integer> getTotalUsersByRole() {
        HashMap<String, Integer> roleCount = new HashMap<>();
        
        roleCount.put("ADMIN", directory.getAdmins().size());
        roleCount.put("FACULTY", directory.getFaculties().size());
        roleCount.put("STUDENT", directory.getStudents().size());
        roleCount.put("REGISTRAR", directory.getRegistrars().size());
        
        return roleCount;
    }
    
    /**
     * Get total courses offered per semester
     */
    public HashMap<String, Integer> getCoursesPerSemester() {
        HashMap<String, Integer> courseCount = new HashMap<>();
        
        for (Semester semester : directory.getSemesters()) {
            int count = directory.getCourseOfferingsBySemester(semester).size();
            courseCount.put(semester.getFullName(), count);
        }
        
        return courseCount;
    }
    
    /**
     * Get total enrolled students per course
     */
    public HashMap<String, Integer> getEnrollmentPerCourse(Semester semester) {
        HashMap<String, Integer> enrollmentCount = new HashMap<>();
        
        for (CourseOffering offering : directory.getCourseOfferingsBySemester(semester)) {
            String courseName = offering.getCourse().getCourseId();
            enrollmentCount.put(courseName, offering.getCurrentEnrollment());
        }
        
        return enrollmentCount;
    }
    
    /**
     * Calculate tuition revenue summary
     */
    public HashMap<String, Object> getTuitionRevenueSummary() {
        HashMap<String, Object> summary = new HashMap<>();
        
        double totalTuitionBilled = 0.0;
        double totalTuitionPaid = 0.0;
        double totalOutstanding = 0.0;
        
        for (Student student : directory.getStudents()) {
            double balance = student.getAccountBalance();
            
            // Calculate total billed (sum of all enrollment tuitions)
            for (Enrollment e : student.getEnrollments()) {
                totalTuitionBilled += e.getTuitionAmount();
                if (e.isPaid()) {
                    totalTuitionPaid += e.getTuitionAmount();
                }
            }
            
            // Outstanding balance
            if (balance > 0) {
                totalOutstanding += balance;
            }
        }
        
        summary.put("totalBilled", totalTuitionBilled);
        summary.put("totalPaid", totalTuitionPaid);
        summary.put("totalOutstanding", totalOutstanding);
        summary.put("collectionRate", totalTuitionBilled > 0 ? 
            (totalTuitionPaid / totalTuitionBilled) * 100 : 0.0);
        
        return summary;
    }
    
    /**
     * Generate comprehensive admin dashboard data
     */
    public HashMap<String, Object> generateAdminDashboard() {
        HashMap<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("usersByRole", getTotalUsersByRole());
        dashboard.put("totalPersons", directory.getPersons().size());
        dashboard.put("totalDepartments", directory.getDepartments().size());
        dashboard.put("totalCourses", directory.getCourses().size());
        dashboard.put("totalSemesters", directory.getSemesters().size());
        dashboard.put("totalEnrollments", directory.getEnrollments().size());
        dashboard.put("tuitionSummary", getTuitionRevenueSummary());
        
        return dashboard;
    }
    
    // ========== FACULTY PERFORMANCE REPORTS ==========
    
    /**
     * Generate detailed course report for faculty
     */
    public HashMap<String, Object> generateFacultyCourseReport(CourseOffering offering) {
        HashMap<String, Object> report = new HashMap<>();
        
        if (offering == null) {
            return report;
        }
        
        // Basic course info
        report.put("courseId", offering.getCourse().getCourseId());
        report.put("courseTitle", offering.getCourse().getTitle());
        report.put("semester", offering.getSemester().getFullName());
        report.put("instructor", offering.getInstructor().getFullName());
        report.put("schedule", offering.getSchedule());
        report.put("roomLocation", offering.getRoomLocation());
        
        // Enrollment statistics
        report.put("currentEnrollment", offering.getCurrentEnrollment());
        report.put("maxCapacity", offering.getMaxCapacity());
        report.put("availableSeats", offering.getAvailableSeats());
        report.put("utilizationRate", offering.getMaxCapacity() > 0 ? 
            ((double) offering.getCurrentEnrollment() / offering.getMaxCapacity()) * 100 : 0.0);
        
        // Grade statistics
        double averageGrade = GradeCalculator.calculateCourseAverage(offering);
        report.put("averageGrade", averageGrade);
        
        // Class GPA
        double classGPA = 0.0;
        int gradeCount = 0;
        for (Enrollment e : offering.getEnrollments()) {
            if (e.getGrade() != null) {
                classGPA += e.getGradePoints();
                gradeCount++;
            }
        }
        classGPA = gradeCount > 0 ? classGPA / gradeCount : 0.0;
        report.put("classGPA", classGPA);
        
        // Grade distribution
        HashMap<String, Integer> gradeDistribution = new HashMap<>();
        for (String grade : GradeCalculator.getAllGrades()) {
            gradeDistribution.put(grade, 0);
        }
        for (Enrollment e : offering.getEnrollments()) {
            if (e.getGrade() != null) {
                gradeDistribution.put(e.getGrade(), 
                    gradeDistribution.getOrDefault(e.getGrade(), 0) + 1);
            }
        }
        report.put("gradeDistribution", gradeDistribution);
        
        // Assignment statistics
        report.put("totalAssignments", offering.getAssignments().size());
        
        // Tuition info
        report.put("tuitionCollected", offering.getTotalTuitionCollected());
        
        return report;
    }
    
    /**
     * Generate report for all faculty courses
     */
    public ArrayList<HashMap<String, Object>> generateAllFacultyCoursesReport(Faculty faculty) {
        ArrayList<HashMap<String, Object>> reports = new ArrayList<>();
        
        if (faculty == null) {
            return reports;
        }
        
        for (CourseOffering offering : faculty.getAssignedCourses()) {
            reports.add(generateFacultyCourseReport(offering));
        }
        
        return reports;
    }
    
    // ========== STUDENT TRANSCRIPT REPORTS ==========
    
    /**
     * Generate formatted transcript for student
     */
    public ArrayList<HashMap<String, Object>> generateTranscript(Student student) {
        ArrayList<HashMap<String, Object>> transcript = new ArrayList<>();
        
        if (student == null) {
            return transcript;
        }
        
        for (Enrollment e : student.getEnrollments()) {
            HashMap<String, Object> record = new HashMap<>();
            
            CourseOffering co = e.getCourseOffering();
            Semester semester = co.getSemester();
            
            record.put("term", semester.getFullName());
            record.put("courseId", co.getCourse().getCourseId());
            record.put("courseName", co.getCourse().getTitle());
            record.put("credits", co.getCourse().getCreditHours());
            record.put("grade", e.getGrade());
            record.put("gradePoints", e.getGradePoints());
            record.put("qualityPoints", e.getQualityPoints());
            
            // Calculate term GPA
            double termGPA = GradeCalculator.calculateTermGPA(student, semester);
            record.put("termGPA", termGPA);
            
            // Overall GPA
            record.put("overallGPA", student.getOverallGPA());
            
            // Academic standing
            String standing = GradeCalculator.determineAcademicStanding(termGPA, student.getOverallGPA());
            record.put("academicStanding", standing);
            
            transcript.add(record);
        }
        
        return transcript;
    }
    
    /**
     * Generate transcript for specific semester
     */
    public ArrayList<HashMap<String, Object>> generateTranscriptBySemester(Student student, Semester semester) {
        ArrayList<HashMap<String, Object>> transcript = new ArrayList<>();
        
        if (student == null || semester == null) {
            return transcript;
        }
        
        for (Enrollment e : student.getEnrollments()) {
            if (e.getCourseOffering().getSemester().equals(semester)) {
                HashMap<String, Object> record = new HashMap<>();
                
                CourseOffering co = e.getCourseOffering();
                
                record.put("term", semester.getFullName());
                record.put("courseId", co.getCourse().getCourseId());
                record.put("courseName", co.getCourse().getTitle());
                record.put("credits", co.getCourse().getCreditHours());
                record.put("grade", e.getGrade());
                record.put("gradePoints", e.getGradePoints());
                record.put("qualityPoints", e.getQualityPoints());
                
                // Term GPA
                double termGPA = GradeCalculator.calculateTermGPA(student, semester);
                record.put("termGPA", termGPA);
                
                // Overall GPA
                record.put("overallGPA", student.getOverallGPA());
                
                // Academic standing
                String standing = GradeCalculator.determineAcademicStanding(termGPA, student.getOverallGPA());
                record.put("academicStanding", standing);
                
                transcript.add(record);
            }
        }
        
        return transcript;
    }
    
    // ========== REGISTRAR REPORTS ==========
    
    /**
     * Generate comprehensive enrollment report
     */
    public HashMap<String, Object> generateEnrollmentReport(Semester semester) {
        HashMap<String, Object> report = new HashMap<>();
        
        if (semester == null) {
            return report;
        }
        
        int totalEnrollments = 0;
        int totalCapacity = 0;
        HashMap<String, Integer> departmentEnrollment = new HashMap<>();
        
        for (CourseOffering co : directory.getCourseOfferingsBySemester(semester)) {
            totalEnrollments += co.getCurrentEnrollment();
            totalCapacity += co.getMaxCapacity();
            
            String deptName = co.getCourse().getDepartment().getName();
            departmentEnrollment.put(deptName, 
                departmentEnrollment.getOrDefault(deptName, 0) + co.getCurrentEnrollment());
        }
        
        report.put("semester", semester.getFullName());
        report.put("totalEnrollments", totalEnrollments);
        report.put("totalCapacity", totalCapacity);
        report.put("utilizationRate", totalCapacity > 0 ? 
            ((double) totalEnrollments / totalCapacity) * 100 : 0.0);
        report.put("departmentEnrollment", departmentEnrollment);
        
        return report;
    }
    
    /**
     * Generate GPA distribution report
     */
    public HashMap<String, Object> generateGPADistributionReport() {
        HashMap<String, Object> report = new HashMap<>();
        
        ArrayList<Double> allGPAs = new ArrayList<>();
        HashMap<String, Integer> gpaRanges = new HashMap<>();
        
        // Initialize GPA ranges
        gpaRanges.put("4.0", 0);
        gpaRanges.put("3.5-3.99", 0);
        gpaRanges.put("3.0-3.49", 0);
        gpaRanges.put("2.5-2.99", 0);
        gpaRanges.put("2.0-2.49", 0);
        gpaRanges.put("Below 2.0", 0);
        
        for (Student student : directory.getStudents()) {
            double gpa = student.getOverallGPA();
            allGPAs.add(gpa);
            
            // Categorize into ranges
            if (gpa == 4.0) {
                gpaRanges.put("4.0", gpaRanges.get("4.0") + 1);
            } else if (gpa >= 3.5) {
                gpaRanges.put("3.5-3.99", gpaRanges.get("3.5-3.99") + 1);
            } else if (gpa >= 3.0) {
                gpaRanges.put("3.0-3.49", gpaRanges.get("3.0-3.49") + 1);
            } else if (gpa >= 2.5) {
                gpaRanges.put("2.5-2.99", gpaRanges.get("2.5-2.99") + 1);
            } else if (gpa >= 2.0) {
                gpaRanges.put("2.0-2.49", gpaRanges.get("2.0-2.49") + 1);
            } else {
                gpaRanges.put("Below 2.0", gpaRanges.get("Below 2.0") + 1);
            }
        }
        
        // Calculate average GPA
        double averageGPA = 0.0;
        if (!allGPAs.isEmpty()) {
            for (double gpa : allGPAs) {
                averageGPA += gpa;
            }
            averageGPA /= allGPAs.size();
        }
        
        report.put("averageGPA", averageGPA);
        report.put("gpaDistribution", gpaRanges);
        report.put("totalStudents", allGPAs.size());
        
        return report;
    }
    
    /**
     * Generate financial summary report
     */
    public HashMap<String, Object> generateFinancialSummary(Semester semester) {
        HashMap<String, Object> summary = new HashMap<>();
        
        if (semester == null) {
            return summary;
        }
        
        double totalRevenue = 0.0;
        double paidTuition = 0.0;
        double unpaidTuition = 0.0;
        HashMap<String, Double> departmentRevenue = new HashMap<>();
        
        for (CourseOffering co : directory.getCourseOfferingsBySemester(semester)) {
            String deptName = co.getCourse().getDepartment().getName();
            
            for (Enrollment e : co.getEnrollments()) {
                double tuition = e.getTuitionAmount();
                totalRevenue += tuition;
                
                if (e.isPaid()) {
                    paidTuition += tuition;
                    departmentRevenue.put(deptName, 
                        departmentRevenue.getOrDefault(deptName, 0.0) + tuition);
                } else {
                    unpaidTuition += tuition;
                }
            }
        }
        
        summary.put("semester", semester.getFullName());
        summary.put("totalRevenue", totalRevenue);
        summary.put("paidTuition", paidTuition);
        summary.put("unpaidTuition", unpaidTuition);
        summary.put("collectionRate", totalRevenue > 0 ? 
            (paidTuition / totalRevenue) * 100 : 0.0);
        summary.put("departmentRevenue", departmentRevenue);
        
        return summary;
    }
    
    /**
     * Get students with academic standing issues
     */
    public ArrayList<HashMap<String, Object>> getStudentsWithAcademicIssues() {
        ArrayList<HashMap<String, Object>> studentList = new ArrayList<>();
        
        for (Student student : directory.getStudents()) {
            if (!student.getAcademicStanding().equals("Good Standing")) {
                HashMap<String, Object> info = new HashMap<>();
                info.put("student", student);
                info.put("gpa", student.getOverallGPA());
                info.put("standing", student.getAcademicStanding());
                info.put("creditsCompleted", student.getTotalCreditsCompleted());
                
                studentList.add(info);
            }
        }
        
        return studentList;
    }
}