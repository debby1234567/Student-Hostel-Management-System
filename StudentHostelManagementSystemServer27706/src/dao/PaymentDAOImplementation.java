/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.HibernateUtil;
import model.Payment;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
 
import java.util.List;

/**
 *
 * @author CTRL-SHIFT Ltd
 */
public class PaymentDAOImplementation implements IPaymentDAO {
 
    @Override
    public void save(Payment payment) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(payment);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error saving payment: " + e.getMessage(), e);
        }
    }
 
    @Override
    public void update(Payment payment) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(payment);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error updating payment: " + e.getMessage(), e);
        }
    }
 
    @Override
    public void delete(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Payment payment = session.get(Payment.class, id);
            if (payment != null) session.delete(payment);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error deleting payment: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Payment findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Payment.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error finding payment by id: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Payment findByReference(String paymentReference) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Payment> query = session.createQuery(
                "FROM Payment p WHERE p.paymentReference = :ref", Payment.class);
            query.setParameter("ref", paymentReference);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Error finding payment by reference: " + e.getMessage(), e);
        }
    }
 
    @Override
    public List<Payment> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Payment p ORDER BY p.paymentDate DESC", Payment.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all payments: " + e.getMessage(), e);
        }
    }
 
    @Override
    public List<Payment> findByBookingId(Long bookingId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Payment> query = session.createQuery(
                "FROM Payment p WHERE p.booking.id = :bid ORDER BY p.paymentDate DESC", Payment.class);
            query.setParameter("bid", bookingId);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding payments by booking: " + e.getMessage(), e);
        }
    }
 
    @Override
    public List<Payment> findByStudentId(Long studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Payment> query = session.createQuery(
                "FROM Payment p WHERE p.booking.student.id = :sid ORDER BY p.paymentDate DESC", Payment.class);
            query.setParameter("sid", studentId);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding payments by student: " + e.getMessage(), e);
        }
    }
 
    @Override
    public double getTotalPaidByBooking(Long bookingId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Double total = session.createQuery(
                "SELECT SUM(p.amount) FROM Payment p WHERE p.booking.id = :bid AND p.status = 'Paid'", Double.class)
                .setParameter("bid", bookingId)
                .uniqueResult();
            return total != null ? total : 0.0;
        } catch (Exception e) {
            throw new RuntimeException("Error calculating total paid: " + e.getMessage(), e);
        }
    }
}
