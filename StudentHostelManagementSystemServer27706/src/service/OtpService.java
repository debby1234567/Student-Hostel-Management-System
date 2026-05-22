/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.IOtpDAO;
import dao.OtpDAOImplementation;
import model.Otp;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.Random;

/**
 *
 * @author CTRL-SHIFT Ltd
 */
public class OtpService {
 
    private final IOtpDAO otpDAO = new OtpDAOImplementation();
 
    // ── Gmail credentials ─────────────────────────────────────────────────
    private static final String SENDER_EMAIL    = "iradukundadebora26@gmail.com";
    private static final String SENDER_PASSWORD = "mlvh mbro tpcy jmur";   // Gmail App Password
    // ─────────────────────────────────────────────────────────────────────
 
    /**
     * Generates a 6-digit OTP, saves it in the DB, and emails it.
     * Any previous unused OTPs for this email are deleted first.
     *
     * @param email    recipient email address
     * @param userType "STUDENT" or "STAFF"
     * @throws RuntimeException if the email cannot be sent
     */
    public void generateAndSendOtp(String email, String userType) {
        // Remove any stale OTPs for this email before creating a new one
        otpDAO.deleteByEmail(email);
 
        // Generate a zero-padded 6-digit code
        String code = String.format("%06d", new Random().nextInt(999999));
 
        // Persist to DB (includes creation timestamp for expiry checks)
        Otp otp = new Otp(email, code, userType);
        otpDAO.save(otp);
 
        // Deliver via Gmail SMTP
        sendOtpEmail(email, code);
    }
 
    /**
     * Validates the code entered by the user.
     *
     * @param email       the user's email
     * @param enteredCode the 6-digit code submitted from the OTP panel
     * @return true if the code matches, is not expired, and has not been used
     */
    public boolean verifyOtp(String email, String enteredCode) {
        Otp otp = otpDAO.findLatestByEmail(email);
 
        if (otp == null)                              return false;
        if (otp.isExpired())                          return false;
        if (!otp.getOtpCode().equals(enteredCode))   return false;
 
        // Mark as used so the same code cannot be replayed
        otpDAO.markAsUsed(otp.getId());
        return true;
    }
 
    // ── Private email sender ──────────────────────────────────────────────
 
    private void sendOtpEmail(String toEmail, String code) {
        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            "smtp.gmail.com");
        props.put("mail.smtp.port",            "587");
        props.put("mail.smtp.ssl.trust",       "smtp.gmail.com");
 
        Session mailSession = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });
 
        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "Hostel Management System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your OTP Verification Code – Hostel Management");
 
            String htmlBody =
                "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;" +
                "     border:1px solid #e0e0e0;border-radius:8px;overflow:hidden;'>" +
 
                "  <div style='background:#1E3A5F;padding:20px 30px;'>" +
                "    <h2 style='color:#fff;margin:0;font-size:20px;'>🏠 Hostel Management System</h2>" +
                "  </div>" +
 
                "  <div style='padding:30px;'>" +
                "    <p style='font-size:15px;color:#333;'>Hello,</p>" +
                "    <p style='font-size:14px;color:#555;'>" +
                "      Use the one-time code below to complete your sign-in." +
                "      This code expires in <strong>5 minutes</strong>." +
                "    </p>" +
 
                "    <div style='margin:30px auto;text-align:center;" +
                "         background:#f4f6fa;border-radius:10px;padding:20px;" +
                "         border:2px dashed #1E3A5F;width:fit-content;min-width:200px;'>" +
                "      <span style='font-size:42px;font-weight:bold;" +
                "            letter-spacing:14px;color:#E74C3C;'>" +
                           code +
                "      </span>" +
                "    </div>" +
 
                "    <p style='font-size:13px;color:#888;margin-top:20px;'>" +
                "      If you did not attempt to sign in, please ignore this email." +
                "      Your account remains secure." +
                "    </p>" +
                "  </div>" +
 
                "  <div style='background:#f9f9f9;padding:12px 30px;" +
                "       border-top:1px solid #eee;text-align:center;" +
                "       font-size:11px;color:#aaa;'>" +
                "    © 2026 Hostel Management System · CTRL-SHIFT Ltd" +
                "  </div>" +
                "</div>";
 
            message.setContent(htmlBody, "text/html; charset=utf-8");
            Transport.send(message);
 
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email to " + toEmail + ": " + e.getMessage(), e);
        }
    }
}