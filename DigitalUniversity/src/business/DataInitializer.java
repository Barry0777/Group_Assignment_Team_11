package business;

import model.*;
import accesscontrol.*;
import java.time.LocalDate;

/**
 * DataInitializer - Pre-populate system with required data
 * Author: [Your Name]
 */
public class DataInitializer {
    
    private UniversityDirectory directory;
    private AuthenticationService authService;
    
    public DataInitializer() {
        this.directory = UniversityDirectory.getInstance();
        this.authService = AuthenticationService.getInstance();
    }
    
    /**
     * Initialize all required data
     * Requirements: 1 department, 30 persons, 10 students, 10 faculties, 
     * 1 admin, 1 registrar, 1 semester, 5 course offers
     */
    public void initializeData() {
        // 1. Create Department
        Department csDept = new Department("CS001", "Computer Science");
        csDept.setLocation("Building A");
        directory.addDepartment(csDept);
        
        // 2. Create Admin
        Admin admin = new Admin(directory.generateUniversityId(), "John", "Admin", "admin@university.edu");
        admin.setPhoneNumber("123-456-7890");
        directory.addAdmin(admin);
        User adminUser = new User("admin", "admin123", "ADMIN", admin);
        authService.registerUser(adminUser);
        
        // 3. Create Registrar
        Registrar registrar = new Registrar(directory.generateUniversityId(), "Sarah", "Registrar", "registrar@university.edu");
        registrar.setOfficeLocation("Admin Building, Room 101");
        registrar.setOfficeHours("Mon-Fri 9AM-5PM");
        directory.addRegistrar(registrar);
        User registrarUser = new User("registrar", "registrar123", "REGISTRAR", registrar);
        authService.registerUser(registrarUser);
        
        // 4. Create 10 Faculty Members
        String[] facultyFirstNames = {"Michael", "Jennifer", "David", "Emily", "Robert", 
                                      "Lisa", "James", "Maria", "William", "Patricia"};
        String[] facultyLastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", 
                                     "Garcia", "Miller", "Davis", "Rodriguez", "Martinez"};
        
        Faculty[] faculties = new Faculty[10];
        for (int i = 0; i < 10; i++) {
            Faculty faculty = new Faculty(
                directory.generateUniversityId(),
                facultyFirstNames[i],
                facultyLastNames[i],
                facultyFirstNames[i].toLowerCase() + "." + facultyLastNames[i].toLowerCase() + "@university.edu",
                csDept
            );
            faculty.setPhoneNumber("555-" + String.format("%04d", 1000 + i));
            faculty.setOfficeLocation("Faculty Building, Room " + (200 + i));
            faculty.setOfficeHours("Tue/Thu 2-4PM");
            
            directory.addFaculty(faculty);
            csDept.addFaculty(faculty);
            faculties[i] = faculty;
            
            // Create user account for faculty
            String username = "faculty" + (i + 1);
            User facultyUser = new User(username, "pass" + (i + 1), "FACULTY", faculty);
            authService.registerUser(facultyUser);
        }
        
        // 5. Create 10 Students
        String[] studentFirstNames = {"Alice", "Bob", "Charlie", "Diana", "Edward", 
                                      "Fiona", "George", "Hannah", "Ian", "Julia"};
        String[] studentLastNames = {"Anderson", "Baker", "Clark", "Davis", "Evans", 
                                    "Foster", "Green", "Harris", "Jackson", "King"};
        
        Student[] students = new Student[10];
        for (int i = 0; i < 10; i++) {
            Student student = new Student(
                directory.generateUniversityId(),
                studentFirstNames[i],
                studentLastNames[i],
                studentFirstNames[i].toLowerCase() + "." + studentLastNames[i].toLowerCase() + "@student.edu",
                "MSIS"
            );
            student.setPhoneNumber("555-" + String.format("%04d", 2000 + i));
            student.setAddress(String.format("%d Campus Drive, Boston, MA", 100 + i));
            
            directory.addStudent(student);
            csDept.addStudent(student);
            students[i] = student;
            
            // Create user account for student
            String username = "student" + (i + 1);
            User studentUser = new User(username, "pass" + (i + 1), "STUDENT", student);
            authService.registerUser(studentUser);
        }
        
        // 6. Create additional 10 persons (mix of different types) to reach 30 total
        for (int i = 0; i < 5; i++) {
            Faculty extraFaculty = new Faculty(
                directory.generateUniversityId(),
                "ExtraFaculty" + (i + 1),
                "LastName" + (i + 1),
                "extrafaculty" + (i + 1) + "@university.edu",
                csDept
            );
            directory.addFaculty(extraFaculty);
        }
        
