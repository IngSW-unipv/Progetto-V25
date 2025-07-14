package it.unipv.ingsfw.aerotrack.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import it.unipv.ingsfw.aerotrack.services.*;

/**
 * Finestra principale dell’applicazione AeroTrack.
 * Mostra un messaggio di benvenuto e funge da base per l’interfaccia grafica.
 */
public class StaffFrame extends JFrame {
    
    public StaffFrame() {
        super("AeroTrack - Applicazione Universitaria per personale");
        setLookAndFeel();
        
        AeroportoService aeroportoService = AeroportoService.getInstance();

        // Imposta la chiusura del programma alla chiusura della finestra
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Imposta dimensioni iniziali
        setSize(950, 700);
        setLocationRelativeTo(null); // Centra la finestra sullo schermo

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        // Tab di Accesso
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(245, 248, 255));
        
        // Banner grafico
        JLabel banner = new JLabel();
        banner.setOpaque(true);
        banner.setBackground(new Color(230, 242, 255));
        banner.setHorizontalAlignment(SwingConstants.CENTER);
        banner.setPreferredSize(new Dimension(900, 120));
        URL imgUrl = getClass().getResource("/img/banner_airport.jpg");
        if (imgUrl != null) {
            ImageIcon img = new ImageIcon(imgUrl);
            banner.setIcon(new ImageIcon(img.getImage().getScaledInstance(900, 120, Image.SCALE_SMOOTH)));
        } else {
            banner.setText("AeroTrack");
            banner.setFont(new Font("Segoe UI", Font.BOLD, 32));
            banner.setForeground(new Color(27, 102, 186));
        }
        welcomePanel.add(banner, BorderLayout.NORTH);

        JLabel label = new JLabel("Benvenuto in AeroTrack! Per favore eseguire l'accesso", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 26));
        label.setForeground(new Color(27, 102, 186));
        welcomePanel.add(label, BorderLayout.CENTER);

     	// Login form
     	JPanel centerPanel = new JPanel(new GridBagLayout());
     	centerPanel.setBackground(new Color(245, 248, 255));
     	GridBagConstraints gbc = new GridBagConstraints();
     	gbc.insets = new Insets(16, 14, 16, 14);
     	gbc.fill = GridBagConstraints.HORIZONTAL;

     	JLabel passwordLabel = new JLabel("Inserisci la password:");
     	passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginButton.setBackground(new Color(27, 102, 186));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        // Arrotonda i bordi con una LineBorder
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(27, 102, 186), 2, true));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

     	// Layout: Etichetta
     	gbc.gridx = 0;
     	gbc.gridy = 0;
     	centerPanel.add(passwordLabel, gbc);

     	// Campo password
     	gbc.gridx = 1;
     	centerPanel.add(passwordField, gbc);

     	// Pulsante login
     	gbc.gridx = 1;
     	gbc.gridy = 1;
     	centerPanel.add(loginButton, gbc);
     	
     	welcomePanel.add(centerPanel, BorderLayout.CENTER);   
     	
     	Icon iconWelcome = emojiIcon("👋");
        Icon iconFlight = emojiIcon("✈️");
        Icon iconBooking = emojiIcon("📋");
        
        tabbedPane.addTab("Benvenuto", iconWelcome, welcomePanel);
        
     	loginButton.addActionListener(e -> {
     	    String password = new String(passwordField.getPassword());

     	    if (password.equals("Davide") || password.equals("Chiara") || password.equals("STAFF")) {
     	        JOptionPane.showMessageDialog(welcomePanel, "Accesso riuscito!");
     	        // Rimuovi tutti i tab dopo "Benvenuto"
                while (tabbedPane.getTabCount() > 1) {
                    tabbedPane.remove(1);
                }
               
                JPanel voliPanel = new StaffVoloPanel();
                JPanel prenotazioniPanel = new StaffPrenotazionePanel();
                addIconBanner(voliPanel, "/img/flight_banner.jpg", "Gestione Voli", new Font("Segoe UI", Font.BOLD, 22));
                addIconBanner(prenotazioniPanel, "/img/booking_banner.jpg", "Gestione Prenotazioni", new Font("Segoe UI", Font.BOLD, 22));

     	        // Nuovi tab per staff: gestione avanzata voli e prenotazioni
                tabbedPane.addTab("Gestione Voli", new StaffVoloPanel());
                tabbedPane.addTab("Gestione Prenotazioni", new StaffPrenotazionePanel());
                
                // Disabilita login dopo il successo 
                loginButton.setEnabled(false);
                passwordField.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(welcomePanel, "Password errata", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(tabbedPane);
        setVisible(true);
    }
    private void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIManager.put("TabbedPane.selected", new Color(230, 242, 255));
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    /** Emoji fallback se mancano le icone */
    private Icon emojiIcon(String emoji) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
                g.drawString(emoji, x, y + 20);
            }
            @Override
            public int getIconWidth() { return 28; }
            @Override
            public int getIconHeight() { return 28; }
        };
    }

    /** Banner grafico per ogni tab */
    private void addIconBanner(JPanel panel, String imgPath, String title, Font font) {
        try {
            JLabel banner = new JLabel();
            URL imgUrl = getClass().getResource(imgPath);
            if (imgUrl != null) {
                ImageIcon img = new ImageIcon(imgUrl);
                banner.setIcon(new ImageIcon(img.getImage().getScaledInstance(900, 80, Image.SCALE_SMOOTH)));
            } else {
                banner.setText(title);
                banner.setFont(font);
                banner.setForeground(new Color(27, 102, 186));
            }
            banner.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(banner, BorderLayout.NORTH);
        } catch (Exception ex) {
            JLabel t = new JLabel(title, SwingConstants.CENTER);
            t.setFont(font);
            t.setForeground(new Color(27, 102, 186));
            panel.add(t, BorderLayout.NORTH);
        }
    }
}