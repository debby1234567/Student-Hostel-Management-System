package controller;

import model.Booking;
import rmi.ServiceLocator;
import java.util.List;

public class BookingController {
    public boolean createBooking(Booking booking) {
        try { return ServiceLocator.getHostelService().createBooking(booking); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    public boolean updateBooking(Booking booking) {
        try { return ServiceLocator.getHostelService().updateBooking(booking); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    public boolean cancelBooking(Long bookingId) {
        try { return ServiceLocator.getHostelService().cancelBooking(bookingId); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    public List<Booking> getAllBookings() {
        try { return ServiceLocator.getHostelService().getAllBookings(); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
}
