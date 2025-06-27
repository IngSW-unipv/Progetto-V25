package it.unipv.ingsfw.aerotrack.services;

import java.util.List;
import java.util.stream.Collectors;
import it.unipv.ingsfw.aerotrack.dao.*;
import it.unipv.ingsfw.aerotrack.models.*;

/**
 * Service per la logica di business dei voli.
 * Valida i dati e delega la persistenza al DAO.
 */
public class VoloService {
	
	private VoloDao voloDao;
    private AeroportoDao aeroportoDao;
    
    public VoloService() {
        this.voloDao = VoloDao.getInstance();
        this.aeroportoDao = AeroportoDao.getInstance();
    }
    
    /**
     * Creo un nuovo volo dopo aver validato i dati.
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
        
        Volo volo = new Volo(codice, partenza, destinazione, orario, velocita);
        if (!voloDao.aggiungiVolo(volo)) {
            throw new RuntimeException("Errore nell'inserimento del volo");
        }
    }
    
    /**
     * Cerca un volo per codice.
     */
    public Volo cercaVolo(String codice) {
    	return voloDao.cercaPerCodice(codice);
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

}
