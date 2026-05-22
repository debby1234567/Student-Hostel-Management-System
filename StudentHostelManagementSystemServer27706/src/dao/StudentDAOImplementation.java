/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.HibernateUtil;
import model.Student;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
 
import java.util.List;
/**
 *
 * @author CTRL-SHIFT Ltd
 */
public class StudentDAOImplementation implements IStudentDAO {
 
    @Override
    public void save(Student student) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(student);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error saving student: " + e.getMessage(), e);
        }
    }
 
    @Override
    public void update(Student student) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(student);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error updating student: " + e.getMessage(), e);
        }
    }
 
    @Override
    public void delete(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Student student = session.get(Student.class, id);
            if (student != null) {
                student.setActive(false); 
                session.update(student);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error deleting student: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Student findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Student.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error finding student by id: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Student findByStudentId(String studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Student> query = session.createQuery(
                "FROM Student s WHERE s.studentId = :sid AND s.isActive = true", Student.class);
            query.setParameter("sid", studentId);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Error finding student by studentId: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Student findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Student> query = session.createQuery(
                "FROM Student s WHERE s.email = :email AND s.isActive = true", Student.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Error finding student by email: " + e.getMessage(), e);
        }
    }
 
    @Override
    public List<Student> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Student s WHERE s.isActive = true ORDER BY s.lastName", Student.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all students: " + e.getMessage(), e);
        }
    }
 
    @Override
    public boolean existsByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                "SELECT COUNT(s) FROM Student s WHERE s.email = :email", Long.class)
                .setParameter("email", email)
                .uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error checking email existence: " + e.getMessage(), e);
        }
    }
 
    @Override
    public boolean existsByStudentId(String studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                "SELECT COUNT(s) FROM Student s WHERE s.studentId = :sid", Long.class)
                .setParameter("sid", studentId)
                .uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error checking studentId existence: " + e.getMessage(), e);
        }
    }
 
    @Override
    public boolean existsByNationalId(String nationalId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                "SELECT COUNT(s) FROM Student s WHERE s.nationalId = :nid", Long.class)
                .setParameter("nid", nationalId)
                .uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error checking nationalId existence: " + e.getMessage(), e);
        }
    }
}
