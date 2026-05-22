package studenthostelmanagementsystemServer27706;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import service.HostelService;
import service.Implementation.HostelServiceImplementation;

/**
 * @author CTRL-SHIFT Ltd
 */
public class ServerMain {
    public static void main(String[] args) {
        ServerLogger.separator();
        ServerLogger.info("Student Hostel Management System - Server Starting");
        ServerLogger.separator();

        try {
            HostelService service = new HostelServiceImplementation();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("HostelService", service);

            ServerLogger.info("RMI registry created on port 1099");
            ServerLogger.info("HostelService bound successfully");
            ServerLogger.separator();
            ServerLogger.info("Server is RUNNING - waiting for client connections...");
            ServerLogger.separator();

        } catch (Exception e) {
            ServerLogger.error("Server failed to start: " + e.getMessage(), e);
        }
    }
}