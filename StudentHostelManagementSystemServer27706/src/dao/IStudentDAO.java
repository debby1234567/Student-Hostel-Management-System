/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import model.Student;
import java.util.List;

/**
 *
 * @author CTRL-SHIFT Ltd
 */
public interface IStudentDAO {
    void save(Student student);
    void update(Student student);
    void delete(Long id);
    Student findById(Long id);
    Student findByStudentId(String studentId);
    Student findByEmail(String email);
    List<Student> findAll();
    boolean existsByEmail(String email);
    boolean existsByStudentId(String studentId);
    boolean existsByNationalId(String nationalId);
}
 
