package business;

import model.*;
import java.util.ArrayList;

/**
 * SearchService - Centralized search functionality for all entities
 * Author: [Your Name]
 */
public class SearchService {
    
    private UniversityDirectory directory;
    
    public SearchService() {
        this.directory = UniversityDirectory.getInstance();
    }
    
    // ========== STUDENT SEARCH ==========
    
    /**
     * Search students by name (first or last name)
     */
    public ArrayList<Student> searchStudentsByName(String name) {
        ArrayList<Student> results = new ArrayList<>();
        
        if (name == null || name.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = name.toLowerCase().trim();
        
        for (Student student : directory.getStudents()) {
            String fullName = student.getFullName().toLowerCase();
            String firstName = student.getFirstName().toLowerCase();
            String lastName = student.getLastName().toLowerCase();
            
            if (fullName.contains(searchTerm) || 
                firstName.contains(searchTerm) || 
                lastName.contains(searchTerm)) {
                results.add(student);
            }
        }
        
        return results;
    }
    
    /**
     * Search students by university ID
     */
    public Student searchStudentById(String universityId) {
        if (universityId == null || universityId.trim().isEmpty()) {
            return null;
        }
        
        return directory.findStudentByUniversityId(universityId.trim());
    }
    
    /**
     * Search students by department/program
     */
    public ArrayList<Student> searchStudentsByProgram(String program) {
        ArrayList<Student> results = new ArrayList<>();
        
        if (program == null || program.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = program.toLowerCase().trim();
        
        for (Student student : directory.getStudents()) {
            if (student.getProgram().toLowerCase().contains(searchTerm)) {
                results.add(student);
            }
        }
        
        return results;
    }
    
    /**
     * Search students by email
     */
    public Student searchStudentByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        
        Person person = directory.findPersonByEmail(email.trim());
        
        if (person instanceof Student) {
            return (Student) person;
        }
        
        return null;
    }
    
    // ========== FACULTY SEARCH ==========
    
    /**
     * Search faculty by name
     */
    public ArrayList<Faculty> searchFacultyByName(String name) {
        ArrayList<Faculty> results = new ArrayList<>();
        
        if (name == null || name.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = name.toLowerCase().trim();
        
        for (Faculty faculty : directory.getFaculties()) {
            String fullName = faculty.getFullName().toLowerCase();
            String firstName = faculty.getFirstName().toLowerCase();
            String lastName = faculty.getLastName().toLowerCase();
            
            if (fullName.contains(searchTerm) || 
                firstName.contains(searchTerm) || 
                lastName.contains(searchTerm)) {
                results.add(faculty);
            }
        }
        
        return results;
    }
    
    /**
     * Search faculty by university ID
     */
    public Faculty searchFacultyById(String universityId) {
        if (universityId == null || universityId.trim().isEmpty()) {
            return null;
        }
        
        return directory.findFacultyByUniversityId(universityId.trim());
    }
    
    /**
     * Search faculty by department
     */
    public ArrayList<Faculty> searchFacultyByDepartment(Department department) {
        ArrayList<Faculty> results = new ArrayList<>();
        
        if (department == null) {
            return results;
        }
        
        return new ArrayList<>(department.getFacultyMembers());
    }
    
    /**
     * Search faculty by department name
     */
    public ArrayList<Faculty> searchFacultyByDepartmentName(String departmentName) {
        ArrayList<Faculty> results = new ArrayList<>();
        
        if (departmentName == null || departmentName.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = departmentName.toLowerCase().trim();
        
        for (Faculty faculty : directory.getFaculties()) {
            if (faculty.getDepartment() != null && 
                faculty.getDepartment().getName().toLowerCase().contains(searchTerm)) {
                results.add(faculty);
            }
        }
        
        return results;
    }
    
    // ========== PERSON SEARCH (GENERAL) ==========
    
    /**
     * Search any person by name
     */
    public ArrayList<Person> searchPersonsByName(String name) {
        ArrayList<Person> results = new ArrayList<>();
        
        if (name == null || name.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = name.toLowerCase().trim();
        
        for (Person person : directory.getPersons()) {
            String fullName = person.getFullName().toLowerCase();
            
            if (fullName.contains(searchTerm)) {
                results.add(person);
            }
        }
        
        return results;
    }
    
    /**
     * Search person by university ID
     */
    public Person searchPersonById(String universityId) {
        if (universityId == null || universityId.trim().isEmpty()) {
            return null;
        }
        
        return directory.findPersonByUniversityId(universityId.trim());
    }
    
    /**
     * Search person by email
     */
    public Person searchPersonByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        
        return directory.findPersonByEmail(email.trim());
    }
    
    // ========== COURSE SEARCH ==========
    
    /**
     * Search courses by course ID
     */
    public ArrayList<Course> searchCourseById(String courseId) {
        ArrayList<Course> results = new ArrayList<>();
        
        if (courseId == null || courseId.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = courseId.toLowerCase().trim();
        
        for (Course course : directory.getCourses()) {
            if (course.getCourseId().toLowerCase().contains(searchTerm)) {
                results.add(course);
            }
        }
        
        return results;
    }
    
    /**
     * Search courses by title
     */
    public ArrayList<Course> searchCourseByTitle(String title) {
        ArrayList<Course> results = new ArrayList<>();
        
        if (title == null || title.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = title.toLowerCase().trim();
        
        for (Course course : directory.getCourses()) {
            if (course.getTitle().toLowerCase().contains(searchTerm)) {
                results.add(course);
            }
        }
        
        return results;
    }
    
    /**
     * Search courses by department
     */
    public ArrayList<Course> searchCourseByDepartment(Department department) {
        ArrayList<Course> results = new ArrayList<>();
        
        if (department == null) {
            return results;
        }
        
        for (Course course : directory.getCourses()) {
            if (course.getDepartment().equals(department)) {
                results.add(course);
            }
        }
        
        return results;
    }
    
    // ========== COURSE OFFERING SEARCH ==========
    
    /**
     * Search course offerings by course ID
     */
    public ArrayList<CourseOffering> searchOfferingsByCourseId(String courseId) {
        ArrayList<CourseOffering> results = new ArrayList<>();
        
        if (courseId == null || courseId.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = courseId.toLowerCase().trim();
        
        for (CourseOffering offering : directory.getCourseOfferings()) {
            if (offering.getCourse().getCourseId().toLowerCase().contains(searchTerm)) {
                results.add(offering);
            }
        }
        
        return results;
    }
    
    /**
     * Search course offerings by instructor name
     */
    public ArrayList<CourseOffering> searchOfferingsByInstructor(String instructorName) {
        ArrayList<CourseOffering> results = new ArrayList<>();
        
        if (instructorName == null || instructorName.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = instructorName.toLowerCase().trim();
        
        for (CourseOffering offering : directory.getCourseOfferings()) {
            if (offering.getInstructor() != null) {
                String fullName = offering.getInstructor().getFullName().toLowerCase();
                if (fullName.contains(searchTerm)) {
                    results.add(offering);
                }
            }
        }
        
        return results;
    }
    
    /**
     * Search course offerings by semester
     */
    public ArrayList<CourseOffering> searchOfferingsBySemester(Semester semester) {
        if (semester == null) {
            return new ArrayList<>();
        }
        
        return directory.getCourseOfferingsBySemester(semester);
    }
    
    /**
     * Search course offerings by course title
     */
    public ArrayList<CourseOffering> searchOfferingsByTitle(String title) {
        ArrayList<CourseOffering> results = new ArrayList<>();
        
        if (title == null || title.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = title.toLowerCase().trim();
        
        for (CourseOffering offering : directory.getCourseOfferings()) {
            if (offering.getCourse().getTitle().toLowerCase().contains(searchTerm)) {
                results.add(offering);
            }
        }
        
        return results;
    }
    
    // ========== DEPARTMENT SEARCH ==========
    
    /**
     * Search departments by name
     */
    public ArrayList<Department> searchDepartmentsByName(String name) {
        ArrayList<Department> results = new ArrayList<>();
        
        if (name == null || name.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = name.toLowerCase().trim();
        
        for (Department dept : directory.getDepartments()) {
            if (dept.getName().toLowerCase().contains(searchTerm)) {
                results.add(dept);
            }
        }
        
        return results;
    }
    
    /**
     * Search department by ID
     */
    public Department searchDepartmentById(String departmentId) {
        if (departmentId == null || departmentId.trim().isEmpty()) {
            return null;
        }
        
        return directory.findDepartmentById(departmentId.trim());
    }
    
    // ========== ADVANCED SEARCH ==========
    
    /**
     * Search students by multiple criteria
     */
    public ArrayList<Student> advancedStudentSearch(String name, String universityId, String program) {
        ArrayList<Student> results = new ArrayList<>(directory.getStudents());
        
        // Filter by name
        if (name != null && !name.trim().isEmpty()) {
            String searchTerm = name.toLowerCase().trim();
            results.removeIf(student -> 
                !student.getFullName().toLowerCase().contains(searchTerm));
        }
        
        // Filter by ID
        if (universityId != null && !universityId.trim().isEmpty()) {
            String searchTerm = universityId.trim();
            results.removeIf(student -> 
                !student.getUniversityId().equalsIgnoreCase(searchTerm));
        }
        
        // Filter by program
        if (program != null && !program.trim().isEmpty()) {
            String searchTerm = program.toLowerCase().trim();
            results.removeIf(student -> 
                !student.getProgram().toLowerCase().contains(searchTerm));
        }
        
        return results;
    }
    
    /**
     * Search faculty by multiple criteria
     */
    public ArrayList<Faculty> advancedFacultySearch(String name, String universityId, String departmentName) {
        ArrayList<Faculty> results = new ArrayList<>(directory.getFaculties());
        
        // Filter by name
        if (name != null && !name.trim().isEmpty()) {
            String searchTerm = name.toLowerCase().trim();
            results.removeIf(faculty -> 
                !faculty.getFullName().toLowerCase().contains(searchTerm));
        }
        
        // Filter by ID
        if (universityId != null && !universityId.trim().isEmpty()) {
            String searchTerm = universityId.trim();
            results.removeIf(faculty -> 
                !faculty.getUniversityId().equalsIgnoreCase(searchTerm));
        }
        
        // Filter by department
        if (departmentName != null && !departmentName.trim().isEmpty()) {
            String searchTerm = departmentName.toLowerCase().trim();
            results.removeIf(faculty -> 
                faculty.getDepartment() == null || 
                !faculty.getDepartment().getName().toLowerCase().contains(searchTerm));
        }
        
        return results;
    }
}