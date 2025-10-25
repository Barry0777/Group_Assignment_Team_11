package accesscontrol;

import model.*;

/**
 * User class for authentication and authorization
 * Author: [Your Name]
 */
public class User {
    private String username;
    private String password;
    private String role; // "ADMIN", "FACULTY", "STUDENT", "REGISTRAR"
    private Person person; // Link to the actual person object
    private boolean isActive;
    
    // Constructor
    public User(String username, String password, String role, Person person) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.person = person;
        this.isActive = true;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Person getPerson() {
        return person;
    }
    
    public void setPerson(Person person) {
        this.person = person;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    /**
     * Validate password
     */
    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
    
    /**
     * Check if user has a specific role
     */
    public boolean hasRole(String role) {
        return this.role.equalsIgnoreCase(role);
    }
    
    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}