package model;

/**
 * Registrar class representing a registrar office staff member
 * Author: [Your Name]
 */
public class Registrar extends Person {
    private String officeLocation;
    private String officeHours;
    private String department;
    
    // Constructor
    public Registrar(String universityId, String firstName, String lastName, String email) {
        super(universityId, firstName, lastName, email);
        this.department = "Registrar Office";
    }
    
    // Getters and Setters
    public String getOfficeLocation() {
        return officeLocation;
    }
    
    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }
    
    public String getOfficeHours() {
        return officeHours;
    }
    
    public void setOfficeHours(String officeHours) {
        this.officeHours = officeHours;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    @Override
    public String toString() {
        return super.toString() + " - " + department;
    }
}