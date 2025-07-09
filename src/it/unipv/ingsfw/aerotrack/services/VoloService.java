package it.unipv.ingsfw.aerotrack.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;
import it.unipv.ingsfw.aerotrack.dao.*;
import it.unipv.ingsfw.aerotrack.models.*;

/**
 * Service Singleton per la logica di business dei voli.
 * Valida i dati e delega la persistenza al DAO.
 */
public class VoloService {
	
	private static VoloService instance;
	private final VoloDao voloDao;
    private final AeroportoDao aeroportoDao;
    
    private VoloService() {
        this.voloDao = VoloDao.getInstance();
        this.aeroportoDao = AeroportoDao.getInstance();
    }
    
    public static VoloService getInstance() {
        if (instance == null) instance = new VoloService();
        return instance;
    }
    
    public void svuotaVoli() {
        try (Connection conn = DBConnection.startConnection("aerotrack");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM voli");
            System.out.println("Tabella voli svuotata.");
        } catch (SQLException e) {
            System.err.println("Errore durante lo svuotamento della tabella voli: " + e.getMessage());
        }
    }
    
    /**
     * Crea un nuovo volo dopo aver validato i dati.
     * 
     * @throws IllegalArgumentException se i dati non sono validi
     */
    public void creaVolo(String codice, String codicePartenza, String codiceDestinazione, double orario, double velocita) {
        // Validazioni
        if (codice == null || codice.isEmpty()) {
            throw new IllegalArgumentException("Codice volo non può essere vuoto");
        }
        if (codicePartenza.equals(codiceDestinazione)) {
            throw new IllegalArgumentException("Aeroporto di partenza e destinazione non possono essere uguali");
        }
        if (velocita <= 0 || velocita > 1200) {
            throw new IllegalArgumentException("Velocità deve essere tra 1 e 1200 km/h");
        }
        Aeroporto partenza = aeroportoDao.cercaPerCodice(codicePartenza);
        Aeroporto destinazione = aeroportoDao.cercaPerCodice(codiceDestinazione);
        if (partenza == null || destinazione == null) {
            throw new IllegalArgumentException("Aeroporto di partenza o destinazione non trovato");
        }
        
        // Trova la prima pista libera nell'aeroporto di partenza in quell'orario
        int pistaLiberaPartenza = -1;
        for (int i = 0; i < partenza.getNumeroPiste(); i++) {
            if (partenza.getPiste()[i] == null) {
                pistaLiberaPartenza = i;
                break;
            }
        }
        
        // Trova la prima pista libera nell'aeroporto di destinazione in quell'orario
        int pistaLiberaDestinazione = -1;
        for (int i = 0; i < destinazione.getNumeroPiste(); i++) {
            if (destinazione.getPiste()[i] == null) {
            	pistaLiberaDestinazione= i;
                break;
            }
        }
        
        Volo volo;
        if (pistaLiberaPartenza != -1) {
        	if (pistaLiberaDestinazione != -1) {
        		volo = new Volo(codice, partenza, destinazione, orario, velocita, pistaLiberaPartenza, 0, Volo.StatoVolo.PROGRAMMATO);
                partenza.occupaPista(pistaLiberaPartenza, volo); // Aggiorna lo stato in memoria
                destinazione.occupaPista(pistaLiberaDestinazione, volo); // Aggiorna lo stato in memoria
        	} else {
                // Nessuna pista libera a destinazione: stato IN_VOLO e calcolo ritardo
                volo = new Volo(codice, partenza, destinazione, orario, velocita, pistaLiberaPartenza, 0, Volo.StatoVolo.PROGRAMMATO);
                volo.setRitardo(volo.calcolaRitardo());
                partenza.occupaPista(pistaLiberaPartenza, volo); // Aggiorna lo stato in memoria
        	}
        } else {
            // Nessuna pista libera in partenza: stato IN_ATTESA e calcolo ritardo
            volo = new Volo(codice, partenza, destinazione, orario, velocita, -1, 0, Volo.StatoVolo.PROGRAMMATO);
            volo.setRitardo(volo.calcolaRitardo());
        }

        if (!voloDao.aggiungiVolo(volo)) {
            throw new RuntimeException("Errore nell'inserimento del volo");
        }
    }
     
    /**
     * Cerca un volo per codice.
     */
    public Volo cercaVolo(String codice) {
        Volo v = voloDao.cercaPerCodice(codice);
        if (v == null) throw new VoloNonTrovatoException(codice);
        return v;
    }
     
    
    /**
     * Restituisce tutti i voli.
     */
    public List<Volo> getTuttiVoli() {
        return voloDao.getTuttiVoli();
    }
    
    /**
     * Trova voli per aeroporto di partenza.
     */
    public List<Volo> trovaVoliPerPartenza(String codiceAeroporto) {
        return voloDao.getTuttiVoli().stream()
                .filter(volo -> volo.getPartenza().getCodice().equalsIgnoreCase(codiceAeroporto))
                .collect(Collectors.toList());
    }
    
    /**
     * Trova voli per aeroporto di destinazione.
     */
    public List<Volo> trovaVoliPerDestinazione(String codiceAeroporto) {
        return voloDao.getTuttiVoli().stream()
                .filter(volo -> volo.getDestinazione().getCodice().equalsIgnoreCase(codiceAeroporto))
                .collect(Collectors.toList());
    }
   
    /**
     * Rimuove un volo dal sistema.
     */
    public boolean rimuoviVolo(String codice) {
        return voloDao.rimuoviVolo(codice);
    }
    
    /** Eccezione custom */
    public static class VoloNonTrovatoException extends RuntimeException {
        public VoloNonTrovatoException(String codice) {
            super("Volo non trovato: " + codice);
        }
    }
    
}