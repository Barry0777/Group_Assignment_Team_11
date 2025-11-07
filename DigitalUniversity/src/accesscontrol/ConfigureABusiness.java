package accesscontrol;

import business.EmployeeDirectory;
import business.FacultyDirectory;
import business.PersonDirectory;
import business.StudentDirectory;
import business.UserAccountDirectory;
import model.Course;
import model.CourseOffer;
import model.CourseSchedule;
import model.Department;
import model.EmployeeProfile;
import model.FacultyProfile;
import model.Person;
import model.RegistrarProfile;
import model.StudentProfile;
import model.UserAccount;






public class ConfigureABusiness {

    public static Business initialize() {
        Business business = new Business("Information Systems");

        PersonDirectory persondirectory = business.getPersonDirectory();

        Person person001 = persondirectory.newPerson("John Smith");
        Person person002 = persondirectory.newPerson("Gina Montana");
        Person person003 = persondirectory.newPerson("Adam Rollen");
        Person person005 = persondirectory.newPerson("Jim Dellon");
        Person person006 = persondirectory.newPerson("Anna Shnider");
        Person person007 = persondirectory.newPerson("Laura Brown");
        Person person008 = persondirectory.newPerson("Jack While");
        Person person009 = persondirectory.newPerson("Fidelity");

        EmployeeDirectory employeedirectory = business.getEmployeeDirectory();
        EmployeeProfile employeeprofile0 = employeedirectory.newEmployeeProfile(person001);

        StudentDirectory studentdirectory = business.getStudentDirectory();
        StudentProfile studentprofile0 = studentdirectory.newStudentProfile(person003);

        UserAccountDirectory uadirectory = business.getUserAccountDirectory();
        UserAccount ua3 = uadirectory.newUserAccount(employeeprofile0, "admin", "****");
        UserAccount ua4 = uadirectory.newUserAccount(studentprofile0, "adam", "****");

        Person registrarPerson = person002;
        RegistrarProfile registrarProfile = new RegistrarProfile(registrarPerson);
        UserAccount registrarUA = uadirectory.newUserAccount(registrarProfile, "registrar","1234");

        Department csDept = business.getUniversity().getDepartmentDirectory().newDepartment("Computer Science");
        Department financeDept = business.getUniversity().getDepartmentDirectory().newDepartment("Finance");

        Course info5100 = csDept.newCourse("INFO 5100", "Application Engineering & Development", 4);
        Course info6215 = csDept.newCourse("INFO 6215", "Business Analysis and Information Engineering", 3);
        Course fina7300 = financeDept.newCourse("FINA 7300", "Investment Analysis", 3);
        Course fina6201 = financeDept.newCourse("FINA 6201", "Financial Management", 3);

        Person profPerson1 = persondirectory.newPerson("Khalid Bugrara");
        Person profPerson2 = persondirectory.newPerson("Maria Pitan");
        Person profPerson3 = persondirectory.newPerson("Jared Lee");

        FacultyDirectory facultyDirCS = csDept.getFacultyDirectory();
        FacultyProfile bugraraProfile = facultyDirCS.newFacultyProfile(profPerson1);
        FacultyProfile leeProfile = facultyDirCS.newFacultyProfile(profPerson3);

        FacultyDirectory facultyDirFIN = financeDept.getFacultyDirectory();
        FacultyProfile pitanProfile = facultyDirFIN.newFacultyProfile(profPerson2);

        CourseSchedule fallScheduleCS = csDept.newCourseSchedule("Fall 2024");

        CourseOffer offer5100 = fallScheduleCS.newCourseOffer("INFO 5100");
        offer5100.AssignAsTeacher(bugraraProfile);
        offer5100.generatSeats(10);

        CourseOffer offer6215 = fallScheduleCS.newCourseOffer("INFO 6215");
        offer6215.AssignAsTeacher(leeProfile);
        offer6215.generatSeats(15);

        return business;
    }
}