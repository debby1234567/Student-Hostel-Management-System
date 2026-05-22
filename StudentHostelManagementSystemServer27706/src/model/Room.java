/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author CTRL-SHIFT Ltd
 */

@Entity
@Table(name = "rooms")
public class Room implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    public Room() {}
    
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "room_number", nullable = false, unique = true, length = 10)
    private String roomNumber;          
 
    @Column(name = "room_type", nullable = false, length = 20)
    private String roomType;            
 
    @Column(nullable = false)
    private int capacity;
 
    @Column(name = "price_per_month", nullable = false)
    private double pricePerMonth;
 
    @Column(nullable = false, length = 20)
    private String status;              
 
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Booking> bookings = new ArrayList<>();

    public Room(Long id, String roomNumber, String roomType, int capacity, double pricePerMonth, String status, String floor, boolean hasBathroom, boolean hasWifi, String amenities) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.capacity = capacity;
        this.pricePerMonth = pricePerMonth;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPricePerMonth() {
        return pricePerMonth;
    }

    public void setPricePerMonth(double pricePerMonth) {
        this.pricePerMonth = pricePerMonth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
    
}
