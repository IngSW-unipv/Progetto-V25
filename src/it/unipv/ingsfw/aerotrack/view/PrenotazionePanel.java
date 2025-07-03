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
        tableModel = new DefaultTableModel(new Object[]{"Codice Prenotazione", "Nome", "Cognome", "Documento", "Codice Volo", "Stato"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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

        // Listener per aggiunta e cancellazione (implementazione simile ad AeroportoPanel)

        aggiornaTabella();
    }

    private void aggiornaTabella() {
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