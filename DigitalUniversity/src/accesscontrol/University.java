package accesscontrol;

import business.DepartmentDirectory;







public class University {


    private DepartmentDirectory departmentDirectory;

    public University() {

        this.departmentDirectory = new DepartmentDirectory(this);
    }


    public DepartmentDirectory getDepartmentDirectory() {
        return departmentDirectory;
    }
}
