package controller;

import java.util.List;
import model.Staff;
import rmi.ServiceLocator;

public class StaffController {
    public boolean addStaff(Staff staff) {
        try { return ServiceLocator.getHostelService().addStaff(staff); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    public boolean updateStaff(Staff staff) {
        try { return ServiceLocator.getHostelService().updateStaff(staff); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    public boolean deleteStaff(Long id) {
        try { return ServiceLocator.getHostelService().deleteStaff(id); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    public List<Staff> getAllStaff() {
        try { return ServiceLocator.getHostelService().getAllStaff(); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
}
