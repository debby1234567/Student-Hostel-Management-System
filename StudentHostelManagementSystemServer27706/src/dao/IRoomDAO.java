/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import model.Room;
import java.util.List;

/**
 *
 * @author CTRL-SHIFT Ltd
 */
public interface IRoomDAO {
    void save(Room room);
    void update(Room room);
    void delete(Long id);
    Room findById(Long id);
    Room findByRoomNumber(String roomNumber);
    List<Room> findAll();
    List<Room> findAvailableRooms();
    List<Room> findByType(String roomType);
    boolean existsByRoomNumber(String roomNumber);
    void updateStatus(Long roomId, String status);
}
