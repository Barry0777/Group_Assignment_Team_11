package business;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * UniversityDirectory - Central directory for managing all university entities
 * Author: [Your Name]
 */
public class UniversityDirectory {
    private static UniversityDirectory instance;
    
    private ArrayList<Person> persons;
    private ArrayList<Student> students;
    private ArrayList<Faculty> faculties;
    private ArrayList<Admin> admins;
    private ArrayList<Registrar> registrars;
    private ArrayList<Department> departments;
    private ArrayList<Course> courses;
    private ArrayList<Semester> semesters;
    private ArrayList<CourseOffering> courseOfferings;
    private ArrayList<Enrollment> enrollments;
    
    private int nextPersonId;
    private int nextEnrollmentId;
    private int nextOfferingId;
    private int nextAssignmentId;
    private int nextPaymentId;
    
    // Private constructor for singleton pattern
    private UniversityDirectory() {
        this.persons = new ArrayList<>();
        this.students = new ArrayList<>();
        this.faculties = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.registrars = new ArrayList<>();
        this.departments = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.semesters = new ArrayList<>();
        this.courseOfferings = new ArrayList<>();
        this.enrollments = new ArrayList<>();
        
        this.nextPersonId = 1000;
        this.nextEnrollmentId = 1;
        this.nextOfferingId = 1;
        this.nextAssignmentId = 1;
        this.nextPaymentId = 1;
    }
    
    /**
     * Get singleton instance
     */
    public static UniversityDirectory getInstance() {
        if (instance == null) {
            instance = new UniversityDirectory();
        }
        return instance;
    }
    
    // Person Management
    public String generateUniversityId() {
        return "U" + String.format("%06d", nextPersonId++);
    }
    
    public boolean isEmailExists(String email) {
        for (Person p : persons) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isUniversityIdExists(String universityId) {
        for (Person p : persons) {
            if (p.getUniversityId().equals(universityId)) {
                return true;
            }
        }
        return false;
    }
    
    public void addPerson(Person person) {
        if (!persons.contains(person)) {
            persons.add(person);
        }
    }
    
    public void removePerson(Person person) {
        persons.remove(person);
    }
    
    public ArrayList<Person> getPersons() {
        return persons;
    }
    
    public Person findPersonByUniversityId(String universityId) {
        for (Person p : persons) {
            if (p.getUniversityId().equals(universityId)) {
                return p;
            }
        }
        return null;
    }
    
    public Person findPersonByEmail(String email) {
        for (Person p : persons) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                return p;
            }
        }
        return null;
    }
    
    // Student Management
    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
            addPerson(student);
        }
    }
    
    public void removeStudent(Student student) {
        students.remove(student);
        removePerson(student);
    }
    
    public ArrayList<Student> getStudents() {
        return students;
    }
    
    public Student findStudentByUniversityId(String universityId) {
        for (Student s : students) {
            if (s.getUniversityId().equals(universityId)) {
                return s;
            }
        }
        return null;
    }
    
    // Faculty Management
    public void addFaculty(Faculty faculty) {
        if (!faculties.contains(faculty)) {
            faculties.add(faculty);
            addPerson(faculty);
        }
    }
    
    public void removeFaculty(Faculty faculty) {
        faculties.remove(faculty);
        removePerson(faculty);
    }
    
    public ArrayList<Faculty> getFaculties() {
        return faculties;
    }
    
    public Faculty findFacultyByUniversityId(String universityId) {
        for (Faculty f : faculties) {
            if (f.getUniversityId().equals(universityId)) {
                return f;
            }
        }
        return null;
    }
    
    // Admin Management
    public void addAdmin(Admin admin) {
        if (!admins.contains(admin)) {
            admins.add(admin);
            addPerson(admin);
        }
    }
    
    public ArrayList<Admin> getAdmins() {
        return admins;
    }
    
    // Registrar Management
    public void addRegistrar(Registrar registrar) {
        if (!registrars.contains(registrar)) {
            registrars.add(registrar);
            addPerson(registrar);
        }
    }
    
    public ArrayList<Registrar> getRegistrars() {
        return registrars;
    }
    
    // Department Management
    public void addDepartment(Department department) {
        if (!departments.contains(department)) {
            departments.add(department);
        }
    }
    
    public ArrayList<Department> getDepartments() {
        return departments;
    }
    
    public Department findDepartmentById(String departmentId) {
        for (Department d : departments) {
            if (d.getDepartmentId().equals(departmentId)) {
                return d;
            }
        }
        return null;
    }
    
    // Course Management
    public void addCourse(Course course) {
        if (!courses.contains(course)) {
            courses.add(course);
        }
    }
    
    public ArrayList<Course> getCourses() {
        return courses;
    }
    
    public Course findCourseById(String courseId) {
        for (Course c : courses) {
            if (c.getCourseId().equalsIgnoreCase(courseId)) {
                return c;
            }
        }
        return null;
    }
    
    // Semester Management
    public void addSemester(Semester semester) {
        if (!semesters.contains(semester)) {
            semesters.add(semester);
        }
    }
    
    public ArrayList<Semester> getSemesters() {
        return semesters;
    }
    
    public Semester findSemesterById(String semesterId) {
        for (Semester s : semesters) {
            if (s.getSemesterId().equals(semesterId)) {
                return s;
            }
        }
        return null;
    }
    
    // Course Offering Management
    public String generateOfferingId() {
        return "OFF" + String.format("%05d", nextOfferingId++);
    }
    
    public void addCourseOffering(CourseOffering offering) {
        if (!courseOfferings.contains(offering)) {
            courseOfferings.add(offering);
        }
    }
    
    public ArrayList<CourseOffering> getCourseOfferings() {
        return courseOfferings;
    }
    
    public CourseOffering findCourseOfferingById(String offeringId) {
        for (CourseOffering co : courseOfferings) {
            if (co.getOfferingId().equals(offeringId)) {
                return co;
            }
        }
        return null;
    }
    
    public ArrayList<CourseOffering> getCourseOfferingsBySemester(Semester semester) {
        ArrayList<CourseOffering> result = new ArrayList<>();
        for (CourseOffering co : courseOfferings) {
            if (co.getSemester().equals(semester)) {
                result.add(co);
            }
        }
        return result;
    }
    
    // Enrollment Management
    public String generateEnrollmentId() {
        return "ENR" + String.format("%06d", nextEnrollmentId++);
    }
    
    public void addEnrollment(Enrollment enrollment) {
        if (!enrollments.contains(enrollment)) {
            enrollments.add(enrollment);
        }
    }
    
    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
    }
    
    public ArrayList<Enrollment> getEnrollments() {
        return enrollments;
    }
    
    public String generateAssignmentId() {
        return "ASN" + String.format("%05d", nextAssignmentId++);
    }
    
    public String generatePaymentId() {
        return "PAY" + String.format("%06d", nextPaymentId++);
    }
}