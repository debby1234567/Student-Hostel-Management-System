/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service.Implementation;

import dao.*;
import model.*;
import service.HostelService;
 
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import service.OtpService;

/**
 *
 * @author CTRL-SHIFT Ltd
 */
public class HostelServiceImplementation extends UnicastRemoteObject implements HostelService {
 
    // ── DAOs ──────────────────────────────────────────────────────────────
    private final IStudentDAO studentDAO;
    private final IRoomDAO    roomDAO;
    private final IBookingDAO bookingDAO;
    private final IPaymentDAO paymentDAO;
    private final IStaffDAO   staffDAO;
 
    // ── OTP service ───────────────────────────────────────────────────────
    private final OtpService otpService = new OtpService();
 
    public HostelServiceImplementation() throws RemoteException {
        super();
        this.studentDAO = new StudentDAOImplementation();
        this.roomDAO    = new RoomDAOImplementation();
        this.bookingDAO = new BookingDAOImplementation();
        this.paymentDAO = new PaymentDAOImplementation();
        this.staffDAO   = new StaffDAOImplementation();
    }
 
    // ── Authentication ────────────────────────────────────────────────────
 
    @Override
    public Staff loginStaff(String email, String password) throws RemoteException {
        try {
            Staff staff = staffDAO.findByEmail(email);
            if (staff == null) return null;
            if (staff.getPassword().equals(password) && staff.isActive()) return staff;
            return null;
        } catch (Exception e) {
            throw new RemoteException("Staff login failed: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Student loginStudent(String studentId, String password) throws RemoteException {
        try {
            Student student = studentDAO.findByStudentId(studentId);
            if (student == null) return null;
            if (student.getPassword().equals(password) && student.isActive()) return student;
            return null;
        } catch (Exception e) {
            throw new RemoteException("Student login failed: " + e.getMessage(), e);
        }
    }
 
    // ── OTP ───────────────────────────────────────────────────────────────
 
    @Override
    public void sendOtp(String email, String userType) throws RemoteException {
        try {
            otpService.generateAndSendOtp(email, userType);
        } catch (Exception e) {
            throw new RemoteException("Failed to send OTP: " + e.getMessage(), e);
        }
    }
 
    @Override
    public boolean verifyOtp(String email, String otpCode) throws RemoteException {
        try {
            return otpService.verifyOtp(email, otpCode);
        } catch (Exception e) {
            throw new RemoteException("OTP verification error: " + e.getMessage(), e);
        }
    }
 
    // ── Students ──────────────────────────────────────────────────────────
 
    @Override
    public boolean addStudent(Student student) throws RemoteException {
        try {
            prepareStudentForPersist(student, true);
            if (studentDAO.existsByEmail(student.getEmail()))
                throw new RemoteException("A student with this email already exists.");
            if (studentDAO.existsByStudentId(student.getStudentId()))
                throw new RemoteException("Student ID already registered.");
            String nationalId = student.getNationalId();
            if (nationalId != null && studentDAO.existsByNationalId(nationalId))
                throw new RemoteException("National ID already registered.");
            studentDAO.save(student);
            return true;
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error adding student: " + e.getMessage(), e);
        }
    }
 
    @Override
    public boolean updateStudent(Student student) throws RemoteException {
        try {
            Student existing = studentDAO.findById(student.getId());
            if (existing == null)
                throw new RemoteException("Student not found.");
            prepareStudentForPersist(student, false);
            if (student.getPassword() == null || student.getPassword().isEmpty())
                student.setPassword(existing.getPassword());
            studentDAO.update(student);
            return true;
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error updating student: " + e.getMessage(), e);
        }
    }
 
    @Override
    public boolean deleteStudent(Long id) throws RemoteException {
        try {
            if (bookingDAO.hasActiveBooking(id))
                throw new RemoteException("Cannot delete student with an active room booking.");
            studentDAO.delete(id);
            return true;
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error deleting student: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Student getStudentById(Long id) throws RemoteException {
        try { return studentDAO.findById(id); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public Student getStudentByStudentId(String studentId) throws RemoteException {
        try { return studentDAO.findByStudentId(studentId); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public List<Student> getAllStudents() throws RemoteException {
        try { return studentDAO.findAll(); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    // ── Rooms ─────────────────────────────────────────────────────────────
 
    @Override
    public boolean addRoom(Room room) throws RemoteException {
        try {
            if (roomDAO.existsByRoomNumber(room.getRoomNumber()))
                throw new RemoteException("Room number already exists.");
            if (room.getCapacity() <= 0)
                throw new RemoteException("Room capacity must be greater than zero.");
            if (room.getPricePerMonth() <= 0)
                throw new RemoteException("Room price must be greater than zero.");
            roomDAO.save(room);
            return true;
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error adding room: " + e.getMessage(), e);
        }
    }
 
    @Override
    public boolean updateRoom(Room room) throws RemoteException {
        try {
            if (roomDAO.findById(room.getId()) == null)
                throw new RemoteException("Room not found.");
            roomDAO.update(room);
            return true;
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error updating room: " + e.getMessage(), e);
        }
    }
 
    @Override
    public boolean deleteRoom(Long id) throws RemoteException {
        try {
            Room room = roomDAO.findById(id);
            if (room == null) throw new RemoteException("Room not found.");
            if ("Occupied".equals(room.getStatus()))
                throw new RemoteException("Cannot delete an occupied room.");
            roomDAO.delete(id);
            return true;
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error deleting room: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Room getRoomById(Long id) throws RemoteException {
        try { return roomDAO.findById(id); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public List<Room> getAllRooms() throws RemoteException {
        try { return roomDAO.findAll(); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public List<Room> getAvailableRooms() throws RemoteException {
        try { return roomDAO.findAvailableRooms(); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public List<Room> getRoomsByType(String roomType) throws RemoteException {
        try { return roomDAO.findByType(roomType); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    // ── Bookings ──────────────────────────────────────────────────────────
 
    @Override
    public boolean createBooking(Booking booking) throws RemoteException {
        try {
            if (bookingDAO.hasActiveBooking(booking.getStudent().getId()))
                throw new RemoteException("Student already has an active room booking.");
            Room room = roomDAO.findById(booking.getRoom().getId());
            if (!"Available".equals(room.getStatus()))
                throw new RemoteException("Selected room is not available.");
            if (!booking.getCheckOutDate().isAfter(booking.getCheckInDate()))
                throw new RemoteException("Check-out date must be after check-in date.");
            bookingDAO.save(booking);
            roomDAO.updateStatus(room.getId(), "Occupied");
            return true;
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error creating booking: " + e.getMessage(), e);
        }
    }
 
    @Override
    public boolean updateBooking(Booking booking) throws RemoteException {
        try { bookingDAO.update(booking); return true; }
        catch (Exception e) { throw new RemoteException("Error updating booking: " + e.getMessage(), e); }
    }
 
    @Override
    public boolean cancelBooking(Long bookingId) throws RemoteException {
        try {
            Booking booking = bookingDAO.findById(bookingId);
            if (booking == null) throw new RemoteException("Booking not found.");
            if (!"Active".equals(booking.getStatus()))
                throw new RemoteException("Only active bookings can be cancelled.");
            bookingDAO.delete(bookingId);
            roomDAO.updateStatus(booking.getRoom().getId(), "Available");
            return true;
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error cancelling booking: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Booking getBookingById(Long id) throws RemoteException {
        try { return bookingDAO.findById(id); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public Booking getActiveBookingByStudent(Long studentId) throws RemoteException {
        try { return bookingDAO.findActiveBookingByStudent(studentId); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public List<Booking> getAllBookings() throws RemoteException {
        try { return bookingDAO.findAll(); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public List<Booking> getBookingsByStudent(Long studentId) throws RemoteException {
        try { return bookingDAO.findByStudentId(studentId); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    // ── Payments ──────────────────────────────────────────────────────────
 
    @Override
    public boolean recordPayment(Payment payment) throws RemoteException {
        try {
            Booking booking = bookingDAO.findById(payment.getBooking().getId());
            if (booking == null) throw new RemoteException("Booking not found.");
            if (payment.getAmount() <= 0)
                throw new RemoteException("Payment amount must be greater than zero.");
            double alreadyPaid = paymentDAO.getTotalPaidByBooking(booking.getId());
            if (alreadyPaid + payment.getAmount() > booking.getTotalAmount())
                throw new RemoteException("Payment exceeds total booking amount.");
            paymentDAO.save(payment);
            return true;
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error recording payment: " + e.getMessage(), e);
        }
    }
 
    @Override
    public boolean updatePayment(Payment payment) throws RemoteException {
        try { paymentDAO.update(payment); return true; }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public Payment getPaymentById(Long id) throws RemoteException {
        try { return paymentDAO.findById(id); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public List<Payment> getAllPayments() throws RemoteException {
        try { return paymentDAO.findAll(); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public List<Payment> getPaymentsByStudent(Long studentId) throws RemoteException {
        try { return paymentDAO.findByStudentId(studentId); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public List<Payment> getPaymentsByBooking(Long bookingId) throws RemoteException {
        try { return paymentDAO.findByBookingId(bookingId); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public double getTotalPaidByBooking(Long bookingId) throws RemoteException {
        try { return paymentDAO.getTotalPaidByBooking(bookingId); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    // ── Staff ─────────────────────────────────────────────────────────────
 
    @Override
    public boolean addStaff(Staff staff) throws RemoteException {
        try {
            if (staffDAO.existsByEmail(staff.getEmail()))
                throw new RemoteException("Staff with this email already exists.");
            staffDAO.save(staff);
            return true;
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error adding staff: " + e.getMessage(), e);
        }
    }
 
    @Override
    public boolean updateStaff(Staff staff) throws RemoteException {
        try { staffDAO.update(staff); return true; }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public boolean deleteStaff(Long id) throws RemoteException {
        try { staffDAO.delete(id); return true; }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public Staff getStaffById(Long id) throws RemoteException {
        try { return staffDAO.findById(id); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public List<Staff> getAllStaff() throws RemoteException {
        try { return staffDAO.findAll(); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public List<Staff> getStaffByRole(String role) throws RemoteException {
        try { return staffDAO.findByRole(role); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    // ── Reports ───────────────────────────────────────────────────────────
 
    @Override
    public List<Booking> getBookingReport() throws RemoteException {
        try { return bookingDAO.findAll(); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public List<Payment> getPaymentReport() throws RemoteException {
        try { return paymentDAO.findAll(); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }
 
    @Override
    public List<Student> getStudentReport() throws RemoteException {
        try { return studentDAO.findAll(); }
        catch (Exception e) { throw new RemoteException(e.getMessage(), e); }
    }

    private static void prepareStudentForPersist(Student student, boolean isNew) throws RemoteException {
        if (student.getEmail() != null)
            student.setEmail(student.getEmail().trim());
        if (student.getStudentId() != null)
            student.setStudentId(student.getStudentId().trim());
        if (student.getFirstName() != null)
            student.setFirstName(student.getFirstName().trim());
        if (student.getLastName() != null)
            student.setLastName(student.getLastName().trim());

        String phone = student.getPhoneNumber();
        if (phone != null) {
            phone = phone.trim();
            student.setPhoneNumber(phone.isEmpty() ? null : phone);
        }

        String nid = student.getNationalId();
        if (nid != null) {
            nid = nid.trim();
            student.setNationalId(nid.isEmpty() ? null : nid);
        }

        if (isNew) {
            if (student.getPassword() == null || student.getPassword().trim().isEmpty())
                throw new RemoteException("Password is required.");
            student.setPassword(student.getPassword().trim());
            student.setActive(true);
        }
    }
}