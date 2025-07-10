package it.unipv.ingsfw.aerotrack.view;

import it.unipv.ingsfw.aerotrack.models.Prenotazione;
import it.unipv.ingsfw.aerotrack.models.Volo;
import it.unipv.ingsfw.aerotrack.services.PrenotazioneService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Pannello Swing per la gestione delle prenotazioni.
 */
public class UserPrenotazionePanel extends JPanel {
    private final PrenotazioneService prenotazioneService;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final String codiceFiscaleUtente;

    // COSTRUTTORE CHE ACCETTA CODICE FISCALE
    public UserPrenotazionePanel(String codiceFiscaleUtente) {
        this.codiceFiscaleUtente = codiceFiscaleUtente;
        this.prenotazioneService = PrenotazioneService.getInstance();
        setLayout(new BorderLayout());
        
        // Tabella prenotazioni
        tableModel = new DefaultTableModel(
        	new Object[]{"Codice Prenotazione", "Nome Completo", "Codice Fiscale", "Codice Volo", "Stato"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
            	return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottoni CRUD + Export + Stampa
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Aggiungi");
        JButton removeBtn = new JButton("Cancella");
        JButton exportBtn = new JButton("Esporta CSV");
        JButton printBtn = new JButton("Stampa Biglietto");
        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(exportBtn);
        buttonPanel.add(printBtn);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Listener aggiungi prenotazione
        addBtn.addActionListener(e -> {
            JTextField nome = new JTextField();
            JTextField cognome = new JTextField();
            JTextField codiceVolo = new JTextField();
            Object[] fields = {
                    "Nome:", nome,
                    "Cognome:", cognome,
                    "Codice volo:", codiceVolo
            };
            int res = JOptionPane.showConfirmDialog(this, fields, "Nuova Prenotazione", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    prenotazioneService.creaPrenotazione(
                            nome.getText().trim(),
                            cognome.getText().trim(),
                            codiceFiscaleUtente,
                            codiceVolo.getText().trim()
                    );
                    aggiornaTabella();
                    JOptionPane.showMessageDialog(this, "Prenotazione aggiunta!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Listener cancella prenotazione 
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleziona una prenotazione da cancellare.");
                return;
            }
            String codicePrenotazione = (String) tableModel.getValueAt(row, 0);
            List<Prenotazione> prenotazioni = prenotazioneService.getTuttePrenotazioni();
            Prenotazione prenotazione = prenotazioni.stream()
                    .filter(p -> p.getCodicePrenotazione().equals(codicePrenotazione))
                    .findFirst().orElse(null);

            if (prenotazione == null) {
                JOptionPane.showMessageDialog(this, "Prenotazione non trovata.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (prenotazione.isCancellata()) {
                JOptionPane.showMessageDialog(this, "Prenotazione già cancellata.");
                return;
            }
            int conf = JOptionPane.showConfirmDialog(this, "Sicuro di voler cancellare la prenotazione?", "Conferma", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                prenotazione.cancella();
                if (conf == JOptionPane.YES_OPTION) {
                    prenotazione.cancella();
                    try {
                        prenotazioneService.aggiornaPrenotazione(prenotazione); // Salva nel DB
                        aggiornaTabella();
                        JOptionPane.showMessageDialog(this, "Prenotazione cancellata!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Errore durante la cancellazione: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        });

     // Listener esporta CSV
        exportBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile().getAbsolutePath())) {
                    // Scrivi intestazione
                    fw.write("Codice Prenotazione,Nome Completo,Documento,Codice Volo,Stato\n");
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        for (int j = 0; j < tableModel.getColumnCount(); j++) {
                            fw.write(tableModel.getValueAt(i, j).toString());
                            if (j < tableModel.getColumnCount() - 1) fw.write(",");
                        }
                        fw.write("\n");
                    }
                    JOptionPane.showMessageDialog(this, "Prenotazioni esportate!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Errore durante l'esportazione: " + ex.getMessage());
                }
            }
        });

        // Listener stampa biglietto 
        printBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleziona una prenotazione da stampare!");
                return;
            }
            String codicePrenotazione = (String) tableModel.getValueAt(row, 0);
            List<Prenotazione> prenotazioni = prenotazioneService.getTuttePrenotazioni();
            Prenotazione prenotazione = prenotazioni.stream()
                    .filter(p -> p.getCodicePrenotazione().equals(codicePrenotazione))
                    .findFirst().orElse(null);

            if (prenotazione == null) {
                JOptionPane.showMessageDialog(this, "Prenotazione non trovata.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            StringBuilder ticket = new StringBuilder();
            ticket.append("------ BIGLIETTO AEREO ------\n");
            ticket.append("Codice Prenotazione: ").append(prenotazione.getCodicePrenotazione()).append("\n");
            ticket.append("Nome: ").append(prenotazione.getPasseggero().getNomeCompleto()).append("\n");
            ticket.append("Documento: ").append(prenotazione.getPasseggero().getDocumento()).append("\n");
            if (prenotazione.getVolo() != null) {
                Volo volo = prenotazione.getVolo();
                ticket.append("Volo: ").append(volo.getCodice()).append("\n");
                ticket.append("Partenza: ").append(volo.getPartenza().getCodice()).append("\n");
                ticket.append("Destinazione: ").append(volo.getDestinazione().getCodice()).append("\n");
                ticket.append("Orario: ").append(volo.getOrarioPartenza()).append("\n");
                ticket.append("Pista: ").append(volo.getPistaAssegnata() >= 0 ? volo.getPistaAssegnata() : "Non assegnata").append("\n");
                ticket.append("Stato: ").append(prenotazione.isCancellata() ? "CANCELLATA" : "ATTIVA").append("\n");
            }
            ticket.append("------------------------------\n");

            JTextArea area = new JTextArea(ticket.toString());
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Biglietto", JOptionPane.INFORMATION_MESSAGE);
        });

        aggiornaTabella();
    }

    public void aggiornaTabella() {
        tableModel.setRowCount(0);
        List<Prenotazione> prenotazioni = prenotazioneService.getTuttePrenotazioni();
        for (Prenotazione p : prenotazioni) {
        	// FILTRO: solo le prenotazioni del codice fiscale utente (cioè del documento)
            if (p.getPasseggero().getDocumento().equalsIgnoreCase(codiceFiscaleUtente)) {
            	// Notifica utente se il volo è in ritardo o cancellato
            	if (p.getVolo() != null) {
            		Volo volo = p.getVolo();
            		if (volo.getRitardo() > 0) {
                        JOptionPane.showMessageDialog(this,
                            "Attenzione: il volo " + volo.getCodice() + " ha un ritardo di " + volo.getRitardo() + " minuti.",
                            "Ritardo volo", JOptionPane.WARNING_MESSAGE);
                    }
                
                    if (volo.isCancellato()) {
                	    JOptionPane.showMessageDialog(this,
                	    	"Attenzione: il volo " + volo.getCodice() + " è stato cancellato.",
                			"Volo cancellato", JOptionPane.ERROR_MESSAGE);
                    }
                }
            	
            	tableModel.addRow(new Object[]{
            		p.getCodicePrenotazione(),
            	    p.getPasseggero().getNomeCompleto(),
                    p.getPasseggero().getDocumento(),
                    p.getVolo() != null ? p.getVolo().getCodice() : "",
                    p.isCancellata() ? "CANCELLATA" : "ATTIVA"
                });
            }
        }
    }
}
         
  
