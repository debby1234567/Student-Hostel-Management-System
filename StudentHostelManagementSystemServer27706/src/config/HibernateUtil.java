package config;

import model.Booking;
import model.Otp;
import model.Payment;
import model.Room;
import model.Staff;
import model.Student;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Hibernate SessionFactory bootstrap.
 * – Registers all @Entity model classes explicitly (no missed mappings).
 * – Catches and prints the real cause if initialisation fails instead of
 *   silently dying with NoClassDefFoundError.
 *
 * @author CTRL-SHIFT Ltd
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration()
                .configure()   
                // Register every @Entity class here
                .addAnnotatedClass(Student.class)
                .addAnnotatedClass(Staff.class)
                .addAnnotatedClass(Room.class)
                .addAnnotatedClass(Booking.class)
                .addAnnotatedClass(Payment.class)
                .addAnnotatedClass(Otp.class)
                .buildSessionFactory();

            System.out.println("[HibernateUtil] SessionFactory initialised successfully.");

        } catch (Exception e) {
            System.err.println("=======================================================");
            System.err.println("[HibernateUtil] FATAL — Could not build SessionFactory.");
            System.err.println("Cause: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Root cause: " + e.getCause().getMessage());
            }
            System.err.println("=======================================================");
            e.printStackTrace(System.err);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Returns the shared SessionFactory.
     * Throws IllegalStateException if initialisation previously failed
     * so the error is obvious rather than a NullPointerException deep in a DAO.
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException(
                "HibernateUtil.sessionFactory is null — check server startup logs for the root cause.");
        }
        return sessionFactory;
    }

    /**
     * Call this once on server shutdown to release all DB connections cleanly.
     * Add  HibernateUtil.shutdown();  to your ServerMain before System.exit().
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            System.out.println("[HibernateUtil] SessionFactory closed.");
        }
    }
}