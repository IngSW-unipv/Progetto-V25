package it.unipv.ingsfw.aerotrack.view;

import it.unipv.ingsfw.aerotrack.models.Prenotazione;
import it.unipv.ingsfw.aerotrack.services.PrenotazioneService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

public class StaffPrenotazionePanel extends JPanel {
    private final PrenotazioneService prenotazioneService;
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField searchField;

    public StaffPrenotazionePanel() {
        setLayout(new BorderLayout());
        prenotazioneService = PrenotazioneService.getInstance();

        model = new DefaultTableModel(
            new Object[]{"Codice Prenotazione", "Nome", "Cognome", "Codice Fiscale", "Codice Volo", "Stato"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Cerca:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);
       
        // Bottoni
        JPanel buttonPanel = new JPanel();
        JButton aggiornaBtn = new JButton("Aggiorna");

        buttonPanel.add(aggiornaBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { aggiornaTabella(); }
        });
        
        // Listener aggiorna
        aggiornaBtn.addActionListener(e -> aggiornaTabella());

        aggiornaTabella();
    }

    private void aggiornaTabella() {
        String q = searchField.getText().toLowerCase();
        model.setRowCount(0);
        List<Prenotazione> prenotazioni = prenotazioneService.getTuttePrenotazioni().stream()
            .filter(p -> p.getCodicePrenotazione().toLowerCase().contains(q)
                || p.getPasseggero().getNome().toLowerCase().contains(q)
                || p.getPasseggero().getCognome().toLowerCase().contains(q)
                || p.getPasseggero().getDocumento().toLowerCase().contains(q)
                || (p.getVolo()!=null && p.getVolo().getCodice().toLowerCase().contains(q)))
            .collect(Collectors.toList());
        for (Prenotazione p : prenotazioni) {
            model.addRow(new Object[]{
                p.getCodicePrenotazione(),
                p.getPasseggero().getNome(),
                p.getPasseggero().getCognome(),
                p.getPasseggero().getDocumento(),
                (p.getVolo()!=null ? p.getVolo().getCodice() : ""),
                p.isCancellata() ? "CANCELLATA" : "ATTIVA"
            });
        }
    }
}