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
public class StaffFrame extends JFrame {
    
    public StaffFrame() {
        super("AeroTrack - Applicazione Universitaria per personale");
        AeroportoService aeroportoService = AeroportoService.getInstance();

        // Imposta la chiusura del programma alla chiusura della finestra
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Imposta dimensioni iniziali
        setSize(800, 600);
        setLocationRelativeTo(null); // Centra la finestra sullo schermo

        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab di Accesso
        JPanel welcomePanel = new JPanel(new BorderLayout());
        
        tabbedPane.addTab("Benvenuto", welcomePanel);
        
        JLabel label = new JLabel("Benvenuto nella demo del progetto! Per favore eseguire l'accesso", SwingConstants.CENTER);
     	label.setFont(new Font("Arial", Font.BOLD, 20));
     	welcomePanel.add(label, BorderLayout.NORTH);

     	// Pannello centrale con campo password
     	JPanel centerPanel = new JPanel(new GridBagLayout());
     	GridBagConstraints gbc = new GridBagConstraints();
     	gbc.insets = new Insets(10, 10, 10, 10);
     	gbc.fill = GridBagConstraints.HORIZONTAL;

     	JLabel passwordLabel = new JLabel("Inserisci la password:");
     	JPasswordField passwordField = new JPasswordField(15);
     	JButton loginButton = new JButton("Login");

     	// Layout: Etichetta
     	gbc.gridx = 0;
     	gbc.gridy = 0;
     	centerPanel.add(passwordLabel, gbc);

     	// Campo password
     	gbc.gridx = 1;
     	gbc.gridy = 0;
     	centerPanel.add(passwordField, gbc);

     	// Pulsante login
     	gbc.gridx = 1;
     	gbc.gridy = 1;
     	centerPanel.add(loginButton, gbc);

    
     	welcomePanel.add(centerPanel, BorderLayout.CENTER);   
     	
     	loginButton.addActionListener(e -> {
     	    String password = new String(passwordField.getPassword());

     	    if (password.equals("Davide") || password.equals("Chiara")) {
     	        JOptionPane.showMessageDialog(welcomePanel, "Accesso riuscito!");
     	        
     	        // Tab CRUD
     	        PrenotazionePanel prenotazionePanel = new PrenotazionePanel(); 
     	        VoloPanel voloPanel = new VoloPanel(prenotazionePanel);        

     	        tabbedPane.addTab("Prenotazioni", prenotazionePanel);
     	        tabbedPane.addTab("Voli", voloPanel);
     	    } else {
     	        JOptionPane.showMessageDialog(welcomePanel, "Password errata", "Errore", JOptionPane.ERROR_MESSAGE);
     	    }
     	});

        setContentPane(tabbedPane);
        setVisible(true);
        }
    }
    