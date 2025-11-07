package business;


import accesscontrol.University;
import java.util.ArrayList;
import model.Department;


public class DepartmentDirectory {
    
    private University university;

    private ArrayList<Department> departmentList;

    public DepartmentDirectory(University u) {
        this.university = u;
        this.departmentList = new ArrayList<>();
    }

    public Department newDepartment(String name) {
        Department d = new Department(name);
        departmentList.add(d);
        return d;
    }


    public ArrayList<Department> getDepartmentList() {
        return departmentList;
    }
}
