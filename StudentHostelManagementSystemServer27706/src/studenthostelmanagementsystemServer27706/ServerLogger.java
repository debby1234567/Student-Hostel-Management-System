package studenthostelmanagementsystemServer27706;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple console logger for the RMI server.
 * Drop ServerLogger.log(...) calls anywhere in the server
 * and messages will appear formatted in the NetBeans Output tab.
 *
 * Usage:
 *   ServerLogger.info("Server started on port 1099");
 *   ServerLogger.warn("Student login attempt failed for: " + studentId);
 *   ServerLogger.error("DB error in addStudent: " + e.getMessage(), e);
 *
 * @author CTRL-SHIFT Ltd
 */
public class ServerLogger {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String RESET  = "\u001B[0m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED    = "\u001B[31m";
    private static final String CYAN   = "\u001B[36m";
    private static final String BOLD   = "\u001B[1m";

    // ── Public API ────────────────────────────────────────────────────────

    /** General information — green */
    public static void info(String message) {
        print("INFO ", GREEN, message);
    }

    /** Warning — something unexpected but not fatal — yellow */
    public static void warn(String message) {
        print("WARN ", YELLOW, message);
    }

    /**
     * Error — prints the message in red AND dumps the full stack trace
     * to System.err so NetBeans shows it in the Output tab.
     */
    public static void error(String message, Throwable cause) {
        print("ERROR", RED, message);
        if (cause != null) {
            System.err.println(RED + "    Caused by: " + cause.getClass().getName()
                + ": " + cause.getMessage() + RESET);
            for (StackTraceElement el : cause.getStackTrace()) {
                System.err.println(RED + "        at " + el + RESET);
            }
        }
    }

    /** Error without a throwable */
    public static void error(String message) {
        error(message, null);
    }

    /** OTP / login events — cyan */
    public static void auth(String message) {
        print("AUTH ", CYAN, message);
    }

    /** Separator line — useful between startup sections */
    public static void separator() {
        System.out.println("─────────────────────────────────────────────────────");
    }

    // ── Private ───────────────────────────────────────────────────────────

    private static void print(String level, String colour, String message) {
        String timestamp = LocalDateTime.now().format(FMT);
        System.out.println(
            colour + BOLD + "[" + level + "]" + RESET +
            "  " + timestamp +
            "  " + colour + message + RESET
        );
    }
}