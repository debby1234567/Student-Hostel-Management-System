/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.HibernateUtil;
import model.Room;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
 
import java.util.List;

/**
 *
 * @author CTRL-SHIFT Ltd
 */
public class RoomDAOImplementation implements IRoomDAO {
 
    @Override
    public void save(Room room) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(room);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error saving room: " + e.getMessage(), e);
        }
    }
 
    @Override
    public void update(Room room) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(room);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error updating room: " + e.getMessage(), e);
        }
    }
 
    @Override
    public void delete(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Room room = session.get(Room.class, id);
            if (room != null) session.delete(room);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error deleting room: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Room findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Room.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error finding room by id: " + e.getMessage(), e);
        }
    }
 
    @Override
    public Room findByRoomNumber(String roomNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Room> query = session.createQuery(
                "FROM Room r WHERE r.roomNumber = :num", Room.class);
            query.setParameter("num", roomNumber);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Error finding room by number: " + e.getMessage(), e);
        }
    }
 
    @Override
    public List<Room> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Room r ORDER BY r.roomNumber", Room.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all rooms: " + e.getMessage(), e);
        }
    }
 
    @Override
    public List<Room> findAvailableRooms() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Room r WHERE r.status = 'Available' ORDER BY r.roomNumber", Room.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching available rooms: " + e.getMessage(), e);
        }
    }
 
    @Override
    public List<Room> findByType(String roomType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Room> query = session.createQuery(
                "FROM Room r WHERE r.roomType = :type AND r.status = 'Available'", Room.class);
            query.setParameter("type", roomType);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching rooms by type: " + e.getMessage(), e);
        }
    }
 
    @Override
    public boolean existsByRoomNumber(String roomNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                "SELECT COUNT(r) FROM Room r WHERE r.roomNumber = :num", Long.class)
                .setParameter("num", roomNumber)
                .uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error checking room existence: " + e.getMessage(), e);
        }
    }
 
    @Override
    public void updateStatus(Long roomId, String status) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery("UPDATE Room r SET r.status = :status WHERE r.id = :id")
                .setParameter("status", status)
                .setParameter("id", roomId)
                .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error updating room status: " + e.getMessage(), e);
        }
    }
}
