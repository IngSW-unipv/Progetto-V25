package it.unipv.ingsfw.aerotrack.view;

import it.unipv.ingsfw.aerotrack.models.Volo;
import it.unipv.ingsfw.aerotrack.services.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Pannello Swing per la gestione dei voli.
 */

public class VoloPanel extends JPanel {
    private final PrenotazioneService prenotazioneService;
    private final VoloService voloService;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final PrenotazionePanel prenotazionePanel; 

    public VoloPanel(PrenotazionePanel prenotazionePanel) {
        this.voloService = VoloService.getInstance();
        this.prenotazioneService = PrenotazioneService.getInstance();
        this.prenotazionePanel = prenotazionePanel; 

        setLayout(new BorderLayout());

        // Tabella voli
        tableModel = new DefaultTableModel(new Object[]{"Codice", "Partenza", "Destinazione", "Orario", "Ritardo", "Pista"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

     // Bottoni CRUD e CSV
        JPanel buttonPanel = new JPanel();
        JButton prenotaBtn = new JButton("Prenota");
        JButton cercaBtn = new JButton("Cerca");

        buttonPanel.add(prenotaBtn);
        buttonPanel.add(cercaBtn);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Listener prenota volo
        prenotaBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleziona una riga.");
                return;
            }
            String codice = (String) tableModel.getValueAt(row, 0);
            
            JTextField nome = new JTextField();
            JTextField cognome = new JTextField();
            JTextField documento = new JTextField();
            Object[] fields = {
                    "Nome:", nome,
                    "Cognome:", cognome,
                    "Documento:", documento,
            };
            int res = JOptionPane.showConfirmDialog(this, fields, "Nuova Prenotazione", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    prenotazioneService.creaPrenotazione(
                            nome.getText().trim(),
                            cognome.getText().trim(),
                            documento.getText().trim(),
                            codice

                    );
                    prenotazionePanel.aggiornaTabella();
                    JOptionPane.showMessageDialog(this, "Prenotazione aggiunta!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }

        });
        
        // Listener cerca volo per partenza e destinazione
        cercaBtn.addActionListener(e -> {
            JTextField partenza = new JTextField();
            JTextField destinazione = new JTextField();
            Object[] fields = {
                "Codice aeroporto partenza:", partenza,
                "Codice aeroporto destinazione:", destinazione,
            };
            int res = JOptionPane.showConfirmDialog(this, fields, "Cerca volo", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                String p = partenza.getText().trim();
                String d = destinazione.getText().trim();

                List<Volo> risultati;

                if (p.isEmpty() && d.isEmpty()) {
                    // Nessun filtro: mostra tutti i voli
                    risultati = voloService.getTuttiVoli();
                } else if (!p.isEmpty() && !d.isEmpty()) {
                    // Filtra per partenza e destinazione
                    risultati = voloService.trovaVoliPerPartenza(p).stream()
                        .filter(v -> v.getDestinazione().getCodice().equalsIgnoreCase(d))
                        .toList();
                } else if (!p.isEmpty()) {
                    // Solo partenza
                    risultati = voloService.trovaVoliPerPartenza(p);
                } else {
                    // Solo destinazione
                    risultati = voloService.trovaVoliPerDestinazione(d);
                }

                // Aggiorna la tabella con i risultati
                tableModel.setRowCount(0);
                for (Volo v : risultati) {
                    tableModel.addRow(new Object[]{
                        v.getCodice(), v.getPartenza().getCodice(),
                        v.getDestinazione().getCodice(),
                        v.getOrarioPartenza(), v.getRitardo(), v.getPistaAssegnata()
                    });
                }
            }
        });
        aggiornaTabella();
    }

    private void aggiornaTabella() {
        tableModel.setRowCount(0);
        List<Volo> voli = voloService.getTuttiVoli();
        for (Volo v : voli) {
            tableModel.addRow(new Object[]{
                v.getCodice(),
                v.getPartenza().getCodice(),
                v.getDestinazione().getCodice(),
                v.getOrarioPartenza(),
                (v.getRitardo() > 0) ? (v.getRitardo()) : " ",
                (v.getPistaAssegnata() >= 0) ? (v.getPistaAssegnata()) : "-"
            });
        }
    }
}