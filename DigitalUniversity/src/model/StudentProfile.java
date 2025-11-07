/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


import java.util.ArrayList;

/**
 *
 * @author kal bugrara
 */
public class StudentProfile extends Profile {

    Person person;
    Transcript transcript;
    EmploymentHistroy employmenthistory;

    public StudentProfile(Person p) {
        super(p); 
        person = p;
        transcript = new Transcript(this);
        employmenthistory = new EmploymentHistroy();
    }

    public boolean isMatch(String id) {
        return person.getPersonId().equals(id);
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public CourseLoad getCourseLoadBySemester(String semester) {

        return transcript.getCourseLoadBySemester(semester);
    }

    public CourseLoad getCurrentCourseLoad() {

        return transcript.getCurrentCourseLoad();
    }

    public CourseLoad newCourseLoad(String s) {

        return transcript.newCourseLoad(s);
    }

    public ArrayList<SeatAssignment> getCourseList() {

        return transcript.getCourseList();

    }
    @Override
    public String getRole(){
        return "Student";
    }
    public String toString() {
       
        return this.getPerson().toString(); 
    }
}
