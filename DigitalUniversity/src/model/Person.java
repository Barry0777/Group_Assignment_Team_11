package model;

/**
 * Base class representing a person in the university system
 * Author: [Your Name]
 */
public class Person {
    private String universityId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    
    // Constructor
    public Person(String universityId, String firstName, String lastName, String email) {
        this.universityId = universityId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Getters and Setters
    public String getUniversityId() {
        return universityId;
    }
    
    public void setUniversityId(String universityId) {
        this.universityId = universityId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return getFullName() + " (" + universityId + ")";
    }
}