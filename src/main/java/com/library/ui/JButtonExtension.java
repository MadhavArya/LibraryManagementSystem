package main.java.com.library.ui;

import javax.swing.JButton;
import java.awt.event.ActionListener;

// Utility extension to remove all action listeners
class JButtonExtension {
    public static void removeActionListeners(JButton button) {
        for (ActionListener al : button.getActionListeners()) {
            button.removeActionListener(al);
        }
    }
}
