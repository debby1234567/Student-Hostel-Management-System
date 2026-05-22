/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.HibernateUtil;
import model.Booking;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
 
import java.util.List;
/**
 *
 * @author CTRL-SHIFT Ltd
 */
public class BookingDAOImplementation implements IBookingDAO {
 
    @Override
    public void save(Booking booking) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(booking);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error saving booking: " + e.getMessage(), e);
        }
    }
 
    @Override
    public void update(Booking booking) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(booking);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error updating booking: " + e.getMessage(), e);
        }
    }
 
    @Override
    public void delete(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Booking booking = session.get(Booking.class, id);
            if (booking != null) {
                booking.setStatus("Cancelled");
                session.update(booking);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error cancelling booking: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Booking findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Booking.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error finding booking by id: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Booking findByBookingCode(String bookingCode) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Booking> query = session.createQuery(
                "FROM Booking b WHERE b.bookingCode = :code", Booking.class);
            query.setParameter("code", bookingCode);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Error finding booking by code: " + e.getMessage(), e);
        }
    }
 
    @Override
    public List<Booking> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Booking b ORDER BY b.bookingDate DESC", Booking.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all bookings: " + e.getMessage(), e);
        }
    }
 
    @Override
    public List<Booking> findByStudentId(Long studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Booking> query = session.createQuery(
                "FROM Booking b WHERE b.student.id = :sid ORDER BY b.bookingDate DESC", Booking.class);
            query.setParameter("sid", studentId);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding bookings by student: " + e.getMessage(), e);
        }
    }
 
    @Override
    public List<Booking> findActiveBookings() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Booking b WHERE b.status = 'Active' ORDER BY b.checkInDate", Booking.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching active bookings: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Booking findActiveBookingByStudent(Long studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Booking> query = session.createQuery(
                "FROM Booking b WHERE b.student.id = :sid AND b.status = 'Active'", Booking.class);
            query.setParameter("sid", studentId);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Error finding active booking: " + e.getMessage(), e);
        }
    }
 
    @Override
    public boolean hasActiveBooking(Long studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                "SELECT COUNT(b) FROM Booking b WHERE b.student.id = :sid AND b.status = 'Active'", Long.class)
                .setParameter("sid", studentId)
                .uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error checking active booking: " + e.getMessage(), e);
        }
    }
}
