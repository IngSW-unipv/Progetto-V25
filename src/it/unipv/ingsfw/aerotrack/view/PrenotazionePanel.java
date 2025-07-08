package it.unipv.ingsfw.aerotrack.view;

import it.unipv.ingsfw.aerotrack.models.Prenotazione;
import it.unipv.ingsfw.aerotrack.services.PrenotazioneService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Pannello Swing per la gestione delle prenotazioni.
 */
public class PrenotazionePanel extends JPanel {
    private final PrenotazioneService prenotazioneService;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public PrenotazionePanel() {
        this.prenotazioneService = PrenotazioneService.getInstance();
        setLayout(new BorderLayout());

        // Tabella prenotazioni
        tableModel = new DefaultTableModel(
        		new Object[]{"Codice Prenotazione", "Nome Completo", "Documento", "Codice Volo", "Stato"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
            	return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottoni CRUD
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Aggiungi");
        JButton removeBtn = new JButton("Cancella");
        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Listener aggiungi prenotazione
        addBtn.addActionListener(e -> {
            JTextField nome = new JTextField();
            JTextField cognome = new JTextField();
            JTextField documento = new JTextField();
            JTextField codiceVolo = new JTextField();
            Object[] fields = {
                    "Nome:", nome,
                    "Cognome:", cognome,
                    "Documento:", documento,
                    "Codice volo:", codiceVolo
            };
            int res = JOptionPane.showConfirmDialog(this, fields, "Nuova Prenotazione", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    prenotazioneService.creaPrenotazione(
                            nome.getText().trim(),
                            cognome.getText().trim(),
                            documento.getText().trim(),
                            codiceVolo.getText().trim()
                    );
                    aggiornaTabella();
                    JOptionPane.showMessageDialog(this, "Prenotazione aggiunta!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Listener cancella prenotazione (soft delete: segna come cancellata)
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
                // Non serve salvare di nuovo su DB se la cancellazione è solo in memoria, ma si potrebbe aggiornare anche il DB qui
                aggiornaTabella();
                JOptionPane.showMessageDialog(this, "Prenotazione cancellata!");
            }
        });

        aggiornaTabella();
    }

    public void aggiornaTabella() {
        tableModel.setRowCount(0);
        List<Prenotazione> prenotazioni = prenotazioneService.getTuttePrenotazioni();
        for (Prenotazione p : prenotazioni) {
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