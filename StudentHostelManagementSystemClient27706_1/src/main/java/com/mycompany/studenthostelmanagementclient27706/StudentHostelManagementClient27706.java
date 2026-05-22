package com.mycompany.studenthostelmanagementclient27706;

import javax.swing.SwingUtilities;
import view.LoginForm;

public class StudentHostelManagementClient27706 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}

