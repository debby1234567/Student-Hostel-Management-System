/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servlet;

import service.OtpService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Handles OTP send, verify, and resend actions.
 *
 * URL mapping:
 *   POST /OtpServlet?action=send    → called right after login succeeds
 *   POST /OtpServlet?action=verify  → called when user submits the OTP form
 *   POST /OtpServlet?action=resend  → called when user clicks "Resend OTP"
 *
 * @author CTRL-SHIFT Ltd
 */
@WebServlet(name = "OtpServlet", urlPatterns = {"/OtpServlet"})
public class OtpServlet extends HttpServlet {

    private final OtpService otpService = new OtpService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        if (action == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        switch (action) {

            // ── SEND ─────────────────────────────────────────────────────────
            case "send": {
                String email    = (String) session.getAttribute("pendingEmail");
                String userType = (String) session.getAttribute("pendingUserType");

                if (email == null || userType == null) {
                    response.sendRedirect("login.jsp?error=session_expired");
                    return;
                }

                try {
                    otpService.generateAndSendOtp(email, userType);
                    response.sendRedirect("otp-verify.jsp");
                } catch (Exception e) {
                    request.setAttribute("error", "Failed to send OTP. Please try again.");
                    request.getRequestDispatcher("otp-verify.jsp").forward(request, response);
                }
                break;
            }

            // ── VERIFY ───────────────────────────────────────────────────────
            case "verify": {
                String email    = (String) session.getAttribute("pendingEmail");
                String userType = (String) session.getAttribute("pendingUserType");
                String enteredCode = request.getParameter("otpCode");

                if (email == null || userType == null) {
                    response.sendRedirect("login.jsp?error=session_expired");
                    return;
                }

                boolean valid = otpService.verifyOtp(email, enteredCode);

                if (valid) {
                    // OTP passed — promote the pending user to a real session
                    session.setAttribute("verifiedEmail", email);
                    session.setAttribute("userType", userType);
                    session.removeAttribute("pendingEmail");
                    session.removeAttribute("pendingUserType");

                    // Redirect based on role
                    if ("STAFF".equalsIgnoreCase(userType)) {
                        response.sendRedirect("staff-dashboard.jsp");
                    } else {
                        response.sendRedirect("student-dashboard.jsp");
                    }

                } else {
                    request.setAttribute("error", "Invalid or expired OTP. Please try again.");
                    request.getRequestDispatcher("otp-verify.jsp").forward(request, response);
                }
                break;
            }

            // ── RESEND ───────────────────────────────────────────────────────
            case "resend": {
                String email    = (String) session.getAttribute("pendingEmail");
                String userType = (String) session.getAttribute("pendingUserType");

                if (email == null || userType == null) {
                    response.sendRedirect("login.jsp?error=session_expired");
                    return;
                }

                try {
                    otpService.generateAndSendOtp(email, userType);
                    request.setAttribute("message", "A new OTP has been sent to your email.");
                    request.getRequestDispatcher("otp-verify.jsp").forward(request, response);
                } catch (Exception e) {
                    request.setAttribute("error", "Failed to resend OTP. Please try again.");
                    request.getRequestDispatcher("otp-verify.jsp").forward(request, response);
                }
                break;
            }

            default:
                response.sendRedirect("login.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}