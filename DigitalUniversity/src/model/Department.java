package model;

import java.util.ArrayList;
import java.util.HashMap;


 import business.PersonDirectory;
 import business.StudentDirectory;
 import business.FacultyDirectory;
 import business.EmployerDirectory;

/**
 * Department class representing an academic department
 * Author: [Your Name]
 */
public class Department {

    // ======== Colleague's Fields  ========
    private String departmentId;
    private String name;
    private String location;
    private ArrayList<Faculty> facultyMembers;
    private ArrayList<Student> students;

    // ======== Zhu's Fields  ========
    private CourseCatalog coursecatalog;
    private PersonDirectory persondirectory;
    private StudentDirectory studentdirectory;
    private FacultyDirectory facultydirectory;
    private EmployerDirectory employerdirectory;
    private Degree degree;
    private HashMap<String, CourseSchedule> mastercoursecatalog;

    // ======== Constructors ========

    /** 推荐构造：带部门编号与名称（同事风格 + 你的目录初始化） */
    public Department(String departmentId, String name) {
        // 
        this.departmentId = departmentId;
        this.name = name;
        this.facultyMembers = new ArrayList<>();
        this.students = new ArrayList<>();
        // Zhu
        this.mastercoursecatalog = new HashMap<>();
        this.coursecatalog = new CourseCatalog(this);
        this.studentdirectory = new StudentDirectory(this);
        this.persondirectory = new PersonDirectory();
        this.degree = new Degree("MSIS");
        this.facultydirectory = new FacultyDirectory(this);
        this.employerdirectory = new EmployerDirectory(this);
    }

   
    public Department(String nameOnly, boolean legacyMode) {
        // 
        this.name = nameOnly;
        this.facultyMembers = new ArrayList<>();
        this.students = new ArrayList<>();
        // Zhu
        this.mastercoursecatalog = new HashMap<>();
        this.coursecatalog = new CourseCatalog(this);
        this.studentdirectory = new StudentDirectory(this);
        this.persondirectory = new PersonDirectory();
        this.degree = new Degree("MSIS");
        this.facultydirectory = new FacultyDirectory(this);
        this.employerdirectory = new EmployerDirectory(this);
    }
    public Department(String name) {
    // Use name for both departmentId and name if only one argument is provided
    this.departmentId = name;
    this.name = name;
    this.facultyMembers = new ArrayList<>();
    this.students = new ArrayList<>();

    this.mastercoursecatalog = new HashMap<>();
    this.coursecatalog = new CourseCatalog(this);
    this.studentdirectory = new StudentDirectory(this);
    this.persondirectory = new PersonDirectory();
    this.degree = new Degree("MSIS");
    this.facultydirectory = new FacultyDirectory(this);
    this.employerdirectory = new EmployerDirectory(this);
}
    // ======== Colleague-style Getters/Setters ========

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public ArrayList<Faculty> getFacultyMembers() { return facultyMembers; }
    public void addFaculty(Faculty faculty) {
        if (!facultyMembers.contains(faculty)) facultyMembers.add(faculty);
    }
    public void removeFaculty(Faculty faculty) { facultyMembers.remove(faculty); }

    public ArrayList<Student> getStudents() { return students; }
    public void addStudent(Student student) {
        if (!students.contains(student)) students.add(student);
    }
    public void removeStudent(Student student) { students.remove(student); }

    // ======== Zhu Business Logic (目录/课程/注册等) ========

    public void addCoreCourse(Course c) { degree.addCoreCourse(c); }
    public void addElectiveCourse(Course c) { degree.addElectiveCourse(c); }

    public PersonDirectory getPersonDirectory() { return persondirectory; }
    public StudentDirectory getStudentDirectory() { return studentdirectory; }
    public FacultyDirectory getFacultyDirectory() { return facultydirectory; }
    public EmployerDirectory getEmployerDirectory() { return employerdirectory; }

    public CourseCatalog getCourseCatalog() { return coursecatalog; }

    public Course newCourse(String n, String nm, int cr) {
        return coursecatalog.newCourse(n, nm, cr);
    }

    public CourseSchedule newCourseSchedule(String semester) {
        CourseSchedule cs = new CourseSchedule(semester, coursecatalog);
        mastercoursecatalog.put(semester, cs);
        return cs;
    }

    public CourseSchedule getCourseSchedule(String semester) {
        return mastercoursecatalog.get(semester);
    }

    public HashMap<String, CourseSchedule> getMasterCourseCatalog() {
        return mastercoursecatalog;
    }

    public int calculateRevenuesBySemester(String semester) {
        CourseSchedule css = mastercoursecatalog.get(semester);
        return css.calculateTotalRevenues();
    }

    public void RegisterForAClass(String studentid, String cn, String semester) {
        StudentProfile sp = studentdirectory.findStudent(studentid);
        CourseLoad cl = sp.getCurrentCourseLoad();
        CourseSchedule cs = mastercoursecatalog.get(semester);
        CourseOffer co = cs.getCourseOfferByNumber(cn);
        co.assignEmptySeat(cl);
    }

    // ======== Utility ========

    @Override
    public String toString() {
        return (name != null ? name : "Department")
                + " (" + (departmentId != null ? departmentId : "No ID") + ")";
    }
}