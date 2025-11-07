/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
/**
 *
 * @author Administrator
 */
public class RegistrarProfile extends Profile {
    private String officeHours;
    public RegistrarProfile(Person p) {
        super(p); 
    }
    @Override
    public String getRole(){
        return "Registrar";
    }
    public boolean isMatch(String id) {
        return person.getPersonId().equals(id);
    }
    public String getOfficeHours() {
        return officeHours;
    }

    public void setOfficeHours(String officeHours) {
        this.officeHours = officeHours;
    }
}
