package view;

import javax.swing.*;
import java.awt.*;

/** Shared window and control styling for Swing forms. */
public final class UiUtil {

    public static final Color NAVY       = new Color(30, 58, 95);
    public static final Color PANEL_BG   = new Color(245, 247, 250);
    public static final Color SUCCESS    = new Color(30, 140, 60);
    public static final Color ERROR      = new Color(200, 50, 50);

    private UiUtil() {}

    /** Makes a frame resizable with sensible minimum dimensions. */
    public static void configureFrame(JFrame frame, int width, int height, int minWidth, int minHeight) {
        frame.setSize(width, height);
        frame.setMinimumSize(new Dimension(minWidth, minHeight));
        frame.setResizable(true);
    }

    public static void configureDialog(JDialog dialog, int width, int height, int minWidth, int minHeight) {
        dialog.setSize(width, height);
        dialog.setMinimumSize(new Dimension(minWidth, minHeight));
        dialog.setResizable(true);
    }

    public static void stylePrimaryButton(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    public static JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190, 205, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    public static String blankToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
