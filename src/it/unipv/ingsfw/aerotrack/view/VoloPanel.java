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

        // Listener per aggiunta e rimozione (implementazione simile ad AeroportoPanel)

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