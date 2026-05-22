package controller;

import java.util.List;
import model.Payment;
import rmi.ServiceLocator;

public class PaymentController {
    public boolean recordPayment(Payment payment) {
        try { return ServiceLocator.getHostelService().recordPayment(payment); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    public boolean updatePayment(Payment payment) {
        try { return ServiceLocator.getHostelService().updatePayment(payment); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    public List<Payment> getAllPayments() {
        try { return ServiceLocator.getHostelService().getAllPayments(); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
}
