/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;

import model.*;
 
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
/**
 *
 * @author CTRL-SHIFT Ltd
 */
public interface HostelService extends Remote {
 
    
    

    Staff loginStaff(String email, String password) throws RemoteException;
    Student loginStudent(String studentId, String password) throws RemoteException;
 
    
    boolean addStudent(Student student) throws RemoteException;
    boolean updateStudent(Student student) throws RemoteException;
    boolean deleteStudent(Long id) throws RemoteException;
    Student getStudentById(Long id) throws RemoteException;
    Student getStudentByStudentId(String studentId) throws RemoteException;
    List<Student> getAllStudents() throws RemoteException;
 
    
    boolean addRoom(Room room) throws RemoteException;
    boolean updateRoom(Room room) throws RemoteException;
    boolean deleteRoom(Long id) throws RemoteException;
    Room getRoomById(Long id) throws RemoteException;
    List<Room> getAllRooms() throws RemoteException;
    List<Room> getAvailableRooms() throws RemoteException;
    List<Room> getRoomsByType(String roomType) throws RemoteException;
 
    
    boolean createBooking(Booking booking) throws RemoteException;
    boolean updateBooking(Booking booking) throws RemoteException;
    boolean cancelBooking(Long bookingId) throws RemoteException;
    Booking getBookingById(Long id) throws RemoteException;
    Booking getActiveBookingByStudent(Long studentId) throws RemoteException;
    List<Booking> getAllBookings() throws RemoteException;
    List<Booking> getBookingsByStudent(Long studentId) throws RemoteException;
 
    
    boolean recordPayment(Payment payment) throws RemoteException;
    boolean updatePayment(Payment payment) throws RemoteException;
    Payment getPaymentById(Long id) throws RemoteException;
    List<Payment> getAllPayments() throws RemoteException;
    List<Payment> getPaymentsByStudent(Long studentId) throws RemoteException;
    List<Payment> getPaymentsByBooking(Long bookingId) throws RemoteException;
    double getTotalPaidByBooking(Long bookingId) throws RemoteException;
 
    
    boolean addStaff(Staff staff) throws RemoteException;
    boolean updateStaff(Staff staff) throws RemoteException;
    boolean deleteStaff(Long id) throws RemoteException;
    Staff getStaffById(Long id) throws RemoteException;
    List<Staff> getAllStaff() throws RemoteException;
    List<Staff> getStaffByRole(String role) throws RemoteException;
 
    
    List<Booking> getBookingReport() throws RemoteException;
    List<Payment> getPaymentReport() throws RemoteException;
    List<Student> getStudentReport() throws RemoteException;
    
    void    sendOtp(String email, String userType) throws RemoteException;
boolean verifyOtp(String email, String otpCode) throws RemoteException;
}