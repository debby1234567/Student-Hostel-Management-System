/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package notification;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.jms.Destination;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;


/**
 *
 * @author CTRL-SHIFT Ltd
 */
public class MessageBrokerService {
 
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static Connection connection;
    private static Session    session;
 
    static {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            try {
                connection = (Connection) factory.createConnection();
            } catch (jakarta.jms.JMSException ex) {
                Logger.getLogger(MessageBrokerService.class.getName()).log(Level.SEVERE, null, ex);
            }
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            System.out.println("[MessageBroker] Connected to ActiveMQ at " + BROKER_URL);
        } catch (JMSException e) {
            System.err.println("[MessageBroker] ActiveMQ not available — notifications disabled. " + e.getMessage());
        }
    }
 
    public static void publish(String topicName, String message) throws JMSException {
        if (session == null) {
            System.out.println("[MessageBroker] (offline) " + topicName + ": " + message);
            return;
        }
        Destination destination = null;
        try {
            destination = session.createQueue(topicName);
        } catch (JMSException ex) {
            Logger.getLogger(MessageBrokerService.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (MessageProducer producer = session.createProducer(destination)) {
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            TextMessage textMessage = session.createTextMessage(message);
            producer.send(textMessage);
            System.out.println("[MessageBroker] Published to " + topicName + ": " + message);
        }
    }
 
    
    public static void notifyBookingConfirmed(String studentName, String roomNumber) {
        try {
            publish("booking.confirmations",
                    "Booking confirmed for " + studentName + " → Room " + roomNumber);
        } catch (JMSException ex) {
            Logger.getLogger(MessageBrokerService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    public static void notifyPaymentReceived(String studentName, double amount) {
        try {
            publish("payment.receipts",
                    "Payment of RWF " + String.format("%.2f", amount) + " received from " + studentName);
        } catch (JMSException ex) {
            Logger.getLogger(MessageBrokerService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    public static void notifyBookingCancelled(String studentName, String roomNumber) {
        try {
            publish("booking.cancellations",
                    "Booking cancelled for " + studentName + " → Room " + roomNumber);
        } catch (JMSException ex) {
            Logger.getLogger(MessageBrokerService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    public static void shutdown() throws IOException {
        if (session    != null) try {
            session.close();
        } catch (JMSException ex) {
            Logger.getLogger(MessageBrokerService.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (connection != null) try {
            connection.close();
        } catch (JMSException ex) {
            Logger.getLogger(MessageBrokerService.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("[MessageBroker] Connection closed.");
    }
}