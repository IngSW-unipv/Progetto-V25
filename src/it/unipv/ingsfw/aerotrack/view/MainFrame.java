package it.unipv.ingsfw.aerotrack.view;

import javax.swing.*;
import java.awt.*;

/**
 * Finestra principale dell’applicazione AeroTrack.
 * Mostra un messaggio di benvenuto e funge da base per l’interfaccia grafica.
 */
public class MainFrame extends JFrame {
    
    public MainFrame() {
        super("Progetto-V25 - Applicazione Universitaria");

        // Imposta la chiusura del programma alla chiusura della finestra
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Imposta dimensioni iniziali
        setSize(600, 400);
        setLocationRelativeTo(null); // Centra la finestra sullo schermo

        // Costruisce il pannello principale con layout BorderLayout
        JPanel panel = new JPanel(new BorderLayout());

        // Messaggio di benvenuto
        JLabel label = new JLabel("Benvenuto nella demo del progetto!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, BorderLayout.CENTER);

        // Aggiunge il pannello al frame
        setContentPane(panel);

        // Imposta la finestra come visibile
        setVisible(true);
    }
}