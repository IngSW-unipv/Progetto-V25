package it.unipv.ingsfw.aerotrack.test;

import it.unipv.ingsfw.aerotrack.view.MainFrame;
import javax.swing.*;

/**
 * Avvio dell'applicazione AeroTrack.
 */
public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}