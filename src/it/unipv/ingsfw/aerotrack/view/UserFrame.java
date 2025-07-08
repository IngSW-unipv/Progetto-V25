package it.unipv.ingsfw.aerotrack.view;

import javax.swing.*;
import java.awt.*;

import it.unipv.ingsfw.aerotrack.models.Aeroporto;
import it.unipv.ingsfw.aerotrack.services.*;

import java.util.ArrayList;

/**
 * Finestra principale dell’applicazione AeroTrack.
 * Mostra un messaggio di benvenuto e funge da base per l’interfaccia grafica.
 */
public class UserFrame extends JFrame {
    
    public UserFrame() {
        super("AeroTrack - Applicazione Universitaria per gli utenti");
        AeroportoService aeroportoService = AeroportoService.getInstance();

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
        PrenotazionePanel prenotazionePanel = new PrenotazionePanel(); 
        VoloPanel voloPanel = new VoloPanel(prenotazionePanel);        

        tabbedPane.addTab("Voli", voloPanel);
        tabbedPane.addTab("Prenotazioni", prenotazionePanel);

        setContentPane(tabbedPane);
        setVisible(true);
        }
    }
    