package it.unipv.ingsfw.aerotrack.view;

import it.unipv.ingsfw.aerotrack.models.Aeroporto;
import it.unipv.ingsfw.aerotrack.models.Volo;
import it.unipv.ingsfw.aerotrack.services.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserVoloPanel extends JPanel {
    private final PrenotazioneService prenotazioneService;
    private final VoloService voloService;
    private final AeroportoService aeroportoService;

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final UserPrenotazionePanel prenotazionePanel;
    private final String codiceFiscaleUtente;

    public UserVoloPanel(UserPrenotazionePanel prenotazionePanel, String codiceFiscaleUtente) {
        this.voloService = VoloService.getInstance();
        this.prenotazioneService = PrenotazioneService.getInstance();
        this.aeroportoService = AeroportoService.getInstance();
        this.prenotazionePanel = prenotazionePanel;
        this.codiceFiscaleUtente = codiceFiscaleUtente;

        setLayout(new BorderLayout());

        // Tabella
        tableModel = new DefaultTableModel(new Object[]{"Codice", "Partenza", "Destinazione", "Orario", "Ritardo", "Pista", "Stato"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottoni
        JPanel buttonPanel = new JPanel();
        JButton prenotaBtn = new JButton("Prenota");
        JButton cercaBtn = new JButton("Cerca");
        JButton aggiornaBtn = new JButton("Aggiorna");

        buttonPanel.add(prenotaBtn);
        buttonPanel.add(cercaBtn);
        buttonPanel.add(aggiornaBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Listener prenota
        prenotaBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleziona una riga.");
                return;
            }

            String codice = (String) tableModel.getValueAt(row, 0);
            JTextField nome = new JTextField();
            JTextField cognome = new JTextField();

            Object[] fields = {
                "Nome:", nome,
                "Cognome:", cognome
            };

            int res = JOptionPane.showConfirmDialog(this, fields, "Nuova Prenotazione", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    prenotazioneService.creaPrenotazione(
                            nome.getText().trim(),
                            cognome.getText().trim(),
                            codiceFiscaleUtente,
                            codice
                    );
                    prenotazionePanel.aggiornaTabella();
                    JOptionPane.showMessageDialog(this, "Prenotazione aggiunta!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Listener cerca
        cercaBtn.addActionListener(e -> {
            JComboBox<String> partenzaCombo = new JComboBox<>(
                aeroportoService.getTuttiAeroporti().stream()
                    .map(Aeroporto::getCodice)
                    .toArray(String[]::new)
            );
            partenzaCombo.insertItemAt("", 0);
            partenzaCombo.setSelectedIndex(0);

            JComboBox<String> destinazioneCombo = new JComboBox<>(
                aeroportoService.getTuttiAeroporti().stream()
                    .map(Aeroporto::getCodice)
                    .toArray(String[]::new)
            );
            destinazioneCombo.insertItemAt("", 0);
            destinazioneCombo.setSelectedIndex(0);

            JTextField orarioField = new JTextField();

            Object[] fields = {
                "Aeroporto di partenza:", partenzaCombo,
                "Aeroporto di destinazione:", destinazioneCombo,
                "Orario di partenza:", orarioField
            };

            int res = JOptionPane.showConfirmDialog(this, fields, "Cerca volo", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                String partenza = (String) partenzaCombo.getSelectedItem();
                String destinazione = (String) destinazioneCombo.getSelectedItem();
                String orarioStr = orarioField.getText().trim();
                Double orario = null;
                if (!orarioStr.isEmpty()) {
                    try {
                        orario = Double.parseDouble(orarioStr.replace(",", "."));
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Orario non valido", "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                Double finalOrario = orario;

                List<Volo> risultati = voloService.getTuttiVoli().stream()
                    .filter(v -> partenza.isEmpty() || v.getPartenza().getCodice().equalsIgnoreCase(partenza))
                    .filter(v -> destinazione.isEmpty() || v.getDestinazione().getCodice().equalsIgnoreCase(destinazione))
                    .filter(v -> finalOrario == null ||  (v.getOrarioPartenza() + (v.calcolaTempo() + v.getRitardo())) > finalOrario - (10.0))
                    .toList();


                tableModel.setRowCount(0);
                for (Volo v : risultati) {
                	tableModel.addRow(new Object[]{
                         v.getCodice(),
                         v.getPartenza().getCodice(),
                         v.getDestinazione().getCodice(),
                         v.getOrarioPartenza(),
                         (v.calcolaRitardo() > 0) ? v.getRitardo() : " ",
                         (v.getPistaAssegnata() > 0) ? v.getPistaAssegnata() : "-",
                         v.calcolaStato(orario).name()
                        });
                }

                if (risultati.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nessun volo trovato con i criteri selezionati.");
                }
            }
        });

        // Listener aggiorna
        aggiornaBtn.addActionListener(e -> aggiornaTabella());

        aggiornaTabella();
    }

    private void aggiornaTabella() {
        tableModel.setRowCount(0);
        for (Volo v : voloService.getTuttiVoli()) {
            tableModel.addRow(new Object[]{
                v.getCodice(),
                v.getPartenza().getCodice(),
                v.getDestinazione().getCodice(),
                v.getOrarioPartenza(),
                (v.getRitardo() > 0) ? v.getRitardo() : " ",
                (v.getPistaAssegnata() > 0) ? v.getPistaAssegnata() : "-",
                v.getStato().name()
            });
        }
    }
}