        for (int i = 0; i < 5; i++) {
            Student extraStudent = new Student(
                directory.generateUniversityId(),
                "ExtraStudent" + (i + 1),
                "LastName" + (i + 1),
                "extrastudent" + (i + 1) + "@student.edu",
                "MSIS"
            );
            directory.addStudent(extraStudent);
        }
        
        // 7. Create Semester
        Semester fall2025 = new Semester("SEM001", "Fall", 2025);
        fall2025.setStartDate(LocalDate.of(2025, 9, 1));
        fall2025.setEndDate(LocalDate.of(2025, 12, 15));
        fall2025.setActive(true);
        directory.addSemester(fall2025);
        
        // 8. Create Courses
        Course[] courses = new Course[5];
        courses[0] = new Course("INFO 5100", "Application Engineering and Development", 4, csDept);
        courses[0].setCoreRequired(true);
        courses[0].setDescription("Comprehensive course on application development");
        
        courses[1] = new Course("INFO 6205", "Program Structure and Algorithms", 4, csDept);
        courses[1].setDescription("Data structures and algorithms");
        
        courses[2] = new Course("INFO 6150", "Web Design and User Experience", 4, csDept);
        courses[2].setDescription("Modern web development and UX principles");
        
        courses[3] = new Course("INFO 7245", "Big Data Systems", 4, csDept);
        courses[3].setDescription("Big data technologies and analytics");
        
        courses[4] = new Course("INFO 7500", "Cryptocurrency and Smart Contracts", 4, csDept);
        courses[4].setDescription("Blockchain technology and applications");
        
        for (Course c : courses) {
            directory.addCourse(c);
        }
        
        // 9. Create 5 Course Offerings with faculty assigned
        for (int i = 0; i < 5; i++) {
            CourseOffering offering = new CourseOffering(
                directory.generateOfferingId(),
                courses[i],
                fall2025,
                faculties[i]
            );
            offering.setMaxCapacity(30);
            offering.setSchedule(getSchedule(i));
            offering.setRoomLocation("Room " + (300 + i));
            offering.setEnrollmentOpen(true);
            offering.setSyllabus("Syllabus for " + courses[i].getTitle());
            
            directory.addCourseOffering(offering);
            faculties[i].addCourse(offering);
        }
        
        // 10. Enroll some students in courses (seat assignments)
        // Enroll students 0-4 in course 0 (INFO 5100)
        CourseOffering info5100Offering = directory.getCourseOfferings().get(0);
        for (int i = 0; i < 5; i++) {
            Enrollment enrollment = new Enrollment(
                directory.generateEnrollmentId(),
                students[i],
                info5100Offering
            );
            directory.addEnrollment(enrollment);
            students[i].addEnrollment(enrollment);
            info5100Offering.addEnrollment(enrollment);
            
            // Add tuition balance
            students[i].setAccountBalance(students[i].getAccountBalance() + enrollment.getTuitionAmount());
        }
        
        // Enroll students in other courses
        for (int i = 0; i < 5; i++) {
            for (int j = 1; j < 3; j++) { // Each student enrolls in 2 more courses
                CourseOffering offering = directory.getCourseOfferings().get(j);
                Enrollment enrollment = new Enrollment(
                    directory.generateEnrollmentId(),
                    students[i],
                    offering
                );
                directory.addEnrollment(enrollment);
                students[i].addEnrollment(enrollment);
                offering.addEnrollment(enrollment);
                
                // Add tuition balance
                students[i].setAccountBalance(students[i].getAccountBalance() + enrollment.getTuitionAmount());
            }
        }
        
        System.out.println("Data initialization complete!");
        System.out.println("Total Persons: " + directory.getPersons().size());
        System.out.println("Total Students: " + directory.getStudents().size());
        System.out.println("Total Faculty: " + directory.getFaculties().size());
        System.out.println("Total Admins: " + directory.getAdmins().size());
        System.out.println("Total Registrars: " + directory.getRegistrars().size());
        System.out.println("Total Departments: " + directory.getDepartments().size());
        System.out.println("Total Courses: " + directory.getCourses().size());
        System.out.println("Total Semesters: " + directory.getSemesters().size());
        System.out.println("Total Course Offerings: " + directory.getCourseOfferings().size());
        System.out.println("Total Enrollments: " + directory.getEnrollments().size());
    }
    
    /**
     * Generate different schedules for courses
     */
    private String getSchedule(int index) {
        String[] schedules = {
            "Mon/Wed 9:00-10:30 AM",
            "Mon/Wed 11:00-12:30 PM",
            "Tue/Thu 2:00-3:30 PM",
            "Tue/Thu 4:00-5:30 PM",
            "Wed/Fri 1:00-2:30 PM"
        };
        return schedules[index % schedules.length];
    }
}