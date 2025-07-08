package it.unipv.ingsfw.aerotrack.test;

import it.unipv.ingsfw.aerotrack.view.UserFrame;
import javax.swing.*;

/**
 * Avvio dell'applicazione AeroTrack.
 */
public class UserApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserFrame::new);
    }
}