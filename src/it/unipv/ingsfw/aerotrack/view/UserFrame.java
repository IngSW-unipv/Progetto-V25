package it.unipv.ingsfw.aerotrack.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.awt.image.BufferedImage;

import it.unipv.ingsfw.aerotrack.services.*;

/**
 * Finestra principale dell’applicazione AeroTrack.
 * Mostra un messaggio di benvenuto e funge da base per l’interfaccia grafica.
 */
public class UserFrame extends JFrame {
	private final JTabbedPane tabbedPane;
	private String codiceFiscaleUtente; 
	
    public UserFrame() {
        super("AeroTrack - Applicazione Universitaria per gli utenti");
        AeroportoService aeroportoService = AeroportoService.getInstance();

        // LOGIN: Chiedi codice fiscale all’utente
        codiceFiscaleUtente = chiediCodiceFiscale();
        if (codiceFiscaleUtente == null || codiceFiscaleUtente.isBlank()) {
            JOptionPane.showMessageDialog(this, "Codice fiscale obbligatorio. L'app verrà chiusa.");
            System.exit(0);
        }

        // Imposta la chiusura del programma alla chiusura della finestra
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Imposta dimensioni iniziali
        setMinimumSize(new Dimension(780, 500));
        setPreferredSize(new Dimension(1000, 700));
        setLocationRelativeTo(null); // Centra la finestra sullo schermo

        // Font e colori moderni
        Font titleFont = new Font("Segoe UI", Font.BOLD, 26);
        Font tabFont = new Font("Segoe UI", Font.PLAIN, 18);

        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 16));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 16));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 15));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 16));
        UIManager.put("TabbedPane.font", tabFont);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(tabFont);
        
        // Icone sui tab (emoji fallback)
        Icon iconWelcome = loadIcon("/icons/hello.png", "👋");
        Icon iconFlight = loadIcon("/icons/airplane.png", "✈️");
        Icon iconBooking = loadIcon("/icons/ticket.png", "🎟️");

       
        // Tab di Benvenuto
        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel banner = new JLabel();
        banner.setOpaque(true);
        banner.setBackground(new Color(230, 242, 255));
        banner.setHorizontalAlignment(SwingConstants.CENTER);
        banner.setPreferredSize(new Dimension(900, 140));

        URL imgUrl = getClass().getResource("/img/banner_airport.jpg");
        if (imgUrl != null) {
            ImageIcon img = new ImageIcon(imgUrl);
            banner.setIcon(new ImageIcon(img.getImage().getScaledInstance(900, 140, Image.SCALE_SMOOTH)));
        } else {
            banner.setText("AeroTrack");
            banner.setFont(titleFont);
            banner.setForeground(new Color(27, 102, 186));
        }
        welcomePanel.add(banner, BorderLayout.NORTH);

        JLabel label = new JLabel("Benvenuto in AeroTrack!", SwingConstants.CENTER);
        label.setFont(titleFont);
        label.setForeground(new Color(27, 102, 186));
        welcomePanel.add(label, BorderLayout.CENTER);
        welcomePanel.setBackground(new Color(245, 248, 255));
        tabbedPane.addTab("Benvenuto", iconWelcome, welcomePanel);
        
        // COSTRUTTORE: passa codice fiscale utente
        PrenotazionePanel prenotazionePanel = new PrenotazionePanel(codiceFiscaleUtente); 
        VoloPanel voloPanel = new VoloPanel(prenotazionePanel);        

        addIconBanner(voloPanel, "/img/flight_banner.jpg", "Voli AeroTrack", titleFont);
        addIconBanner(prenotazionePanel, "/img/booking_banner.jpg", "Le tue Prenotazioni", titleFont);

        tabbedPane.addTab("Voli", iconFlight, voloPanel);
        tabbedPane.addTab("Prenotazioni", iconBooking, prenotazionePanel);

        tabbedPane.addChangeListener(e -> animateTabTransition());

        setContentPane(tabbedPane);
        pack();
        setVisible(true);
    }

    private String chiediCodiceFiscale() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        JLabel l = new JLabel("Inserisci il tuo codice fiscale:", SwingConstants.CENTER);
        JTextField tf = new JTextField();
        panel.add(l, BorderLayout.NORTH);
        panel.add(tf, BorderLayout.CENTER);
        int res = JOptionPane.showConfirmDialog(this, panel, "Login Utente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            return tf.getText().trim().toUpperCase();
        }
        return null;
    }   

    /**
     * Fade-in leggero per transizione tra tab
     */
    private void animateTabTransition() {
        final Component current = tabbedPane.getSelectedComponent();
        if (current == null) return;
        Timer timer = new Timer(20, null);
        final float[] alpha = {0f};
        JLayeredPane layeredPane = getLayeredPane();
        JPanel fadePanel = new JPanel() {
            @Override
        	protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - alpha[0]));
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        fadePanel.setOpaque(false);
        fadePanel.setBounds(current.getBounds());
        layeredPane.add(fadePanel, JLayeredPane.POPUP_LAYER);
        timer.addActionListener(evt -> {
            alpha[0] += 0.09f;
            fadePanel.repaint();
            if (alpha[0] >= 1f) {
                layeredPane.remove(fadePanel);
                layeredPane.repaint();
                ((Timer) evt.getSource()).stop();
            }
        });
        timer.start();
    }

    /**
     * Utility per icone: se non trovata, mostra emoji fallback.
     */
    private Icon loadIcon(String path, String fallbackEmoji) {
        try {
            URL res = getClass().getResource(path);
            if (res != null)
                return new ImageIcon(new ImageIcon(res).getImage().getScaledInstance(26, 26, Image.SCALE_SMOOTH));
        } catch (Exception e) {}
        return emojiIcon(fallbackEmoji);
    }

    private Icon emojiIcon(String emoji) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
                g.drawString(emoji, x, y + 20);
            }
            @Override
            public int getIconWidth() { return 26; }
            @Override
            public int getIconHeight() { return 26; }
        };
    }

    /**
     * Utility per banner grafico
     */
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
    