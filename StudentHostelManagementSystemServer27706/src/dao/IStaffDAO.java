/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import model.Staff;
import java.util.List;

/**
 *
 * @author CTRL-SHIFT Ltd
 */
public interface IStaffDAO {
    void save(Staff staff);
    void update(Staff staff);
    void delete(Long id);
    Staff findById(Long id);
    Staff findByStaffCode(String staffCode);
    Staff findByEmail(String email);
    List<Staff> findAll();
    List<Staff> findByRole(String role);
    boolean existsByEmail(String email);
}
