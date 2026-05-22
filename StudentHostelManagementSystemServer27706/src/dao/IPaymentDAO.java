/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import model.Payment;
import java.util.List;

/**
 *
 * @author CTRL-SHIFT Ltd
 */
public interface IPaymentDAO {
    void save(Payment payment);
    void update(Payment payment);
    void delete(Long id);
    Payment findById(Long id);
    Payment findByReference(String paymentReference);
    List<Payment> findAll();
    List<Payment> findByBookingId(Long bookingId);
    List<Payment> findByStudentId(Long studentId);
    double getTotalPaidByBooking(Long bookingId);
}