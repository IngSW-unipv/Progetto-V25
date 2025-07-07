package it.unipv.ingsfw.aerotrack.view;

import it.unipv.ingsfw.aerotrack.models.Volo;
import it.unipv.ingsfw.aerotrack.services.VoloService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Pannello Swing per la gestione dei voli.
 */
public class VoloPanel extends JPanel {
    private final VoloService voloService;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public VoloPanel() {
        this.voloService = VoloService.getInstance();
        setLayout(new BorderLayout());

        // Tabella voli
        tableModel = new DefaultTableModel(new Object[]{"Codice", "Partenza", "Destinazione", "Orario", "Velocità","Pista"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottoni CRUD
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Aggiungi");
        JButton removeBtn = new JButton("Rimuovi");
        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Listener per aggiunta volo
        addBtn.addActionListener(e -> {
            JTextField codice = new JTextField();
            JTextField partenza = new JTextField();
            JTextField destinazione = new JTextField();
            JTextField orario = new JTextField();
            JTextField velocita = new JTextField();
            Object[] fields = {
                "Codice volo:", codice,
                "Codice aeroporto partenza:", partenza,
                "Codice aeroporto destinazione:", destinazione,
                "Orario partenza:", orario,
                "Velocità (km/h):", velocita
            };
            int res = JOptionPane.showConfirmDialog(this, fields, "Nuovo Volo", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    voloService.creaVolo(
                        codice.getText().trim(),
                        partenza.getText().trim(),
                        destinazione.getText().trim(),
                        Double.parseDouble(orario.getText().trim()),
                        Double.parseDouble(velocita.getText().trim())
                    );
                    aggiornaTabella();
                    JOptionPane.showMessageDialog(this, "Volo aggiunto!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
     // Listener rimuovi volo
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String codiceVolo = (String) table.getValueAt(row, 0);
                int conferma = JOptionPane.showConfirmDialog(this, "Sei sicuro di voler rimuovere il volo " + codiceVolo + "?", "Conferma rimozione", JOptionPane.YES_NO_OPTION);
                if (conferma == JOptionPane.YES_OPTION) {
                    try {
                        voloService.rimuoviVolo(codiceVolo);
                        aggiornaTabella();
                        JOptionPane.showMessageDialog(this, "Volo rimosso!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleziona un volo da rimuovere.", "Attenzione", JOptionPane.WARNING_MESSAGE);
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
                v.getVelocita(),
                (v.getPistaAssegnata() >= 0) ? (v.getPistaAssegnata() + 1) : "-"
            });
        }
    }
}