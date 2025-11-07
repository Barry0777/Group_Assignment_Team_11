package model;

/**
 * Base class representing a person in the university system
 * Author: [Your Name]
 */
public class Person {

    // ======== Main Fields ========
    private String universityId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;

    // ======== Zhu Fields ========
    private String id;     // legacy ID field
    private String name;   // legacy single-name field
    private String phone;  // legacy phone alias

    // ======== Constructors ========

    /** Full constructor (Colleague’s version) */
    public Person(String universityId, String firstName, String lastName, String email) {
        this.universityId = universityId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /** Zhu constructor (Your version) */
    public Person(String id) {
        this.id = id;
    }

    // ======== Colleague’s Getters/Setters ========

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
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return name != null ? name : "Unknown";
    }

    // ======== Zhu Legacy Methods ========

    public String getPersonId() {
        return id != null ? id : universityId;
    }

    public boolean isMatch(String targetId) {
        if (getPersonId() == null || targetId == null) return false;
        return getPersonId().equals(targetId);
    }

    public String getName() {
        if (name != null) return name;
        return getFullName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        if (phone != null) return phone;
        return phoneNumber;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.phoneNumber = phone;
    }

    // ======== toString ========

    @Override
    public String toString() {
        String idDisplay = getPersonId() != null ? getPersonId() : "N/A";
        return getFullName() + " (" + idDisplay + ")";
    }
}