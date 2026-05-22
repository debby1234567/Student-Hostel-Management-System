package controller;

import java.util.List;
import model.Room;
import rmi.ServiceLocator;

public class RoomController {
    public boolean addRoom(Room room) {
        try { return ServiceLocator.getHostelService().addRoom(room); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    public boolean updateRoom(Room room) {
        try { return ServiceLocator.getHostelService().updateRoom(room); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    public boolean deleteRoom(Long id) {
        try { return ServiceLocator.getHostelService().deleteRoom(id); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    public List<Room> getAllRooms() {
        try { return ServiceLocator.getHostelService().getAllRooms(); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    public List<Room> getAvailableRooms() {
        try { return ServiceLocator.getHostelService().getAvailableRooms(); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
}
