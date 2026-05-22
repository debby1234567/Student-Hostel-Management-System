package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import service.HostelService;

public class ServiceLocator {
    private static HostelService service;

    public static HostelService getHostelService() throws Exception {
        if (service == null) {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            service = (HostelService) registry.lookup("HostelService");
        }
        return service;
    }

    public static void reset() {
        service = null;
    }
}
