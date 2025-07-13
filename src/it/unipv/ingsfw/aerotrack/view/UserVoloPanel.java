package it.unipv.ingsfw.aerotrack.view;

import it.unipv.ingsfw.aerotrack.models.Aeroporto;


import it.unipv.ingsfw.aerotrack.models.Volo;
import it.unipv.ingsfw.aerotrack.models.Volo.StatoVolo;
import it.unipv.ingsfw.aerotrack.services.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.*;
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
        tableModel = new DefaultTableModel(new Object[]{"Codice", "Partenza", "Destinazione", "Orario", "Ritardo", "Stato"}, 0) {
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
            Volo volo = voloService.cercaVolo(codice);

            if (volo.getDataVolo().isBefore(LocalDate.now()) || (volo.getDataVolo().isEqual(LocalDate.now())) && (volo.getOrarioPartenza().isBefore(LocalTime.now()))) {
                JOptionPane.showMessageDialog(this, "Il volo non è più prenotabile.");
                return;
            }
            
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

            JTextField orarioField = new JTextField(LocalTime.now().withSecond(0).withNano(0).toString());
            JTextField dataField = new JTextField(LocalDate.now().toString());

            Object[] fields = {
                "Aeroporto di partenza:", partenzaCombo,
                "Aeroporto di destinazione:", destinazioneCombo,
                "Orario di riferimento (HH:mm):", orarioField,
                "Data di riferimento (year-month-date):", dataField
            };

            int res = JOptionPane.showConfirmDialog(this, fields, "Cerca volo", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            String partenza = (String) partenzaCombo.getSelectedItem();
            String destinazione = (String) destinazioneCombo.getSelectedItem();
            String orarioStr = orarioField.getText().trim();
            String dataStr = dataField.getText().trim();

            LocalTime orario = null;
            LocalDate data = null;
            if (!orarioStr.isEmpty()) {
                try {
                    orario = LocalTime.parse(orarioStr);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Orario non valido (usa formato HH:mm)", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
            }	
            
            if (!dataStr.isEmpty()) {
            	try {
            		data = LocalDate.parse(dataStr);
            	} catch (Exception ex) {
            		JOptionPane.showMessageDialog(this, "Data non valido (usa formato year-month-date)", "Errore", JOptionPane.ERROR_MESSAGE);
            		return;
            	}    
            }

            LocalTime finalOrario = orario;
            LocalDate finalData = data;

            List<Volo> risultati = voloService.getTuttiVoli().stream()
            	    .filter(v -> {
            	        if (partenza != null && !partenza.isEmpty() && !v.getPartenza().getCodice().equals(partenza)) {
            	            return false;
            	        }
            	        if (destinazione != null && !destinazione.isEmpty() && !v.getDestinazione().getCodice().equals(destinazione)) {
            	            return false;
            	        }

            	        if (finalOrario != null && finalData != null) {
            	            LocalDateTime riferimento = LocalDateTime.of(finalData, finalOrario);
            	            LocalDateTime partenzaReale = LocalDateTime.of(v.getDataVolo(), v.getOrarioPartenza())
            	                .plusMinutes(v.getRitardo().toSecondOfDay() / 60);
            	            LocalDateTime fineAtterraggio = partenzaReale.plusMinutes(30 + v.calcolaTempo() + 30);

            	            if (riferimento.isAfter(fineAtterraggio)) {
            	                return false;
            	            }
            	        }

            	        return true;
            	    })
            	    .toList();

            tableModel.setRowCount(0);

            for (Volo v : risultati) {
            	StatoVolo stato = StatoVolo.ATTERRATO;
            	if(v.getDataVolo().isEqual(LocalDate.now())) stato = v.calcolaStato(LocalTime.now());

                tableModel.addRow(new Object[]{
                    v.getCodice(),
                    v.getPartenza().getCodice(),
                    v.getDestinazione().getCodice(),
                    v.getOrarioPartenza(),
                    v.getRitardo().toSecondOfDay() > 0 ? (v.getRitardo().toSecondOfDay() / 60) + "'" : "-",
                    stato
                });
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Nessun volo trovato con i criteri selezionati.");
            }
        });


        // Listener aggiorna
        aggiornaBtn.addActionListener(e -> aggiornaTabella());

        aggiornaTabella();
    }
    
    private void aggiornaTabella() {
        LocalTime orario = LocalTime.now();

        tableModel.setRowCount(0);
        for (Volo v : voloService.getTuttiVoli()) {
            StatoVolo stato = StatoVolo.PROGRAMMATO;
            
            if (!v.getDataVolo().equals(LocalDate.now())) continue;

            int ritardoMin = v.getRitardo().toSecondOfDay() / 60;
            LocalTime partenzaReale = v.getOrarioPartenza().plusMinutes(ritardoMin);
            LocalTime finePartenza = partenzaReale.plusMinutes(30);
            LocalTime fineVolo = finePartenza.plusMinutes(v.calcolaTempo());
            LocalTime fineAtterraggio = fineVolo.plusMinutes(30);

            if(v.getRitardo().isAfter(LocalTime.of(2, 0))) {
            	stato = StatoVolo.CANCELLATO;
            } else {
            	 if (orario.isAfter(v.getOrarioPartenza()) && orario.isBefore(partenzaReale)) {
                     stato = StatoVolo.IN_ATTESA;
                 } else if (!orario.isBefore(partenzaReale) && orario.isBefore(finePartenza)) {
                     stato = StatoVolo.IN_PARTENZA;
                 } else if (!orario.isBefore(finePartenza) && orario.isBefore(fineVolo)) {
                     stato = StatoVolo.IN_VOLO;
                 } else if (!orario.isBefore(fineVolo) && orario.isBefore(fineAtterraggio)) {
                     stato = StatoVolo.ATTERRATO;
                 } else if (orario.isAfter(fineAtterraggio)) {
                     continue; // Escludi volo atterrato da più di 30 minuti
                 }
            }
           
            tableModel.addRow(new Object[]{
                v.getCodice(),
                v.getPartenza().getCodice(),
                v.getDestinazione().getCodice(),
                v.getOrarioPartenza(),
                v.getRitardo().toSecondOfDay() > 0 ? (ritardoMin) + "'" : "-",
                stato
            });
        }
    }

}
