package business;

import model.*;
import accesscontrol.*;
import java.util.ArrayList;

/**
 * AdminService - Business logic for administrator operations
 * Author: [Your Name - Admin Use Case]
 */
public class AdminService {
    
    private UniversityDirectory directory;
    private AuthenticationService authService;
    
    public AdminService() {
        this.directory = UniversityDirectory.getInstance();
        this.authService = AuthenticationService.getInstance();
    }
    
    // ========== USER ACCOUNT MANAGEMENT ==========
    
    /**
     * Create a new user account
     */
    public boolean createUserAccount(String username, String password, String role, Person person) {
        // Validation
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (role == null || !isValidRole(role)) {
            throw new IllegalArgumentException("Invalid role");
        }
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        
        // Check if username already exists
        if (authService.getUserByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        User user = new User(username, password, role, person);
        return authService.registerUser(user);
    }
    
    /**
     * Modify existing user account
     */
    public boolean modifyUserAccount(String username, String newPassword, boolean isActive) {
        User user = authService.getUserByUsername(username);
        if (user == null) {
            return false;
        }
        
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            user.setPassword(newPassword);
        }
        user.setActive(isActive);
        
        return true;
    }
    
    /**
     * Delete user account
     */
    public boolean deleteUserAccount(String username) {
        return authService.deleteUser(username);
    }
    
    /**
     * Get all users by role
     */
    public ArrayList<User> getUsersByRole(String role) {
        ArrayList<User> result = new ArrayList<>();
        for (User user : authService.getAllUsers().values()) {
            if (user.getRole().equalsIgnoreCase(role)) {
                result.add(user);
            }
        }
        return result;
    }
    
    private boolean isValidRole(String role) {
        return role.equalsIgnoreCase("ADMIN") || 
               role.equalsIgnoreCase("FACULTY") || 
               role.equalsIgnoreCase("STUDENT") || 
               role.equalsIgnoreCase("REGISTRAR");
    }
    
    // ========== PERSON REGISTRATION ==========
    
