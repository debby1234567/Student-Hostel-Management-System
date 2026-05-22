/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author CTRL-SHIFT Ltd
 */

@Entity
@Table(name = "students")
public class Student implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    public Student() {}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "student_id", nullable = false, unique = true, length = 20)
    private String studentId;           
 
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
 
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
 
    @Column(nullable = false, unique = true, length = 100)
    private String email;
 
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;
 
    @Column(length = 10)
    private String gender;              
 
    @Column(name = "national_id", unique = true, length = 20)
    private String nationalId;
 
    @Column(nullable = false)
    private String password;            
 
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Booking> bookings = new ArrayList<>();
    
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Staff staffRecord;
    
    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;


    public Student(Long id, String studentId, String firstName, String lastName, String email, String phoneNumber, String gender, String nationalId, String emergencyContact, String password, Staff staffRecord) {
        this.id = id;
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.nationalId = nationalId;
        this.password = password;
        this.staffRecord = staffRecord;
    }

    public Long getId() {
        return id;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public Student(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public Staff getStaffRecord() {
        return staffRecord;
    }

    public void setStaffRecord(Staff staffRecord) {
        this.staffRecord = staffRecord;
    }

  public boolean isActive() { return isActive; }
public void setActive(boolean b) { this.isActive = b; }
    
    
}
