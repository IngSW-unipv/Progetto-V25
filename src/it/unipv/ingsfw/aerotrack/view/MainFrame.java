package it.unipv.ingsfw.aerotrack.view;

import javax.swing.*;
import java.awt.*;

/**
 * Finestra principale dell’applicazione AeroTrack.
 * Mostra un messaggio di benvenuto e funge da base per l’interfaccia grafica.
 */
public class MainFrame extends JFrame {
    
    public MainFrame() {
        super("AeroTrack - Applicazione Universitaria");

        // Imposta la chiusura del programma alla chiusura della finestra
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Imposta dimensioni iniziali
        setSize(800, 600);
        setLocationRelativeTo(null); // Centra la finestra sullo schermo

        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab di Benvenuto
        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Benvenuto nella demo del progetto!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        welcomePanel.add(label, BorderLayout.CENTER);
        tabbedPane.addTab("Benvenuto", welcomePanel);

        // Tab CRUD
        tabbedPane.addTab("Aeroporti", new AeroportoPanel());
        tabbedPane.addTab("Voli", new VoloPanel());
        tabbedPane.addTab("Prenotazioni", new PrenotazionePanel());

        setContentPane(tabbedPane);
        setVisible(true);
        }
    }
    