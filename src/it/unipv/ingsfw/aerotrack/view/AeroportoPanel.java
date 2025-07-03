package it.unipv.ingsfw.aerotrack.view;

import it.unipv.ingsfw.aerotrack.models.Aeroporto;
import it.unipv.ingsfw.aerotrack.services.AeroportoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Pannello Swing per la gestione degli aeroporti, con import/export CSV.
 */
public class AeroportoPanel extends JPanel {
    private final AeroportoService aeroportoService;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public AeroportoPanel() {
        this.aeroportoService = AeroportoService.getInstance();

        setLayout(new BorderLayout());

        // Tabella aeroporti
        tableModel = new DefaultTableModel(new Object[]{"Codice", "Nome", "Latitudine", "Longitudine", "Piste"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Bottoni CRUD e CSV
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Aggiungi");
        JButton removeBtn = new JButton("Rimuovi");
        JButton esportaBtn = new JButton("Esporta CSV");
        JButton importaBtn = new JButton("Importa CSV");

        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(esportaBtn);
        buttonPanel.add(importaBtn);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Listener aggiungi aeroporto
        addBtn.addActionListener(e -> {
            JTextField codice = new JTextField();
            JTextField nome = new JTextField();
            JTextField lat = new JTextField();
            JTextField lon = new JTextField();
            JTextField piste = new JTextField();
            Object[] fields = {
                "Codice (3 lettere):", codice,
                "Nome:", nome,
                "Latitudine:", lat,
                "Longitudine:", lon,
                "Numero piste:", piste
            };
            int res = JOptionPane.showConfirmDialog(this, fields, "Nuovo Aeroporto", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    aeroportoService.aggiungiAeroporto(
                        codice.getText().trim(), nome.getText().trim(),
                        Double.parseDouble(lat.getText().trim()),
                        Double.parseDouble(lon.getText().trim()),
                        Integer.parseInt(piste.getText().trim())
                    );
                    aggiornaTabella();
                    JOptionPane.showMessageDialog(this, "Aeroporto aggiunto!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Listener rimuovi aeroporto
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleziona una riga.");
                return;
            }
            String codice = (String) tableModel.getValueAt(row, 0);
            int conf = JOptionPane.showConfirmDialog(this, "Sicuro di voler eliminare " + codice + "?", "Conferma", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                if (aeroportoService.rimuoviAeroporto(codice)) {
                    aggiornaTabella();
                    JOptionPane.showMessageDialog(this, "Aeroporto rimosso!");
                } else {
                    JOptionPane.showMessageDialog(this, "Errore nella rimozione.", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        // Listener esporta
        esportaBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    aeroportoService.esportaAeroportiCSV(fileChooser.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "Esportazione completata!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Errore esportazione: " + ex.getMessage());
                }
            }
        });

        // Listener importa
        importaBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    aeroportoService.importaAeroportiCSV(fileChooser.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "Importazione completata!");
                    aggiornaTabella();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Errore importazione: " + ex.getMessage());
                }
            }
        });

        aggiornaTabella();
    }

    /** Aggiorna la tabella con gli aeroporti attuali */
    private void aggiornaTabella() {
        tableModel.setRowCount(0);
        List<Aeroporto> aeroporti = aeroportoService.getTuttiAeroporti();
        for (Aeroporto a : aeroporti) {
            tableModel.addRow(new Object[]{
                a.getCodice(), a.getNome(), a.getLatitudine(), a.getLongitudine(), a.getNumeroPiste()
            });
        }
    }
}