    /**
     * Register a new student
     */
    public Student registerStudent(String firstName, String lastName, String email, 
                                   String phoneNumber, String address, String program) {
        // Validation
        validatePersonData(firstName, lastName, email);
        
        // Check for duplicate email
        if (directory.isEmailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Generate university ID
        String universityId = directory.generateUniversityId();
        
        // Create student
        Student student = new Student(universityId, firstName, lastName, email, program);
        student.setPhoneNumber(phoneNumber);
        student.setAddress(address);
        
        // Add to directory
        directory.addStudent(student);
        
        return student;
    }
    
    /**
     * Register a new faculty member
     */
    public Faculty registerFaculty(String firstName, String lastName, String email, 
                                   String phoneNumber, Department department, 
                                   String officeLocation, String officeHours) {
        // Validation
        validatePersonData(firstName, lastName, email);
        
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null");
        }
        
        // Check for duplicate email
        if (directory.isEmailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Generate university ID
        String universityId = directory.generateUniversityId();
        
        // Create faculty
        Faculty faculty = new Faculty(universityId, firstName, lastName, email, department);
        faculty.setPhoneNumber(phoneNumber);
        faculty.setOfficeLocation(officeLocation);
        faculty.setOfficeHours(officeHours);
        
        // Add to directory and department
        directory.addFaculty(faculty);
        department.addFaculty(faculty);
        
        return faculty;
    }
    
    /**
     * Register a new registrar
     */
    public Registrar registerRegistrar(String firstName, String lastName, String email, 
                                       String phoneNumber, String officeLocation, 
                                       String officeHours) {
        // Validation
        validatePersonData(firstName, lastName, email);
        
        // Check for duplicate email
        if (directory.isEmailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Generate university ID
        String universityId = directory.generateUniversityId();
        
        // Create registrar
        Registrar registrar = new Registrar(universityId, firstName, lastName, email);
        registrar.setPhoneNumber(phoneNumber);
        registrar.setOfficeLocation(officeLocation);
        registrar.setOfficeHours(officeHours);
        
        // Add to directory
        directory.addRegistrar(registrar);
        
        return registrar;
    }
    
    /**
     * Register a new admin
     */
    public Admin registerAdmin(String firstName, String lastName, String email, 
                               String phoneNumber, String adminLevel) {
        // Validation
        validatePersonData(firstName, lastName, email);
        
        // Check for duplicate email
        if (directory.isEmailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Generate university ID
        String universityId = directory.generateUniversityId();
        
        // Create admin
        Admin admin = new Admin(universityId, firstName, lastName, email, adminLevel);
        admin.setPhoneNumber(phoneNumber);
        
        // Add to directory
        directory.addAdmin(admin);
        
        return admin;
    }
    
    private void validatePersonData(String firstName, String lastName, String email) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
    
    // ========== STUDENT RECORDS MANAGEMENT ==========
    
    /**
     * Update student information
     */
    public boolean updateStudentInfo(Student student, String email, String phoneNumber, 
                                    String address, String program) {
        if (student == null) {
            return false;
        }
        
        // Check if new email already exists (for different student)
        if (email != null && !email.equals(student.getEmail())) {
            Person existingPerson = directory.findPersonByEmail(email);
            if (existingPerson != null && !existingPerson.equals(student)) {
                throw new IllegalArgumentException("Email already exists");
            }
            student.setEmail(email);
        }
        
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            student.setPhoneNumber(phoneNumber);
        }
        if (address != null && !address.trim().isEmpty()) {
            student.setAddress(address);
        }
        if (program != null && !program.trim().isEmpty()) {
            student.setProgram(program);
        }
        
        return true;
    }
    
    /**
     * Delete student record
     */
    public boolean deleteStudent(Student student) {
        if (student == null) {
            return false;
        }
        
        // Remove all enrollments
        ArrayList<Enrollment> enrollments = new ArrayList<>(student.getEnrollments());
        for (Enrollment e : enrollments) {
            e.getCourseOffering().removeEnrollment(e);
            directory.removeEnrollment(e);
        }
        
        // Remove from directory
        directory.removeStudent(student);
        
        // Delete user account
        User user = findUserByPerson(student);
        if (user != null) {
            authService.deleteUser(user.getUsername());
        }
        
        return true;
    }
    
    // ========== FACULTY RECORDS MANAGEMENT ==========
    
    /**
     * Update faculty information
     */
    public boolean updateFacultyInfo(Faculty faculty, String email, String phoneNumber, 
                                    Department department, String officeLocation, 
                                    String officeHours) {
        if (faculty == null) {
            return false;
        }
        
        // Check if new email already exists
        if (email != null && !email.equals(faculty.getEmail())) {
            Person existingPerson = directory.findPersonByEmail(email);
            if (existingPerson != null && !existingPerson.equals(faculty)) {
                throw new IllegalArgumentException("Email already exists");
            }
            faculty.setEmail(email);
        }
        
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            faculty.setPhoneNumber(phoneNumber);
        }
        if (department != null && !department.equals(faculty.getDepartment())) {
            // Remove from old department
            if (faculty.getDepartment() != null) {
                faculty.getDepartment().removeFaculty(faculty);
            }
            // Add to new department
            faculty.setDepartment(department);
            department.addFaculty(faculty);
        }
        if (officeLocation != null && !officeLocation.trim().isEmpty()) {
            faculty.setOfficeLocation(officeLocation);
        }
        if (officeHours != null && !officeHours.trim().isEmpty()) {
            faculty.setOfficeHours(officeHours);
        }
        
        return true;
    }
    
    /**
     * Assign faculty to course offering
     */
    public boolean assignFacultyToCourse(Faculty faculty, CourseOffering courseOffering) {
        if (faculty == null || courseOffering == null) {
            return false;
        }
        
        // Remove old instructor
        Faculty oldInstructor = courseOffering.getInstructor();
        if (oldInstructor != null) {
            oldInstructor.removeCourse(courseOffering);
        }
        
        // Assign new instructor
        courseOffering.setInstructor(faculty);
        faculty.addCourse(courseOffering);
        
        return true;
    }
    
    /**
     * Delete faculty record
     */
    public boolean deleteFaculty(Faculty faculty) {
        if (faculty == null) {
            return false;
        }
        
        // Remove from all course offerings
        ArrayList<CourseOffering> courses = new ArrayList<>(faculty.getAssignedCourses());
        for (CourseOffering co : courses) {
            co.setInstructor(null);
            faculty.removeCourse(co);
        }
        
        // Remove from department
        if (faculty.getDepartment() != null) {
            faculty.getDepartment().removeFaculty(faculty);
        }
        
        // Remove from directory
        directory.removeFaculty(faculty);
        
        // Delete user account
        User user = findUserByPerson(faculty);
        if (user != null) {
            authService.deleteUser(user.getUsername());
        }
        
        return true;
    }
    
    // ========== REGISTRAR RECORDS MANAGEMENT ==========
    
    /**
     * Update registrar information
     */
    public boolean updateRegistrarInfo(Registrar registrar, String email, String phoneNumber,
                                      String officeLocation, String officeHours) {
        if (registrar == null) {
            return false;
        }
        
        if (email != null && !email.equals(registrar.getEmail())) {
            Person existingPerson = directory.findPersonByEmail(email);
            if (existingPerson != null && !existingPerson.equals(registrar)) {
                throw new IllegalArgumentException("Email already exists");
            }
            registrar.setEmail(email);
        }
        
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            registrar.setPhoneNumber(phoneNumber);
        }
        if (officeLocation != null && !officeLocation.trim().isEmpty()) {
            registrar.setOfficeLocation(officeLocation);
        }
        if (officeHours != null && !officeHours.trim().isEmpty()) {
            registrar.setOfficeHours(officeHours);
        }
        
        return true;
    }
    
    /**
     * Delete registrar record
     */
    public boolean deleteRegistrar(Registrar registrar) {
        if (registrar == null) {
            return false;
        }
        
        directory.getRegistrars().remove(registrar);
        directory.removePerson(registrar);
        
        // Delete user account
        User user = findUserByPerson(registrar);
        if (user != null) {
            authService.deleteUser(user.getUsername());
        }
        
        return true;
    }
    
    // ========== HELPER METHODS ==========
    
    /**
     * Find user by person object
     */
    private User findUserByPerson(Person person) {
        for (User user : authService.getAllUsers().values()) {
            if (user.getPerson().equals(person)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Get total active users by role
     */
    public int getTotalUsersByRole(String role) {
        int count = 0;
        for (User user : authService.getAllUsers().values()) {
            if (user.getRole().equalsIgnoreCase(role) && user.isActive()) {
                count++;
            }
        }
        return count;
    }
}