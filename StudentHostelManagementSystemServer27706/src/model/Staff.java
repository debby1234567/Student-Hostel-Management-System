/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
/**
 *
 * @author CTRL-SHIFT Ltd
 */
@Entity
@Table(name = "staff")
public class Staff implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public Staff() {}
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "staff_code", nullable = false, unique = true, length = 20)
    private String staffCode;           
 
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
 
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
 
    @Column(nullable = false, unique = true, length = 100)
    private String email;
 
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;
 
    @Column(nullable = false, length = 30)
    private String role;                
 
    @Column(nullable = false)
    private String password;
 
    @Column(name = "hire_date")
    private LocalDate hireDate;
 
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
 
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", unique = true)
    private Student student;

    public Staff(Long id, String staffCode, String firstName, String lastName, String email, String phoneNumber, String role, String password, LocalDate hireDate, Student student) {
        this.id = id;
        this.staffCode = staffCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.password = password;
        this.hireDate = hireDate;
        this.student = student;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

public boolean isActive() { 
return isActive; 
}
public void setActive(boolean b){
this.isActive = b;
}
    

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

   
 
    
}
