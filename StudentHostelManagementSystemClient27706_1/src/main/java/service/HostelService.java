/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.*;
/**
 *
 * @author CTRL-SHIFT Ltd
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */



/**
 *
 * @author CTRL-SHIFT Ltd
 */
public interface HostelService extends Remote {
 
    // ── Authentication ────────────────────────────────────────────────────
    Staff   loginStaff(String email, String password)       throws RemoteException;
    Student loginStudent(String studentId, String password) throws RemoteException;
 
    // ── OTP ───────────────────────────────────────────────────────────────
    /**
     * Asks the server to generate and email a 6-digit OTP to {@code email}.
     * Call this immediately after credentials are validated successfully.
     *
     * @param email    the authenticated user's email
     * @param userType "STUDENT" or "STAFF"
     */
    void    sendOtp(String email, String userType)  throws RemoteException;
 
    /**
     * Asks the server to validate the code the user typed in the OTP panel.
     *
     * @param email   the authenticated user's email
     * @param otpCode 6-digit code entered by the user
     * @return true if the code is correct and not expired
     */
    boolean verifyOtp(String email, String otpCode) throws RemoteException;
 
    // ── Students ──────────────────────────────────────────────────────────
    boolean         addStudent(Student student)               throws RemoteException;
    boolean         updateStudent(Student student)            throws RemoteException;
    boolean         deleteStudent(Long id)                    throws RemoteException;
    Student         getStudentById(Long id)                   throws RemoteException;
    Student         getStudentByStudentId(String studentId)   throws RemoteException;
    List<Student>   getAllStudents()                           throws RemoteException;
 
    // ── Rooms ─────────────────────────────────────────────────────────────
    boolean      addRoom(Room room)              throws RemoteException;
    boolean      updateRoom(Room room)           throws RemoteException;
    boolean      deleteRoom(Long id)             throws RemoteException;
    Room         getRoomById(Long id)            throws RemoteException;
    List<Room>   getAllRooms()                   throws RemoteException;
    List<Room>   getAvailableRooms()             throws RemoteException;
    List<Room>   getRoomsByType(String roomType) throws RemoteException;
 
    // ── Bookings ──────────────────────────────────────────────────────────
    boolean          createBooking(Booking booking)                throws RemoteException;
    boolean          updateBooking(Booking booking)                throws RemoteException;
    boolean          cancelBooking(Long bookingId)                 throws RemoteException;
    Booking          getBookingById(Long id)                       throws RemoteException;
    Booking          getActiveBookingByStudent(Long studentId)     throws RemoteException;
    List<Booking>    getAllBookings()                               throws RemoteException;
    List<Booking>    getBookingsByStudent(Long studentId)          throws RemoteException;
 
    // ── Payments ──────────────────────────────────────────────────────────
    boolean          recordPayment(Payment payment)                throws RemoteException;
    boolean          updatePayment(Payment payment)                throws RemoteException;
    Payment          getPaymentById(Long id)                       throws RemoteException;
    List<Payment>    getAllPayments()                               throws RemoteException;
    List<Payment>    getPaymentsByStudent(Long studentId)          throws RemoteException;
    List<Payment>    getPaymentsByBooking(Long bookingId)          throws RemoteException;
    double           getTotalPaidByBooking(Long bookingId)         throws RemoteException;
 
    // ── Staff ─────────────────────────────────────────────────────────────
    boolean       addStaff(Staff staff)         throws RemoteException;
    boolean       updateStaff(Staff staff)      throws RemoteException;
    boolean       deleteStaff(Long id)          throws RemoteException;
    Staff         getStaffById(Long id)         throws RemoteException;
    List<Staff>   getAllStaff()                  throws RemoteException;
    List<Staff>   getStaffByRole(String role)   throws RemoteException;
 
    // ── Reports ───────────────────────────────────────────────────────────
    List<Booking>   getBookingReport()  throws RemoteException;
    List<Payment>   getPaymentReport()  throws RemoteException;
    List<Student>   getStudentReport()  throws RemoteException;

    public boolean authenticate(String username, String password);
}