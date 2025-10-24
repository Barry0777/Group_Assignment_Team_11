package model;

/**
 * Admin class representing a university administrator
 * Author: [Your Name]
 */
public class Admin extends Person {
    private String adminLevel; // e.g., "System Administrator", "Department Admin"
    
    // Constructor
    public Admin(String universityId, String firstName, String lastName, String email) {
        super(universityId, firstName, lastName, email);
        this.adminLevel = "System Administrator";
    }
    
    public Admin(String universityId, String firstName, String lastName, String email, String adminLevel) {
        super(universityId, firstName, lastName, email);
        this.adminLevel = adminLevel;
    }
    
    // Getters and Setters
    public String getAdminLevel() {
        return adminLevel;
    }
    
    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }
    
    @Override
    public String toString() {
        return super.toString() + " - " + adminLevel;
    }
}