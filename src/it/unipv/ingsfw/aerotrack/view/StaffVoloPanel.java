package it.unipv.ingsfw.aerotrack.view;

import it.unipv.ingsfw.aerotrack.models.Aeroporto;
import it.unipv.ingsfw.aerotrack.models.Volo;
import it.unipv.ingsfw.aerotrack.models.Volo.StatoVolo;
import it.unipv.ingsfw.aerotrack.services.AeroportoService;
import it.unipv.ingsfw.aerotrack.services.VoloService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class StaffVoloPanel extends JPanel {
    private final VoloService voloService;
    private final AeroportoService aeroportoService;
    private final DefaultTableModel model;
    private final JTable table;

    public StaffVoloPanel() {
        setLayout(new BorderLayout());
        voloService = VoloService.getInstance();
        aeroportoService = AeroportoService.getInstance();

        model = new DefaultTableModel(
                new Object[]{"Codice", "Partenza", "Destinazione", "Orario", "Data", "Ritardo", "Pista", "Stato"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                Object pistaObj = getValueAt(row, 6);
                if (pistaObj == null || pistaObj.toString().equals("-1"))
                    c.setBackground(new Color(255, 130, 130));
                else
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 255));
                return c;
            }
        };

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton addBtn = new JButton("Aggiungi");
        JButton delBtn = new JButton("Elimina");
        JButton statoBtn = new JButton("Stato/Ritardo");
        JButton notifyBtn = new JButton("Notifica Utenti");

        buttons.add(addBtn);
        buttons.add(delBtn);
        buttons.add(statoBtn);
        buttons.add(notifyBtn);
        add(buttons, BorderLayout.SOUTH);

        // Listener aggiungi nuovo volo
        addBtn.addActionListener(e -> {
        	JTextField codice = new JTextField();
        	JComboBox<String> partenza = new JComboBox<>(aeroportoService.getTuttiAeroporti().stream().map(Aeroporto::getCodice).toArray(String[]::new));
        	JComboBox<String> destinazione = new JComboBox<>(aeroportoService.getTuttiAeroporti().stream().map(Aeroporto::getCodice).toArray(String[]::new));
        	JTextField orario = new JTextField();
        	JTextField velocita = new JTextField();
        	JTextField data = new JTextField(LocalDate.now().toString());

        	Object[] fields = {
        			"Codice:", codice,
        			"Partenza:", partenza,
        			"Destinazione:", destinazione,
        			"Orario:", orario,
        			"Velocità:", velocita,
        			"Data (YYYY-MM-DD):", data
        	};
        	int res = JOptionPane.showConfirmDialog(this, fields, "Nuovo Volo", JOptionPane.OK_CANCEL_OPTION);
        	if (res == JOptionPane.OK_OPTION) {
        		try {
        			voloService.creaVolo(
                        codice.getText().trim(),
                        partenza.getSelectedItem().toString(),
                        destinazione.getSelectedItem().toString(),
                        LocalTime.parse(orario .getText().trim()),
                        Double.parseDouble(velocita.getText().trim()),
                        LocalDate.parse(data.getText().trim())
        			);
        			aggiornaTabella();
        			JOptionPane.showMessageDialog(this, "Volo aggiunto!");
        		} catch (Exception ex) {
        			JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        		}
        	}
        });
        
        // Listener cancella volo
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleziona un volo da eliminare.");
                return;
            }
            String codice = model.getValueAt(row, 0).toString();
            int conf = JOptionPane.showConfirmDialog(this, "Sicuro di eliminare il volo " + codice + "?", "Conferma", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                if (voloService.rimuoviVolo(codice)) {
                    aggiornaTabella();
                    JOptionPane.showMessageDialog(this, "Volo eliminato.");
                } else {
                    JOptionPane.showMessageDialog(this, "Errore durante la cancellazione.", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Listener per modifica di ritardo e stato di un volo
        statoBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleziona un volo.");
                return;
            }
            String codice = model.getValueAt(row, 0).toString();
            Volo v;
            try {
                v = voloService.cercaVolo(codice);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Volo non trovato.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JTextField ritardo = new JTextField("" + v.getRitardo());
            JComboBox<Volo.StatoVolo> stato = new JComboBox<>(Volo.StatoVolo.values());
            stato.setSelectedItem(v.getStato());
            JTextField dataField = new JTextField(v.getDataVolo().toString());

            Object[] fields = {
                    "Ritardo (minuti):", ritardo,
                    "Stato:", stato,
                    "Data (YYYY-MM-DD):", dataField
            };
            int res = JOptionPane.showConfirmDialog(this, fields, "Modifica stato/ritardo volo", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    LocalTime nuovoRitardo = LocalTime.parse(ritardo.getText().trim());
                    if (voloService.aggiornaStatoERitardo(codice, nuovoRitardo, (Volo.StatoVolo) stato.getSelectedItem())) {
                        aggiornaTabella();
                        JOptionPane.showMessageDialog(this, "Modifiche effettuate!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Errore durante l'aggiornamento.", "Errore", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // Listener per notifiche a passeggeri di un volo
        notifyBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleziona un volo.");
                return;
            }
            String codice = model.getValueAt(row, 0).toString();
            JTextArea msg = new JTextArea(6, 40);
            int res = JOptionPane.showConfirmDialog(this, new JScrollPane(msg), "Messaggio da inviare agli utenti del volo " + codice, JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(this, "Notifica inviata agli utenti del volo " + codice + ":\n\n" + msg.getText());
            }
        });

        aggiornaTabella();
    }

    private void aggiornaTabella() {
        model.setRowCount(0);
        for (Volo v : voloService.getTuttiVoli()) {
        	StatoVolo stato = StatoVolo.ATTERRATO;
        	if(v.getDataVolo().isEqual(LocalDate.now())) stato = v.calcolaStato(LocalTime.now());
        	if(v.getDataVolo().isAfter(LocalDate.now())) stato = StatoVolo.PROGRAMMATO;
            model.addRow(new Object[]{
                v.getCodice(),
                v.getPartenza().getCodice(),
                v.getDestinazione().getCodice(),
                v.getOrarioPartenza(),
                v.getDataVolo(),
                v.getRitardo().toSecondOfDay() > 0 ? (v.getRitardo().toSecondOfDay() / 60) + "'" : "-",
                (v.getPistaAssegnata() >= 0 ? v.getPistaAssegnata(): "-"),
                stato
            });
        }
    }
}