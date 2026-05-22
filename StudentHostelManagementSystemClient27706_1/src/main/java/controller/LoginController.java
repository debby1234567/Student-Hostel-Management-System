package controller;

import model.Staff;
import model.Student;
import rmi.ServiceLocator;
import service.HostelService;

public class LoginController {

    public Staff loginStaff(String email, String password) {
        try {
            HostelService service = ServiceLocator.getHostelService();
            return service.loginStaff(email, password);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Student loginStudent(String studentId, String password) {
        try {
            HostelService service = ServiceLocator.getHostelService();
            return service.loginStudent(studentId, password);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /** Legacy plain authenticate call */
    public boolean login(String username, String password) {
        try {
            return ServiceLocator.getHostelService().authenticate(username, password);
        } catch (Exception e) {
            return false;
        }
    }
}
