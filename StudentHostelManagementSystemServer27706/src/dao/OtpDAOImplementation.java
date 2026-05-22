/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.HibernateUtil;
import model.Otp;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author CTRL-SHIFT Ltd
 */
public class OtpDAOImplementation implements IOtpDAO {

    
    public void save(Otp otp) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(otp);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error saving OTP: " + e.getMessage(), e);
        }
    }

   
    public void update(Otp otp) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(otp);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error updating OTP: " + e.getMessage(), e);
        }
    }

    
    public Otp findLatestByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Otp> query = session.createQuery(
                "FROM Otp o WHERE o.email = :email AND o.isUsed = false " +
                "ORDER BY o.createdAt DESC", Otp.class);
            query.setParameter("email", email);
            query.setMaxResults(1);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Error finding OTP by email: " + e.getMessage(), e);
        }
    }

    
    public void deleteByEmail(String email) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery("DELETE FROM Otp o WHERE o.email = :email")
                .setParameter("email", email)
                .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error deleting OTP: " + e.getMessage(), e);
        }
    }

    
    public void markAsUsed(Long otpId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery("UPDATE Otp o SET o.isUsed = true WHERE o.id = :id")
                .setParameter("id", otpId)
                .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error marking OTP as used: " + e.getMessage(), e);
        }
    }
}