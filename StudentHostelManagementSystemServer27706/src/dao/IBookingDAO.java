/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import model.Booking;
import java.util.List;

/**
 *
 * @author CTRL-SHIFT Ltd
 */
public interface IBookingDAO {
    void save(Booking booking);
    void update(Booking booking);
    void delete(Long id);
    Booking findById(Long id);
    Booking findByBookingCode(String bookingCode);
    List<Booking> findAll();
    List<Booking> findByStudentId(Long studentId);
    List<Booking> findActiveBookings();
    Booking findActiveBookingByStudent(Long studentId);
    boolean hasActiveBooking(Long studentId);
}
