package it.unipv.ingsfw.aerotrack.test;

import it.unipv.ingsfw.aerotrack.view.StaffFrame;
import javax.swing.*;

/**
 * Avvio dell'applicazione AeroTrack.
 */
public class StaffApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StaffFrame::new);
    }
}