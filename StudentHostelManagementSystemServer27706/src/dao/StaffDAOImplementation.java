/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.HibernateUtil;
import model.Staff;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
 
import java.util.List;
/**
 *
 * @author CTRL-SHIFT Ltd
 */
public class StaffDAOImplementation implements IStaffDAO {
 
    @Override
    public void save(Staff staff) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(staff);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error saving staff: " + e.getMessage(), e);
        }
    }
 
    @Override
    public void update(Staff staff) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(staff);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error updating staff: " + e.getMessage(), e);
        }
    }
 
    @Override
    public void delete(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Staff staff = session.get(Staff.class, id);
            if (staff != null) {
                staff.setActive(false); 
                session.update(staff);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error deleting staff: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Staff findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Staff.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error finding staff by id: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Staff findByStaffCode(String staffCode) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Staff> query = session.createQuery(
                "FROM Staff s WHERE s.staffCode = :code AND s.isActive = true", Staff.class);
            query.setParameter("code", staffCode);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Error finding staff by code: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Staff findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Staff> query = session.createQuery(
                "FROM Staff s WHERE s.email = :email AND s.isActive = true", Staff.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Error finding staff by email: " + e.getMessage(), e);
        }
    }
 
    @Override
    public List<Staff> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Staff s WHERE s.isActive = true ORDER BY s.lastName", Staff.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all staff: " + e.getMessage(), e);
        }
    }
 
    @Override
    public List<Staff> findByRole(String role) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Staff> query = session.createQuery(
                "FROM Staff s WHERE s.role = :role AND s.isActive = true", Staff.class);
            query.setParameter("role", role);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding staff by role: " + e.getMessage(), e);
        }
    }
 
    @Override
    public boolean existsByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                "SELECT COUNT(s) FROM Staff s WHERE s.email = :email", Long.class)
                .setParameter("email", email)
                .uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error checking staff email existence: " + e.getMessage(), e);
        }
    }
}
